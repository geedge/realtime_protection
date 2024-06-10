package com.realtime.protection.server.alertmessage;

import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.realtime.protection.configuration.entity.alert.AlertMessage;
import com.realtime.protection.configuration.entity.defense.template.ProtectLevel;
import com.realtime.protection.configuration.entity.task.FiveTupleWithMask;
import com.realtime.protection.configuration.entity.task.TaskCommandInfo;
import com.realtime.protection.configuration.utils.Counter;
import com.realtime.protection.configuration.utils.enums.StateEnum;
import com.realtime.protection.configuration.utils.enums.TaskTypeEnum;
import com.realtime.protection.server.command.CommandService;
import com.realtime.protection.server.task.status.StateHandler;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

// AlertMessage的UUID在mapper插入数据库时生成了，这里提前生成好像美神恶魔用
@Service
@Slf4j
public class AlertMessageService {
    private final CommandService commandService;
    private final AlertMessageMapper alertMessageMapper;
    private  final Counter counter;
    private final StateHandler stateHandler;

    public AlertMessageService(
            CommandService commandService, AlertMessageMapper alertMessageMapper, Counter counter, StateHandler stateHandler) {
        this.commandService = commandService;
        this.alertMessageMapper = alertMessageMapper;
        this.counter = counter;
        this.stateHandler = stateHandler;
    }

    @DSTransactional
    public void processAlertMessage(AlertMessage alertMessage) {
        //将告警信息中的c_time转换为LocalDateTime，并写入ctime
        Instant instant = Instant.ofEpochSecond(alertMessage.getC_time());
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        alertMessage.setCtime(localDateTime);

        //根据告警信息——>生成指令
        List<TaskCommandInfo> dynamicTaskCommandInfoList = generateDynamicCommand(alertMessage);
        //可能isProtectSrcOrDst和isProtectSrcOrDst都为FALSE，说明没有生成指令
        if(dynamicTaskCommandInfoList == null || dynamicTaskCommandInfoList.isEmpty()){
            return;
        }
        //获取任务状态，设置指令的isValid字段，且是否生成指令入库（除了RUNING\PAUSED状态，其他都不入command库）。
        Integer taskStatus = dynamicTaskCommandInfoList.get(0).getTaskStatus();
        //获取任务类型，设置指令的isJudged字段。
        Integer taskType = dynamicTaskCommandInfoList.get(0).getTaskType();


        if (taskType == TaskTypeEnum.DYNAMIC.getTaskType())//实时
            switch (StateEnum.getStateEnumByNum(taskStatus)) {
                case RUNNING:
                    insertCommandAndAlertMessage(dynamicTaskCommandInfoList, true, true, alertMessage);
                    break;
                case PAUSED:
                    insertCommandAndAlertMessage(dynamicTaskCommandInfoList, false, true, alertMessage);
                    break;
                default://主要是stop
                    //command不入库
                    //alertmessage入库
                    insertAlertMessageOnly(alertMessage);
                    break;
            }
        else if (taskType == TaskTypeEnum.JUDGED.getTaskType())//研判后
            switch (StateEnum.getStateEnumByNum(taskStatus)) {
                case RUNNING:
                    insertCommandAndAlertMessage(dynamicTaskCommandInfoList, true, false, alertMessage);
                    break;
                case PAUSED:
                    insertCommandAndAlertMessage(dynamicTaskCommandInfoList, false, false, alertMessage);
                    break;
                default://主要是stop
                    //command不入库
                    //alertmessage入库
                    insertAlertMessageOnly(alertMessage);
            }
    }


    private List<TaskCommandInfo> generateDynamicCommand(AlertMessage alertMessage){
        Long taskId = alertMessage.getTaskId();
        Integer DynamicRuleId = alertMessage.getDynamicRuleId();
//        查task信息
//        （1）查询生成指令所需信息：和alertMessage中的fiveTuple信息 合并成 TaskCommandInfo;
//        （2）额外信息：并额外查询templateId、protectLevel和taskStatus
        TaskCommandInfo dynamicCommandInfo = alertMessageMapper.getDynamicTaskInfos(taskId, DynamicRuleId);
        if (dynamicCommandInfo == null || dynamicCommandInfo.getTemplateId() == null){
            throw new IllegalArgumentException("taskId: " + taskId + " DynamicRuleId: " + DynamicRuleId + " 不正确");
        }
//        根据templateId、protectLevel获取策略模板
        ProtectLevel templateProtectLevel = alertMessageMapper.queryTemplateProtectLevel(
                dynamicCommandInfo.getTemplateId(),
                dynamicCommandInfo.getProtectLevel());

        //根据策略模板和alertMessage中的FiveTupleWithMask生成要下发五元组信息
        //根据策略模板的is_full_flow字段，如果是双向流量会生成两个fiveTuple，所以返回List
        List<FiveTupleWithMask> fiveTupleWithMaskNew =  updateFiveTupleWithMask(alertMessage.getFiveTupleWithMask(),
                alertMessage.getProtectIsSrcOrDst(), templateProtectLevel);
        if(fiveTupleWithMaskNew.isEmpty()){
            return  null;
        }
        //根据fiveTuple生成动态指令信息
        List<TaskCommandInfo> dynamicCommandInfoList = new ArrayList<>();
        if (fiveTupleWithMaskNew.size() == 2){
            TaskCommandInfo dynamicCommandInfo_bi = new TaskCommandInfo();
            dynamicCommandInfo_bi.copyTaskCommandInfo(dynamicCommandInfo);
            dynamicCommandInfo_bi.setFiveTupleWithMask(fiveTupleWithMaskNew.get(1));
            dynamicCommandInfoList.add(dynamicCommandInfo_bi);
        }
        dynamicCommandInfo.setFiveTupleWithMask(fiveTupleWithMaskNew.get(0));
        dynamicCommandInfoList.add(dynamicCommandInfo);

        return dynamicCommandInfoList;
    }

