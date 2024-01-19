package com.realtime.protection.server.alertmessage;

import com.realtime.protection.configuration.entity.defense.template.ProtectLevel;
import com.realtime.protection.configuration.entity.rule.dynamicrule.AlertMessage;
import com.realtime.protection.configuration.entity.task.FiveTupleWithMask;
import com.realtime.protection.configuration.entity.task.TaskCommandInfo;
import com.realtime.protection.configuration.utils.enums.StateEnum;
import com.realtime.protection.server.command.CommandService;
import com.realtime.protection.server.task.TaskService;
import org.springframework.stereotype.Service;

@Service
public class AlertMessageService {
    private final CommandService commandService;
    private final AlertMessageMapper alertMessageMapper;
    private final TaskService taskService;

    public AlertMessageService(CommandService commandService,TaskService taskService,
    AlertMessageMapper alertMessageMapper) {
        this.commandService = commandService;
        this.taskService = taskService;
        this.alertMessageMapper = alertMessageMapper;
    }

    public void processAlertMessage(AlertMessage alertMessage) {
        Long taskId = alertMessage.getTaskId();
        //检查task status是否为running？
        Integer taskStatus = taskService.queryTaskStatus(taskId);
        Integer temp = StateEnum.RUNNING.getStateNum();
//        if (taskStatus != StateEnum.RUNNING.getStateNum()) {
//            return;
//        }

        //查task信息,和alertMessage中的fiveTuple信息 合并成 TaskCommandInfo
        TaskCommandInfo dynamicTaskCommandInfo = alertMessageMapper.getDynamicTaskInfos(taskId);

        //根据策略模板更新五元组
        ProtectLevel templateProtectLevel = alertMessageMapper.queryTemplateProtectLevel(
                dynamicTaskCommandInfo.getTemplateId(),
                dynamicTaskCommandInfo.getProtectLevel(),
                alertMessage.getFiveTupleWithMask());
        updateFiveTupleWithMask(alertMessage.getFiveTupleWithMask(), templateProtectLevel);
        dynamicTaskCommandInfo.setFiveTupleWithMask(alertMessage.getFiveTupleWithMask());

//        command入库
        commandService.createCommand(dynamicTaskCommandInfo);

    }

    private void updateFiveTupleWithMask(FiveTupleWithMask alertMessageFiveTupleW, ProtectLevel templateProtectLevel) {
        if(!templateProtectLevel.getHasProtectObjectIP()){
            alertMessageFiveTupleW.setDestinationIP(null);
            alertMessageFiveTupleW.setMaskDestinationIP(null);
        }
        if(!templateProtectLevel.getHasProtectObjectPort()){
            alertMessageFiveTupleW.setDestinationPort(null);
            alertMessageFiveTupleW.setMaskDestinationPort(null);
        }
        if(!templateProtectLevel.getHasPeerIP()){
            alertMessageFiveTupleW.setSourceIP(null);
            alertMessageFiveTupleW.setMaskSourceIP(null);
        }
        if(!templateProtectLevel.getHasPeerPort()){
            alertMessageFiveTupleW.setSourcePort(null);
            alertMessageFiveTupleW.setMaskSourcePort(null);
        }
        if (!templateProtectLevel.getHasProtocol()) {
            alertMessageFiveTupleW.setProtocol(null);
            alertMessageFiveTupleW.setMaskProtocol(null);
        }
        //目前告警信息还只是五元组，没有url、dns
    }
}
