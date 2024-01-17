package com.realtime.protection.server.task.status;

import com.realtime.protection.configuration.entity.task.Task;
import com.realtime.protection.configuration.entity.task.TaskCommandInfo;
import com.realtime.protection.configuration.exception.DorisStartException;
import com.realtime.protection.configuration.utils.enums.TaskTypeEnum;
import com.realtime.protection.configuration.utils.status.AuditStatus;
import com.realtime.protection.server.command.CommandService;
import com.realtime.protection.server.task.TaskService;

import java.util.List;

public class StateHandler {

    protected Boolean handleStart(TaskService taskService, CommandService commandService, Long taskId) throws DorisStartException {
        Task task = taskService.queryTask(taskId);

        if (task == null) {
            throw new IllegalArgumentException("无效task_id，因为无法找到对应任务");
        }

        Integer taskAuditStatus = task.getTaskAuditStatus();

        if (taskAuditStatus == null) {
            throw new IllegalArgumentException("无效的task_id，因为task_audit_status为空");
        }

        // 如果审核状态不为已通过审核，则无效
        if (taskAuditStatus != AuditStatus.AUDITED.getAuditStatus()) {
            return false;
        }

        return switch (TaskTypeEnum.getTaskTypeByNum(task.getTaskType())) {
            case STATIC -> handleStaticTaskStart(commandService, taskService, taskId);
            case DYNAMIC -> handleDynamicTaskStart(commandService, taskService, taskId);
            case JUDGED -> handleJudgedTaskStart(commandService, taskService, taskId);
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

    protected Boolean handleStop(CommandService commandService, Long taskId) {
        commandService.removeCommandsByTaskId(taskId);
        return true;
    }

    protected Boolean handleFinish(CommandService commandService, Long taskId) {
        commandService.removeCommandsByTaskId(taskId);
        return true;
    }

    protected Boolean handleFailed(CommandService commandService, Long taskId) {
        commandService.removeCommandsByTaskId(taskId);
        return true;
    }

    // todo: 如果是实时任务或者研判后处置任务，那么就需要在任务启动之后，立刻向动态规则中指定的系统发送日志筛选请求。
    //       筛选完成后，系统返回日志，需要由接收端点提取字段，并且合成一条静态规则，再按照任务开始时间、结束时间和任务类型进行指令创建
    private Boolean handleJudgedTaskStart(CommandService commandService, TaskService taskService, Long taskId) {
        // todo: 研判后处置任务的指令的is_valid字段一开始需要设置为false
        return true;
    }

    private Boolean handleDynamicTaskStart(CommandService commandService, TaskService taskService, Long taskId) {
        // todo: 实时任务的指令的is_valid字段一开始需要设置为true
        return true;
    }

    private Boolean handleStaticTaskStart(CommandService commandService, TaskService taskService, Long taskId) {
        // 如果未能获取staticTaskCommandInfos，需要报错
        List<TaskCommandInfo> staticTaskCommandInfos = taskService.getStaticCommandInfos(taskId);
        if (staticTaskCommandInfos == null || staticTaskCommandInfos.isEmpty()) {
            throw new IllegalArgumentException("静态规则列表为空，请至少选择一个静态规则以启动任务");
        }

        commandService.createCommands(staticTaskCommandInfos);
        return true;
    }
}
