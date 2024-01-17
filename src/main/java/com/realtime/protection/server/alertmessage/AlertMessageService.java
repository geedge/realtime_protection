package com.realtime.protection.server.alertmessage;

import com.realtime.protection.configuration.entity.rule.dynamicrule.AlertMessage;
import com.realtime.protection.configuration.entity.task.Task;
import com.realtime.protection.configuration.entity.task.TaskCommandInfo;
import com.realtime.protection.server.task.TaskService;
import org.springframework.stereotype.Service;
import com.realtime.protection.server.command.CommandService;
import com.realtime.protection.configuration.utils.enums.StateEnum;

@Service
public class AlertMessageService {
    private final CommandService commandService;
    private final TaskService taskService;

    public AlertMessageService(CommandService commandService,TaskService taskService) {
        this.commandService = commandService;
        this.taskService = taskService;
    }

    public void receiveAlertMessage(AlertMessage alertMessage) {
        Long taskId = alertMessage.getTaskId();
        //查task信息
        Task task = taskService.queryTask(taskId);
        //检查task status是否为running？
//        if (task.getTaskStatus() != StateEnum.RUNNING.getStateNum()) {
//            return;
//        }

        //task信息和alertMessage中的fiveTuple信息 合并成 TaskCommandInfo
        TaskCommandInfo dynamicTaskCommandInfo = new TaskCommandInfo();


        //command入库
        //commandService.createCommand(staticTaskCommandInfo);

    }
}
