package com.realtime.protection.server.v1.controller;


import com.realtime.protection.configuration.response.ResponseResult;
import com.realtime.protection.server.v1.entity.TCommandReport;
import com.realtime.protection.server.v1.entity.TCommandTask;
import com.realtime.protection.server.v1.service.TCommandReportService;
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

    @Autowired
    private TCommandReportService tCommandReportService;

    @GetMapping("/task_list")
    public ResponseResult commandList(@RequestParam(value = "page", defaultValue = "1") Integer pageNum,
                                      @RequestParam(value = "page_size", defaultValue = "20") Integer pageSize,
                                      @RequestParam(value = "taskName", required = false) String taskName,
                                      @RequestParam(value = "eventType", required = false) String eventType,
                                      @RequestParam(value = "eventLevel", required = false) String eventLevel,
                                      @RequestParam(value = "priority", required = false) String priority,
                                      @RequestParam(value = "taskStartTime", required = false) String taskStartTime,
                                      @RequestParam(value = "taskDisplayId", required = false) String taskDisplayId,
                                      @RequestParam(value = "taskEndTime", required = false) String taskEndTime
    ) {
        TCommandTask tCommandTask = new TCommandTask();
        tCommandTask.setTaskName(taskName);
        tCommandTask.setEventType(eventType);
        tCommandTask.setEventLevel(eventLevel);
        tCommandTask.setPriority(priority);
        tCommandTask.setTaskStartTime(taskStartTime);
        tCommandTask.setTaskEndTime(taskEndTime);
        tCommandTask.setTaskDisplayId(taskDisplayId);
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
                                       @RequestParam(value = "eventType", required = false) String eventType,
                                       @RequestParam(value = "eventLevel", required = false) String eventLevel,
                                       @RequestParam(value = "priority", required = false) String priority,
                                       @RequestParam(value = "taskDisplayId", required = false) String taskDisplayId,
                                       @RequestParam(value = "taskStartTime", required = false) String taskStartTime,
                                       @RequestParam(value = "taskEndTime", required = false) String taskEndTime) {
        TCommandTask tCommandTask = new TCommandTask();
        tCommandTask.setTaskName(taskName);
        tCommandTask.setEventType(eventType);
        tCommandTask.setEventLevel(eventLevel);
        tCommandTask.setPriority(priority);
        tCommandTask.setTaskStartTime(taskStartTime);
        tCommandTask.setTaskEndTime(taskEndTime);
        tCommandTask.setTaskDisplayId(taskDisplayId);
        Long count = tCommandService.countCommandTasks1(tCommandTask);
        pageNum = (pageNum - 1) * pageSize;
        List<TCommandTask> tCommandTasks = tCommandService.queryCommandTasks1(tCommandTask, pageNum, pageSize);
        return ResponseResult.ok()
                .setData("task_list", tCommandTasks)
                .setData("total_num", count);
    }

    /**
     * 查询所有未被关联的任务总数
     *
     * @return
     */
    @GetMapping("/task_list_no_verify_count")
    public ResponseResult commandCount() {
        TCommandTask tCommandTask = new TCommandTask();
        Long count = tCommandService.countCommandTasks1(tCommandTask);
        return ResponseResult.ok()
                .setData("total_num", count);
    }

    /**
     * 新增处置任务
     *
     * @return
     */
    @PostMapping("/result_add")
    public ResponseResult resultAdd(@RequestBody TCommandReport tCommandReport) {
        Integer i = tCommandReportService.infoAdd(tCommandReport);
        return ResponseResult.ok().setData("num", i);
    }

    /**
     * 更新处置任务
     *
     * @return
     */
    @PostMapping("/result_update")
    public ResponseResult resultUpdate(@RequestBody TCommandReport tCommandReport) {
        Integer i = tCommandReportService.infoUpdate(tCommandReport);
        return ResponseResult.ok().setData("num", i);
    }

    /**
     * 更新处置任务
     *
     * @return
     */
    @GetMapping("/result_query")
    public ResponseResult resultQuery(@RequestParam(value = "commandId") Long commandId) {
        TCommandReport tCommandReport = tCommandReportService.queryInfo(commandId);
        return ResponseResult.ok().setData("info", tCommandReport);
    }

}
