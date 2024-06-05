package com.realtime.protection.server.task.status;

import com.realtime.protection.configuration.entity.task.DynamicTaskInfo;
import com.realtime.protection.configuration.entity.task.Task;
import com.realtime.protection.configuration.entity.task.TaskCommandInfo;
import com.realtime.protection.configuration.response.SimpleResponse;
import com.realtime.protection.configuration.utils.enums.TaskTypeEnum;
import com.realtime.protection.configuration.utils.enums.audit.AuditStatusEnum;
import com.realtime.protection.server.command.CommandService;
import com.realtime.protection.server.task.TaskService;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.*;
import reactor.core.publisher.Mono;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class StateHandler {


    private final WebClient client = WebClient.builder()
            .baseUrl("http://192.168.107.49:8088")
//            .baseUrl("http://10.58.72.151:8088")
            .build();

    protected Boolean handleStart(TaskService taskService, CommandService commandService, Long taskId) {
        Task task = taskService.queryTask(taskId);

        if (task == null) {
            throw new IllegalArgumentException("无效task_id，因为无法找到对应任务");
        }

        Integer taskAuditStatus = task.getTaskAuditStatus();

        if (taskAuditStatus == null) {
            throw new IllegalArgumentException("无效的task_id，因为task_audit_status为空");
        }

        // 如果审核状态不为已通过审核，则报错
        if (!taskAuditStatus.equals(AuditStatusEnum.AUDITED.getNum())) {
            throw new IllegalArgumentException("无效的task_id，因为未通过审核");
        }

        return switch (TaskTypeEnum.getTaskTypeByNum(task.getTaskType())) {
            case STATIC -> handleStaticTaskStart(commandService, taskService, task);
            case DYNAMIC -> handleDynamicTaskStart(taskService, task);
            case JUDGED -> handleJudgedTaskStart(taskService, task);
        };
    }

    protected Boolean handleResume(CommandService commandService, Long taskId) {
        commandService.startCommandsByTaskId(taskId);
        return true;
    }

    protected Boolean handlePause(CommandService commandService, Long taskId) {
        commandService.stopCommandsByTaskId(taskId);
        return true;
    }

    protected Boolean handleStop(CommandService commandService, TaskService taskService, Long taskId) {
        commandService.removeCommandsByTaskId(taskId);
        taskService.updateDynamicRuleAuditStatusInTask(taskId, AuditStatusEnum.AUDITED);
        // 要删去规则的used_task_id,因为在新建时检查了是否有重复使用的规则
        taskService.removeDynamicRuleUsedTaskIdInTask(taskId);

        taskService.updateStaticRuleAuditStatusInTask(taskId, AuditStatusEnum.AUDITED);
        taskService.removeStaticRuleUsedTaskIdInTask(taskId);

        return true;
    }

    protected Boolean handleFinish(CommandService commandService, TaskService taskService, Long taskId) {
        commandService.removeCommandsByTaskId(taskId);

        taskService.updateDynamicRuleAuditStatusInTask(taskId, AuditStatusEnum.AUDITED);
        // 要删去规则的used_task_id,因为在新建时检查了是否有重复使用的规则
        taskService.removeDynamicRuleUsedTaskIdInTask(taskId);

        taskService.updateStaticRuleAuditStatusInTask(taskId, AuditStatusEnum.AUDITED);
        taskService.removeStaticRuleUsedTaskIdInTask(taskId);


        return true;
    }

    protected Boolean handleFailed(CommandService commandService, TaskService taskService, Long taskId) {
        commandService.removeCommandsByTaskId(taskId);
        taskService.updateDynamicRuleAuditStatusInTask(taskId, AuditStatusEnum.AUDITED);
        // 要删去规则的used_task_id,因为在新建时检查了是否有重复使用的规则
        taskService.removeDynamicRuleUsedTaskIdInTask(taskId);
        taskService.updateStaticRuleAuditStatusInTask(taskId, AuditStatusEnum.AUDITED);
        taskService.removeStaticRuleUsedTaskIdInTask(taskId);
        return true;
    }

    // 如果是实时任务或者研判后处置任务，那么就需要在任务启动之后，立刻向动态规则中指定的系统发送日志筛选请求。
    // 筛选完成后，系统返回日志，需要由接收端点提取字段，并且合成一条静态规则，再按照任务开始时间、结束时间和任务类型进行指令创建
    private Boolean handleJudgedTaskStart(TaskService taskService, Task task) {
        return sendFilters(taskService, task);
    }

    private Boolean handleDynamicTaskStart(TaskService taskService, Task task) {
        // 将所有关联的动态规则审批状态修改为“已使用”
        taskService.updateDynamicRuleAuditStatusInTask(task.getTaskId(), AuditStatusEnum.USING);

        return sendFilters(taskService, task);
//        return true;
    }

    private Boolean handleStaticTaskStart(CommandService commandService, TaskService taskService, Task task) {
        // 如果未能获取staticTaskCommandInfos，需要报错
        List<TaskCommandInfo> staticTaskCommandInfos = taskService.getStaticCommandInfos(task.getTaskId());
        if (staticTaskCommandInfos == null || staticTaskCommandInfos.isEmpty()) {
            throw new IllegalArgumentException("静态规则列表为空，请至少选择一个静态规则以启动任务");
        }

        // 将所有关联的静态规则全部设置为已使用状态
        taskService.updateStaticRuleAuditStatusInTask(task.getTaskId(), AuditStatusEnum.USING);
//        taskService.changeTaskAuditStatus(task.getTaskId(), AuditStatusEnum.USING.getNum());

        commandService.createCommands(staticTaskCommandInfos);
        return true;
    }

    private Boolean sendFilters(TaskService taskService, Task task) {
        List<DynamicTaskInfo> dynamicTaskInfos = taskService.getDynamicTaskInfos(task.getTaskId());

        if (dynamicTaskInfos == null || dynamicTaskInfos.isEmpty()) {
            throw new IllegalArgumentException("动态规则列表为空，请至少选择一个动态规则以启动动态/研判后类型任务");
        }
        //解析sql
        dynamicTaskInfos.forEach(dynamicTaskInfo -> {
            dynamicTaskInfo.parseSql();
        });

//        // 将所有关联的动态规则审批状态修改为“已使用”
//        taskService.updateDynamicRuleAuditStatusInTask(task.getTaskId(), AuditStatusEnum.USING);

        AtomicReference<Boolean> success = new AtomicReference<>(false);

//        System.out.println(dynamicTaskInfos);

        Mono<SimpleResponse> mono = client.post()
                .uri("/api/v1/kafkasend")
                .bodyValue(dynamicTaskInfos)
                .exchangeToMono(res -> {
                    if (res.statusCode().equals(HttpStatus.OK)) {
                        return res.bodyToMono(SimpleResponse.class);
                    }

                    return res.createError();
                })
                .doOnError(WebClientResponseException.class, res -> success.set(false));


        SimpleResponse response = mono.block(Duration.ofSeconds(5));

        if (response == null || response.getCode() != 200 || response.getSuccess() == null) {
            return false;
        }

        success.set(response.getSuccess());

        return success.get();
    }
}

