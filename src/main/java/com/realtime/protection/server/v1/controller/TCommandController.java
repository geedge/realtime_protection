package com.realtime.protection.server.v1.controller;


import com.realtime.protection.configuration.response.ResponseResult;
import com.realtime.protection.server.v1.entity.TCommandTask;
import com.realtime.protection.server.v1.service.TCommandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/t_command")
public class TCommandController {

    @Autowired
    private TCommandService tCommandService;

    @GetMapping("/task_list")
    public ResponseResult commandList(@RequestParam(value = "page", defaultValue = "1") Integer pageNum,
                                      @RequestParam(value = "page_size", defaultValue = "20") Integer pageSize,
                                      @RequestParam(value = "taskName", required = false) String taskName,
                                      @RequestParam(value = "eventType", required = false) String eventType
    ) {
        TCommandTask tCommandTask = new TCommandTask();
        tCommandTask.setTaskName(taskName);
        tCommandTask.setEventType(eventType);
        Long count = tCommandService.countCommandTasks(tCommandTask);
        pageNum = (pageNum - 1) * pageSize;
        List<TCommandTask> tCommandTasks = tCommandService.queryCommandTasks(tCommandTask, pageNum, pageSize);
        return ResponseResult.ok()
                .setData("task_list", tCommandTasks)
                .setData("total_num", count);
    }

    /**
     * 查询所有未被关联的任务
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping("/task_list_no_verify")
    public ResponseResult commandList1(@RequestParam(value = "page", defaultValue = "1") Integer pageNum,
                                       @RequestParam(value = "page_size", defaultValue = "20") Integer pageSize,
                                       @RequestParam(value = "taskName", required = false) String taskName,
                                       @RequestParam(value = "eventType", required = false) String eventType) {
        TCommandTask tCommandTask = new TCommandTask();
        tCommandTask.setTaskName(taskName);
        tCommandTask.setEventType(eventType);
        Long count = tCommandService.countCommandTasks1(tCommandTask);
        pageNum = (pageNum - 1) * pageSize;
        List<TCommandTask> tCommandTasks = tCommandService.queryCommandTasks1(tCommandTask, pageNum, pageSize);
        return ResponseResult.ok()
                .setData("task_list", tCommandTasks)
                .setData("total_num", count);
    }
}