    @DSTransactional
    private void insertCommandAndAlertMessage(List<TaskCommandInfo> dynamicTaskCommandInfoList,
                                              Boolean isValid,
                                              Boolean isJudged,
                                              AlertMessage alertMessage){
        List<String> commandUUIDs = new ArrayList<>();

        for (TaskCommandInfo dynamicTaskCommandInfo : dynamicTaskCommandInfoList ){
            //command入库
            dynamicTaskCommandInfo.setIsValid(isValid);
            dynamicTaskCommandInfo.setIsJudged(isJudged);
            String commandUUID = commandService.createCommand(dynamicTaskCommandInfo);

            //alertmessage入库

            alertMessage.setCommandUUID(commandUUID);
            String alertMessageUUID = UUID.randomUUID().toString();
            commandUUIDs.add(commandUUID);
            alertMessage.setAlertMessageUUID(alertMessageUUID);
            alertMessage.setDisplay_id(
                    "GJ-"
                            + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))
                            + "-"
                            + String.format("%06d", counter.generateId("alert_message"))

            );
            alertMessageMapper.insertAlertMessage(alertMessage);
            }
        //发送指令新建信号...实时任务 isJudged=1 才首次立刻下发
        try {
            if (isJudged){
                stateHandler.sendCommandDistributeSignal(commandUUIDs);
            }
        }catch (Exception e) {
            log.info(String.format("动态任务首次指令下发c3出错，任务id: %d,commandUUIDs: %s",
                    dynamicTaskCommandInfoList.get(0).getTaskId(),
                    commandUUIDs));
        }
        //发送RCP查询信号
        try {
            if (isJudged){
                stateHandler.sendCommandRcpQuerySignal(commandUUIDs);
            }
        }catch (Exception e) {
            log.info(String.format("动态任务首次指令查询RCP出错，任务id: %d,commandUUIDs: %s",
                    dynamicTaskCommandInfoList.get(0).getTaskId(),
                    commandUUIDs));
        }
    }
    private String insertAlertMessageOnly(AlertMessage alertMessage){
        //alertmessage入库
        alertMessage.setCommandUUID(null);
        String alertMessageUUID = UUID.randomUUID().toString();
        alertMessage.setAlertMessageUUID(alertMessageUUID);
        alertMessage.setDisplay_id(
                "GJ-"
                        + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))
                        + "-"
                        + String.format("%06d", counter.generateId("alert_message"))

        );
        alertMessageMapper.insertAlertMessage(alertMessage);

        return alertMessageUUID;

    }



    private List<FiveTupleWithMask> updateFiveTupleWithMask(FiveTupleWithMask fiveTupleWithMask,
                                                      int protectIsSrcOrDst,
                                                      ProtectLevel templateProtectLevel) {
        //参数是告警信息的FiveTupleWithMask、防护对象是src还是dst、某个安全等级下的安全事件策略模板templateProtectLevel
        //首先先从告警信息中获取protectObject和peer
        @Data
        class CommunicateObject {
            private String IP;
            private String maskIP;
            private String Port;
            private String maskPort;

            public CommunicateObject(String IP,
                                     String maskIP,
                                     String Port,
                                     String maskPort) {
                this.IP = IP;
                this.maskIP = maskIP;
                this.Port = Port;
                this.maskPort = maskPort;
            }
        }
        CommunicateObject protectObject;
        CommunicateObject peer;
        //0代表命中防护对象在告警信息的源ip
        if (protectIsSrcOrDst == 0) {
            protectObject = new CommunicateObject(
                    fiveTupleWithMask.getSourceIP(),
                    fiveTupleWithMask.getMaskSourceIP(),
                    fiveTupleWithMask.getSourcePort(),
                    fiveTupleWithMask.getMaskSourcePort()
            );
            peer = new CommunicateObject(
                    fiveTupleWithMask.getDestinationIP(),
                    fiveTupleWithMask.getMaskDestinationIP(),
                    fiveTupleWithMask.getDestinationPort(),
                    fiveTupleWithMask.getMaskDestinationPort()
            );
        } else {
            protectObject = new CommunicateObject(
                    fiveTupleWithMask.getDestinationIP(),
                    fiveTupleWithMask.getMaskDestinationIP(),
                    fiveTupleWithMask.getDestinationPort(),
                    fiveTupleWithMask.getMaskDestinationPort()
            );
            peer = new CommunicateObject(
                    fiveTupleWithMask.getSourceIP(),
                    fiveTupleWithMask.getMaskSourceIP(),
                    fiveTupleWithMask.getSourcePort(),
                    fiveTupleWithMask.getMaskSourcePort()
            );
        }
        //根据模板抽取防护对象和对端需要的字段
        if (!templateProtectLevel.getHasProtectObjectIP()) {
            protectObject.setIP(null);
            protectObject.setMaskIP(null);
        }
        if (!templateProtectLevel.getHasProtectObjectPort()) {
            protectObject.setPort(null);
            protectObject.setMaskPort(null);
        }
        if (!templateProtectLevel.getHasPeerIP()) {
            peer.setIP(null);
            peer.setMaskIP(null);
        }
        if (!templateProtectLevel.getHasPeerPort()) {
            peer.setPort(null);
            peer.setMaskPort(null);
        }
        List<FiveTupleWithMask> newFiveTupleWithMask = new ArrayList<>();
        //生成指令command1：防护对象为目的的五元组
        FiveTupleWithMask command1 = new FiveTupleWithMask();
        command1.setSourceIP(peer.getIP());
        command1.setMaskSourceIP(peer.getMaskIP());
        command1.setSourcePort(peer.getPort());
        command1.setMaskSourcePort(peer.getMaskPort());
        command1.setDestinationIP(protectObject.getIP());
        command1.setMaskDestinationIP(protectObject.getMaskIP());
        command1.setDestinationPort(protectObject.getPort());
        command1.setMaskDestinationPort(protectObject.getMaskPort());
        if (templateProtectLevel.getHasProtocol()){
            command1.setProtocolNum(Integer.valueOf(fiveTupleWithMask.getProtocol()));
            command1.setProtocol(fiveTupleWithMask.getProtocol());
            command1.setMaskProtocol(fiveTupleWithMask.getMaskProtocol());
        }
//        newFiveTupleWithMask.add(command1);
        //生成指令command2：防护对象为源的五元组
        FiveTupleWithMask command2 = new FiveTupleWithMask();

        command2.setSourceIP(protectObject.getIP());
        command2.setMaskSourceIP(protectObject.getMaskIP());
        command2.setSourcePort(protectObject.getPort());
        command2.setMaskSourcePort(protectObject.getMaskPort());

        command2.setDestinationIP(peer.getIP());
        command2.setMaskDestinationIP(peer.getMaskIP());
        command2.setDestinationPort(peer.getPort());
        command2.setMaskDestinationPort(peer.getMaskPort());
        if (templateProtectLevel.getHasProtocol()){
            command2.setProtocol(fiveTupleWithMask.getProtocol());
            command2.setProtocol(fiveTupleWithMask.getProtocol());
            command2.setMaskProtocol(fiveTupleWithMask.getMaskProtocol());
        }
/*
        //若需要处置全方向流量，防护对象为源和目的的五元组都生成指令下发
        if(templateProtectLevel.getIsFullFlow()){
            newFiveTupleWithMask.add(command1);
            newFiveTupleWithMask.add(command2);
        }else {
            //不需要处置全方向流量
            // 判断防护对象为源还是目的，生成指令
            if(templateProtectLevel.getIsProtectObjectIPSrc()){
                newFiveTupleWithMask.add(command2);
            }else {
                newFiveTupleWithMask.add(command1);
            }
        }
*/

        //若需要处置全方向流量，防护对象为源和目的的五元组都生成指令下发

            // 判断防护对象为源还是目的，生成指令
        if(templateProtectLevel.getIsProtectObjectSrc()){
            newFiveTupleWithMask.add(command2);
        }
        if (templateProtectLevel.getIsProtectObjectDst()){
            newFiveTupleWithMask.add(command1);
        }


        //目前告警信息还只是五元组，没有url、dns
        return newFiveTupleWithMask;
    }



    public List<AlertMessage> queryAlarmsByCommandId(String commandId) {

        return alertMessageMapper.queryAlermsByCommandId(commandId);
    }
}
