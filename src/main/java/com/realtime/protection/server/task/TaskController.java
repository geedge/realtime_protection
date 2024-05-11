package com.realtime.protection.server.task;

import com.realtime.protection.configuration.entity.task.Task;
import com.realtime.protection.configuration.entity.task.TaskCommandInfo;
import com.realtime.protection.configuration.exception.DorisStartException;
import com.realtime.protection.configuration.response.ResponseResult;
import com.realtime.protection.configuration.utils.enums.StateEnum;
import com.realtime.protection.configuration.utils.enums.audit.AuditStatusEnum;
import com.realtime.protection.server.command.CommandService;
import com.realtime.protection.server.task.status.StateChangeService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/task")
public class TaskController implements TaskControllerApi {

    private final TaskService taskService;
    private final CommandService commandService;
    private final StateChangeService stateChangeService;

    public TaskController(TaskService taskService, CommandService commandService, StateChangeService stateChangeService) {
        this.taskService = taskService;
        this.commandService = commandService;
        this.stateChangeService = stateChangeService;
    }

    @Override
    @PostMapping("/new")
    public ResponseResult newTask(@RequestBody @Valid Task task) {
        Long taskId = taskService.newTask(task);

        if (taskId > 0) {
            return ResponseResult.ok()
                    .setData("task_name", task.getTaskName())
                    .setData("task_id", taskId)
                    .setData("success", true);
        }

        return ResponseResult.error()
                .setData("task_name", task.getTaskName())
                .setData("task_id", 0)
                .setData("success", false);
    }

    // API推送Endpoint
    @Override
    @PostMapping("/api/new")
    public ResponseResult newTaskWithAPI(@RequestBody @Valid TaskCommandInfo taskCommandInfo) {
        Long taskId = taskService.newTaskUsingCommandInfo(taskCommandInfo);
        if (taskId <= 0) {
            return ResponseResult.invalid()
                    .setData("taskId", -1)
                    .setData("success", false);
        }

        commandService.createCommand(taskCommandInfo);

        return ResponseResult.ok()
                .setData("taskId", taskId)
                .setData("success", true);
    }

    @Override
    @GetMapping("/query")
    public ResponseResult queryTasks(@RequestParam(value = "task_status", required = false) Integer taskStatus,
                                     @RequestParam(value = "task_type", required = false) Integer taskType,
                                     @RequestParam(value = "task_name", required = false) String taskName,
                                     @RequestParam(value = "task_creator", required = false) String taskCreator,
                                     @RequestParam(value = "audit_status", required = false) Integer auditStatus,
                                     @RequestParam(value = "task_act", required = false) String taskAct,
                                     @RequestParam(value = "task_auditor", required = false) String taskAuditor,
                                     @RequestParam(value = "task_source", required = false) String taskSource,
                                     @RequestParam(value = "rule_name", required = false) String ruleName,
                                     @RequestParam("page") @Min(1) Integer page,
                                     @RequestParam("page_size") @Min(1) Integer pageSize) {
        List<Task> tasks = taskService.queryTasks(taskStatus, taskType, taskName, taskCreator, auditStatus,
                taskAct, taskAuditor, taskSource, ruleName, page, pageSize);
        return ResponseResult.ok()
                .setData("task_list", tasks)
                .setData("total_num", taskService.queryTaskTotalNum(taskStatus, taskType, taskName, taskCreator, auditStatus,
                        taskAct, taskAuditor, taskSource, ruleName));
    }

    @Override
    @GetMapping("/{id}/query")
    public ResponseResult queryTask(@PathVariable @Min(1) Long id) {
        Task task = taskService.queryTask(id);

        if (task == null) {
            return ResponseResult.invalid().setMessage("无效Task ID，也许该ID对应的任务不存在？");
        }

        return ResponseResult.ok().setData("task", task);
    }

    @Override
    @PostMapping("/{taskId}/update")
    public ResponseResult updateTask(@PathVariable Long taskId, @RequestBody @Valid Task task) {
        task.setTaskId(taskId);
        return ResponseResult.ok()
                .setData("task_id", task.getTaskId())
                .setData("success", taskService.updateTask(task));
    }

    @Override
    @GetMapping("/{taskId}/audit/{auditStatus}")
    public ResponseResult changeTaskAuditStatus(@PathVariable @NotNull @Max(10) Integer auditStatus,
                                                @PathVariable @NotNull @Min(1) Long taskId) {

        return ResponseResult.ok()
                .setData("task_id", taskId)
                .setData("success", taskService.changeTaskAuditStatus(taskId, auditStatus))
                .setData("audit_status", taskService.queryTaskAuditStatus(taskId));
    }

