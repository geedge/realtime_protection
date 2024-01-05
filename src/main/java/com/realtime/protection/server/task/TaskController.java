package com.realtime.protection.server.task;

import com.realtime.protection.configuration.entity.task.Task;
import com.realtime.protection.configuration.response.ResponseResult;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/task")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping("/new")
    public ResponseResult newTask(@RequestBody @Valid Task task) {
        Integer taskId = taskService.newTask(task);

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

    @GetMapping("/{id}/query")
    public ResponseResult queryTask(@PathVariable("id") @Min(1) Integer id) {
        Task task = taskService.queryTask(id);

        if (task == null) {
            return ResponseResult.invalid().setMessage("Task ID is invalid");
        }

        return ResponseResult.ok()
                .setData("task_id", task.getTaskId())
                .setData("task_name", task.getTaskName())
                .setData("task_type", task.getTaskType())
                .setData("task_status", task.getTaskStatus())
                .setData("task_creator", task.getTaskCreateUsername())
                .setData("task_creator_depart", task.getTaskCreateDepart())
                .setData("task_start_time", task.getTaskStartTime())
                .setData("task_end_time", task.getTaskEndTime())
                .setData("task_static_rule_ids", task.getStaticRuleIds())
                .setData("task_dynamic_rule_ids", task.getDynamicRuleIds())
                .setData("task_protect_object_ids", task.getProtectObjectIds());
    }

    @PostMapping("/{id}/update")
    public ResponseResult updateTask(@PathVariable("id") @Min(1) Integer taskId, @RequestBody @Valid Task task) {
        task.setTaskId(taskId);
        taskService.updateTask(task);

        return ResponseResult.ok()
                .setData("task_id", taskId)
                .setData("success", true);
    }
}
