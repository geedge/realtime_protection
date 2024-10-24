package com.realtime.protection.server.task.status;

import com.realtime.protection.configuration.entity.task.DynamicTaskInfo;
import com.realtime.protection.configuration.entity.task.Task;
import com.realtime.protection.configuration.entity.task.TaskCommandInfo;
import com.realtime.protection.configuration.response.SimpleResponse;
import com.realtime.protection.configuration.utils.enums.StateEnum;
import com.realtime.protection.configuration.utils.enums.TaskTypeEnum;
import com.realtime.protection.configuration.utils.enums.audit.AuditStatusEnum;
import com.realtime.protection.server.command.CommandService;
import com.realtime.protection.server.task.TaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.*;
import reactor.core.publisher.Mono;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Component
public class StateHandler {


    private final WebClient client = WebClient.builder()
//            .baseUrl("http://192.168.107.49:8088")
            .baseUrl("http://10.58.72.146:8088")
            .build();

    //http://10.26.22.123:17011/rule
    private final WebClient client_commandDistribute = WebClient.builder()
//            .baseUrl("http://10.26.22.123:17011")
            .baseUrl("http://10.58.72.151:17011")
            .build();

    private final WebClient client_RCPquery = WebClient.builder()
            .baseUrl("http://10.58.72.140:8088")
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
        /*
       什么时候会调用这个函数呢，
         1是周期函数判断出来当前时间超过了任务开始时间，且运行状态处于PENDING，审批状态为AUDITED
         2是走http接口调用，这时候有可能是任务开始时间已经过了，也有可能是任务开始时间还没到
            任务开始时间已经过了，周期性函数已经执行转变为RUNNNING状态了，前端再请求启动。 会报错
            所以只能是任务开始时间还没到，这时候“提前启动”就要设置任务开始时间为当前时间
         */
        if (task.getTaskStartTime().isAfter(LocalDateTime.now())) {
            taskService.updateTaskStartTime(taskId);
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

    protected Boolean handleRunningSuccess(CommandService commandService, TaskService taskService, Long taskId) {
        //查询任务状态
        Integer taskStatusNum = taskService.queryTaskStatus(taskId);

        if (taskStatusNum == StateEnum.RUNNING_SUCCESS.getStateNum()) {
            return true;
        } else {
            //修改任务状态
            taskService.changeTaskStatus(taskId, StateEnum.RUNNING_SUCCESS.getStateNum());
            //插入历史表
            taskService.insertTaskStatusLog(taskId);
        }
        return true;
    }

    protected Boolean handleRunningPartialSuccess(CommandService commandService, TaskService taskService, Long taskId) {
        ///查询任务状态
        Integer taskStatusNum = taskService.queryTaskStatus(taskId);

        if (taskStatusNum == StateEnum.RUNNING_PARTIAL_SUCCESS.getStateNum()) {
            return true;
        } else {
            //修改任务状态
            taskService.changeTaskStatus(taskId, StateEnum.RUNNING_PARTIAL_SUCCESS.getStateNum());
            //插入历史表
            taskService.insertTaskStatusLog(taskId);
        }
        return true;
    }

    protected Boolean handleRunningFail(CommandService commandService, TaskService taskService, Long taskId) {
        ///查询任务状态
        Integer taskStatusNum = taskService.queryTaskStatus(taskId);

        if (taskStatusNum == StateEnum.RUNNING_FAILED.getStateNum()) {
            return true;
        } else {
            //修改任务状态
            taskService.changeTaskStatus(taskId, StateEnum.RUNNING_FAILED.getStateNum());
            //插入历史表
            taskService.insertTaskStatusLog(taskId);
        }
        return true;
    }

    protected Boolean handleAllFail(CommandService commandService, TaskService taskService, Long taskId) {
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
        // 将所有关联的动态规则审批状态修改为“已使用”
        taskService.updateDynamicRuleAuditStatusInTask(task.getTaskId(), AuditStatusEnum.USING);
        try {
            sendFilters(taskService, task);
        } catch (Exception e) {
            log.error(String.format("研判后任务%d 发送筛选条件出错", task.getTaskId()));
            log.error(e.getMessage());
            return true;
        }
        return sendFilters(taskService, task);
//        return true;
    }

    private Boolean handleDynamicTaskStart(TaskService taskService, Task task) {
        // 将所有关联的动态规则审批状态修改为“已使用”
        taskService.updateDynamicRuleAuditStatusInTask(task.getTaskId(), AuditStatusEnum.USING);
        try {
            sendFilters(taskService, task);
        } catch (Exception e) {
            log.error(String.format("动态任务%d 发送筛选条件出错", task.getTaskId()));
            log.error(e.getMessage());
            return true;
        }
//        return sendFilters(taskService, task);

        return true;
    }

    private Boolean handleStaticTaskStart(CommandService commandService, TaskService taskService, Task task) {
        // 如果是ddos的任务，则不用对指令进行判断，直接启动就好
        if ("ddos".equals(task.getTaskSource())) {
            return true;
        }
        // 如果未能获取staticTaskCommandInfos，需要报错
        List<TaskCommandInfo> staticTaskCommandInfos = taskService.getStaticCommandInfos(task.getTaskId());
        //判断有没有多局点


        if (staticTaskCommandInfos == null || staticTaskCommandInfos.isEmpty()) {
            throw new IllegalArgumentException("静态规则列表为空，请至少选择一个静态规则以启动任务");
        }
        //指令拆分局点，拆分后的指令放入staticTaskCommandInfosSplitDistributePoint
        List<TaskCommandInfo> staticTaskCommandInfosSplitDistributePoint = new ArrayList<>();
        for (TaskCommandInfo taskCommandInfo : staticTaskCommandInfos) {
            //判断是否多局点
            if (taskCommandInfo.getDistributePoint().contains(",")) {
                String commandGroup = UUID.randomUUID().toString();
                List<String> distributePointArray = Arrays.asList(taskCommandInfo.getDistributePoint().split(","));
                for (String distributePoint : distributePointArray) {
                    TaskCommandInfo taskCommandInfoSplit = new TaskCommandInfo();
                    taskCommandInfoSplit.copyTaskCommandInfo(taskCommandInfo);
                    taskCommandInfoSplit.setDistributePoint(distributePoint);
                    taskCommandInfoSplit.setCommandGroup(commandGroup);
                    staticTaskCommandInfosSplitDistributePoint.add(taskCommandInfoSplit);
                }
            } else {
                staticTaskCommandInfosSplitDistributePoint.add(taskCommandInfo);
            }
        }

        // 将所有关联的静态规则全部设置为已使用状态
//        taskService.updateStaticRuleAuditStatusInTask(task.getTaskId(), AuditStatusEnum.USING);
//        taskService.changeTaskAuditStatus(task.getTaskId(), AuditStatusEnum.USING.getNum());

        List<String> commandUUIDs = commandService.createCommands(staticTaskCommandInfosSplitDistributePoint);

        // 将command新建信号发送到c3下发程序
        try {
            sendCommandDistributeSignal(commandUUIDs);
        } catch (Exception e) {
            log.error(String.format("静态任务%d 首次指令下发c3出错", task.getTaskId()));
            e.printStackTrace();
            // 将command查询RCP信号发送到RCP查询程序
            try {
                sendCommandRcpQuerySignal(commandUUIDs);
            } catch (Exception e2) {
                log.error(String.format("静态任务%d 首次指令查询RCP出错", task.getTaskId()));
                e.printStackTrace();
                return true;
            }
        }

        // 将command查询RCP信号发送到RCP查询程序
        try {
            sendCommandRcpQuerySignal(commandUUIDs);
        } catch (Exception e) {
            log.error(String.format("静态任务%d 首次指令查询RCP出错", task.getTaskId()));
            return true;
        }


        return true;
    }

    public Boolean sendCommandDistributeSignal(List<String> commandUUIDs) {

        List<Map<String, String>> commandIDMaps = new ArrayList<>();
        for (String commandUUID : commandUUIDs) {
            commandIDMaps.add(Map.of("COMMAND_ID", commandUUID));
        }

        AtomicReference<Boolean> success = new AtomicReference<>(false);


        Mono<Map> mono = client_commandDistribute.post()
                .uri("/rule")
                .bodyValue(commandIDMaps)
                .accept(MediaType.APPLICATION_JSON)
                .exchangeToMono(res -> {
                    if (res.statusCode().equals(HttpStatus.OK)) {
                        return res.bodyToMono(Map.class);
                    }
                    return res.createError();
                })
                .doOnError(WebClientResponseException.class, res -> success.set(false));

        // 异步处理响应
        mono.subscribe(
                response2 -> {
                    Map<String, Integer> response = response2;
                    success.set(false);
                    response.forEach((commandUUID, responseCode) -> {
                        log.info("指令首次下发成功, 指令uuid: " + commandUUID + ", responseCode: " + responseCode);
                        if (responseCode != 0) {
                            log.warn("指令首次下发失败, 指令uuid: " + commandUUID + ", responseCode: " + responseCode);
                        }
                    });
                    success.set(true);
                },
                error -> {
                    // 错误响应处理
                    System.err.println("指令首次下发错误: " + error.getMessage());
                    success.set(false);
                }
        );
//        Map<String, Integer> response = mono.block(Duration.ofSeconds(5));
//
//        if (response == null) {
//            return false;
//        }
//        response.forEach((commandUUID, responseCode) -> {
//            log.info("指令首次下发成功, 指令uuid: " + commandUUID + ", responseCode: " + responseCode);
//            if (responseCode != 0) {
//                log.warn("指令首次下发失败, 指令uuid: " + commandUUID + ", responseCode: " + responseCode);
//            }
//        });
//
//        success.set(true);

        return success.get();
    }


    public Boolean sendCommandRcpQuerySignal(List<String> commandUUIDs) {

        List<Map<String, String>> commandIDMaps = new ArrayList<>();
        for (String commandUUID : commandUUIDs) {
            commandIDMaps.add(Map.of("COMMAND_ID", commandUUID));
        }

        AtomicReference<Boolean> success = new AtomicReference<>(false);


        Mono<Map> mono = client_RCPquery.post()
                .uri("/command")
                .bodyValue(commandIDMaps)
                .accept(MediaType.APPLICATION_JSON)
                .exchangeToMono(res -> {
                    if (res.statusCode().equals(HttpStatus.OK)) {
                        return res.bodyToMono(Map.class);
                    }
                    return res.createError();
                })
                .doOnError(WebClientResponseException.class, res -> success.set(false));


//        Map<String, Integer> response = mono.block(Duration.ofSeconds(5));

        // 异步处理响应
        mono.subscribe(
                response -> {
                    // 成功响应处理
                    System.out.println("响应: " + response);
                    success.set(true);
                },
                error -> {
                    // 错误响应处理
                    System.err.println("错误: " + error.getMessage());
                    success.set(false);
                }
        );


        success.set(true);

        return success.get();
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