    @Override
    @DeleteMapping("/{taskId}/delete")
    public ResponseResult deleteTask(@PathVariable @NotNull @Min(1) Long taskId) {
        return ResponseResult.ok()
                .setData("task_id", taskId)
                .setData("success", taskService.deleteTask(taskId));
    }

    @Override
    @GetMapping("/{taskId}/running/{stateNum}")
    public ResponseResult changeTaskStatus(@PathVariable @NotNull @Min(0) @Max(6) Integer stateNum,
                                           @PathVariable @NotNull @Min(1) Long taskId) throws DorisStartException {
        return ResponseResult.ok()
                .setData("task_id", taskId)
                // 外部修改状态，需要进行状态检查
                .setData("success", stateChangeService.changeState(stateNum, taskId, false))
                .setData("status_now", taskService.queryTaskStatus(taskId));
    }

    @Override
    @GetMapping("/{taskId}/commands")
    public ResponseResult queryCommandInfos(@PathVariable Long taskId,
                                            @RequestParam(name = "src_ip", required = false) String sourceIP,
                                            @RequestParam(name = "src_port", required = false) String sourcePort,
                                            @RequestParam(name = "dst_ip", required = false) String destinationIP,
                                            @RequestParam(name = "dst_port", required = false) String destinationPort,
                                            @RequestParam(name = "page") @Min(1) Integer page,
                                            @RequestParam(name = "page_num") @Min(1) Integer pageNum) {
        List<TaskCommandInfo> taskCommandInfos = commandService.queryCommandInfos(
                taskId, sourceIP, sourcePort, destinationIP, destinationPort, page, pageNum);

        return ResponseResult.ok()
                .setData("success", true)
                .setData("commands", taskCommandInfos)
                .setData("total_num", commandService.queryCommandTotalNum(taskId, sourceIP, sourcePort, destinationIP, destinationPort));
    }

    @GetMapping("/{commandId}/valid/{isJudged}")
    public ResponseResult setCommandJudged(@PathVariable Boolean isJudged,
                                           @PathVariable String commandId) {
        return ResponseResult.ok()
                .setData("success", commandService.setCommandJudged(commandId, isJudged))
                .setData("command_id", commandId);
    }


    /**
     * 批量修改审核状态
     */
    @Override
    @PostMapping("/auditbatch")
    public ResponseResult updateTaskAuditStatusBatch(@RequestBody Map<Integer, Integer> idsWithAuditStatusMap) {
        List<Integer> errorIds = new ArrayList<>();
        for (Map.Entry<Integer, Integer> entry: idsWithAuditStatusMap.entrySet()) {
            Integer id = entry.getKey();
            Integer auditStatus = entry.getValue();
            if (id <= 0 || auditStatus < 0 || auditStatus > 2) {
                errorIds.add(id);
            }
        }
        if (!errorIds.isEmpty()) {
            return ResponseResult.invalid()
                    .setData("tasks_id", errorIds)
                    .setData("success", false);
        }

        return ResponseResult.ok()
                .setData("success", taskService.updateAuditStatusBatch(idsWithAuditStatusMap));
    }

    /**
     * 统计
     */
    @Override
    @GetMapping("/statistics")
    public ResponseResult statistics() {
        return ResponseResult.ok()
                .setData("total_num", taskService.queryTaskTotalNum(null, null, null, null, null,
                        null, null, null, null))
                .setData("running_num", taskService.queryTaskTotalNum(StateEnum.RUNNING.getStateNum(), null, null, null, null,
                        null, null, null, null))
                .setData("finished_num", taskService.queryTaskTotalNum(StateEnum.FINISHED.getStateNum(), null, null, null, null,
                        null, null, null, null))
                .setData("unaudit_num", taskService.queryAuditTaskTotalNum(
                        AuditStatusEnum.PENDING.getNum()
                ));
    }

    @Override
    @PostMapping("/auditInfo/{ids}")
    public ResponseResult updateAuditInfo(@PathVariable List<Integer> ids,
                                          @RequestBody Map<String, String> auditInfo) {
        if (auditInfo.get("auditInfo") == null || auditInfo.get("auditInfo").isEmpty()) {
            throw new IllegalArgumentException("auditInfo is empty");
        }
        return ResponseResult.ok()
                .setData("success", taskService.updateAuditInfo(ids, auditInfo.get("auditInfo")));
    }

    @Override
    @GetMapping("/auditInfo/{id}")
    public ResponseResult queryAuditInfo(@PathVariable Integer id) {
        return ResponseResult.ok()
                .setData("auditInfo", taskService.queryAuditInfo(id));
    }


}