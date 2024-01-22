package com.realtime.protection.server.alertmessage;

import com.realtime.protection.configuration.entity.defense.template.ProtectLevel;
import com.realtime.protection.configuration.entity.rule.dynamicrule.AlertMessage;
import com.realtime.protection.configuration.entity.task.FiveTupleWithMask;
import com.realtime.protection.configuration.entity.task.TaskCommandInfo;
import com.realtime.protection.configuration.utils.enums.StateEnum;
import com.realtime.protection.configuration.utils.enums.TaskTypeEnum;
import com.realtime.protection.server.command.CommandService;
import com.realtime.protection.server.task.TaskService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.baomidou.dynamic.datasource.annotation.DSTransactional;

import java.util.List;



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

    @DSTransactional
    public void processAlertMessage(AlertMessage alertMessage) {
        TaskCommandInfo dynamicTaskCommandInfo = generateDynamicCommand(alertMessage);

        Integer taskStatus = dynamicTaskCommandInfo.getTaskStatus();
        Integer taskType = dynamicTaskCommandInfo.getTaskType();

        if (taskType == TaskTypeEnum.DYNAMIC.getTaskType())//动态
            switch (StateEnum.getStateEnumByNum(taskStatus)) {
                case RUNNING:
                    insertCommandAndAlertMessage(dynamicTaskCommandInfo, true, alertMessage);
                    break;
                case PAUSED:
                    insertCommandAndAlertMessage(dynamicTaskCommandInfo, false, alertMessage);
                    break;
                default://主要是stop
                    //command不入库
                    //alertmessage入库
                    insertAlertMessageOnly(alertMessage, true);
                    break;
            }
        else if (taskType == TaskTypeEnum.JUDGED.getTaskType())//研判后
            switch (StateEnum.getStateEnumByNum(taskStatus)) {
                case RUNNING:
                    insertCommandAndAlertMessage(dynamicTaskCommandInfo, false, alertMessage);
                    break;
                case PAUSED:
                    insertCommandAndAlertMessage(dynamicTaskCommandInfo, false, alertMessage);
                    break;
                default://主要是stop
                    //command不入库
                    //alertmessage入库
                    insertAlertMessageOnly(alertMessage, false);
            }
    }


    private TaskCommandInfo generateDynamicCommand(AlertMessage alertMessage){
        Long taskId = alertMessage.getTaskId();

//        查task信息
//        （1）查询生成指令所需信息：和alertMessage中的fiveTuple信息 合并成 TaskCommandInfo;
//        （2）额外信息：并额外查询templateId、protectLevel和taskStatus
        TaskCommandInfo dynamicCommandInfo = alertMessageMapper.getDynamicTaskInfos(taskId);

//        根据templateId、protectLevel获取策略模板
        ProtectLevel templateProtectLevel = alertMessageMapper.queryTemplateProtectLevel(
                dynamicCommandInfo.getTemplateId(),
                dynamicCommandInfo.getProtectLevel());
        //根据策略模板和alertMessage中的FiveTupleWithMask生成要下发五元组信息
        FiveTupleWithMask fiveTupleWithMaskNew =  updateFiveTupleWithMask(alertMessage.getFiveTupleWithMask(),
                templateProtectLevel);
        //指令加入策略模板筛选后的fiveTupleWithMaskNew
        dynamicCommandInfo.setFiveTupleWithMask(fiveTupleWithMaskNew);

        return dynamicCommandInfo;
    }

    @DSTransactional
    private void insertCommandAndAlertMessage(TaskCommandInfo dynamicTaskCommandInfo, Boolean isValid,
                                                AlertMessage alertMessage){
        //command入库
        dynamicTaskCommandInfo.setIsValid(isValid);
        String commandUUID = commandService.createCommand(dynamicTaskCommandInfo);

        //alertmessage入库
        alertMessage.setCommandUUID(commandUUID);
        alertMessageMapper.insertAlertMessage(alertMessage);
    }
    private void insertAlertMessageOnly(AlertMessage alertMessage, Boolean isDistribute){
        //alertmessage入库
        alertMessage.setCommandUUID(null);
        alertMessageMapper.insertAlertMessage(alertMessage);
    }



    private FiveTupleWithMask updateFiveTupleWithMask(FiveTupleWithMask fiveTupleWithMask, ProtectLevel templateProtectLevel) {

        FiveTupleWithMask newFiveTupleWithMask = new FiveTupleWithMask();
        newFiveTupleWithMask.copyFiveTupleWithMask(fiveTupleWithMask);

        if(!templateProtectLevel.getHasProtectObjectIP()){
            newFiveTupleWithMask.setDestinationIP(null);
            newFiveTupleWithMask.setMaskDestinationIP(null);
        }
        if(!templateProtectLevel.getHasProtectObjectPort()){
            newFiveTupleWithMask.setDestinationPort(null);
            newFiveTupleWithMask.setMaskDestinationPort(null);
        }
        if(!templateProtectLevel.getHasPeerIP()){
            newFiveTupleWithMask.setSourceIP(null);
            newFiveTupleWithMask.setMaskSourceIP(null);
        }
        if(!templateProtectLevel.getHasPeerPort()){
            newFiveTupleWithMask.setSourcePort(null);
            newFiveTupleWithMask.setMaskSourcePort(null);
        }
        if (!templateProtectLevel.getHasProtocol()) {
            newFiveTupleWithMask.setProtocol(null);
            newFiveTupleWithMask.setMaskProtocol(null);
        }
        //目前告警信息还只是五元组，没有url、dns
        return newFiveTupleWithMask;
    }



    public List<AlertMessage> queryAlarmsByCommandId(String commandId) {

        return alertMessageMapper.queryAlermsByCommandId(commandId);
    }
}
