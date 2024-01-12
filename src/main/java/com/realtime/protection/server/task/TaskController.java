package com.realtime.protection.server.task;

import com.realtime.protection.configuration.entity.task.Task;
import com.realtime.protection.configuration.exception.DorisStartException;
import com.realtime.protection.configuration.response.ResponseResult;
import com.realtime.protection.configuration.utils.EntityUtils;
import com.realtime.protection.server.task.status.StateChangeService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/task")
public class TaskController implements TaskControllerApi {

    private final TaskService taskService;
    private final StateChangeService stateChangeService;

    public TaskController(TaskService taskService, StateChangeService stateChangeService) {
        this.taskService = taskService;
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

    @Override
    @GetMapping("/query")
    public ResponseResult queryTasks(@RequestParam(value = "task_status", required = false) Integer taskStatus,
                                     @RequestParam(value = "task_type", required = false) String taskType,
                                     @RequestParam(value = "task_name", required = false) String taskName,
                                     @RequestParam(value = "task_creator", required = false) String taskCreator,
                                     @RequestParam("page") @Min(1) Integer page,
                                     @RequestParam("page_size") @Min(1) Integer pageSize) {
        List<Task> tasks = taskService.queryTasks(taskStatus, taskType, taskName, taskCreator, page, pageSize);
        return ResponseResult.ok()
                .setData("task_list", tasks);
    }

    @Override
    @GetMapping("/{id}/query")
    public ResponseResult queryTask(@PathVariable @Min(1) Long id) throws IllegalAccessException {
        Task task = taskService.queryTask(id);

        if (task == null) {
            return ResponseResult.invalid().setMessage("Task ID is invalid");
        }

        return ResponseResult.ok()
                .setDataMap(EntityUtils.entityToMap(task));
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
    public ResponseResult changeTaskStatus(@PathVariable @NotNull Integer stateNum,
                                           @PathVariable @NotNull Long taskId) throws DorisStartException {
        return ResponseResult.ok()
                .setData("task_id", taskId)
                .setData("success", stateChangeService.changeState(stateNum, taskId))
                .setData("status_now", taskService.queryTaskStatus(taskId));
    }
}
