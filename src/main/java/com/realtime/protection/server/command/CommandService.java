package com.realtime.protection.server.command;

import com.alibaba.excel.util.ListUtils;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.realtime.protection.configuration.entity.task.TaskCommandInfo;
import com.realtime.protection.configuration.entity.whitelist.WhiteListObject;
import com.realtime.protection.configuration.utils.Counter;
import com.realtime.protection.configuration.utils.SqlSessionWrapper;
import com.realtime.protection.server.task.status.StateHandler;
import com.realtime.protection.server.whitelist.WhiteListMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

@Service
@Slf4j
@DS("doris")
public class CommandService {

    private final CommandMapper commandMapper;
    private final SqlSessionWrapper sqlSessionWrapper;
    private final Counter counter;
    private final WhiteListMapper whiteListMapper;
    private static final int BatchSize = 100;
    private final StateHandler stateHandler;

    public CommandService(CommandMapper commandMapper, SqlSessionWrapper sqlSessionWrapper, Counter counter, WhiteListMapper whiteListMapper, StateHandler stateHandler) {
        this.commandMapper = commandMapper;
        this.sqlSessionWrapper = sqlSessionWrapper;
        this.counter = counter;
        this.whiteListMapper = whiteListMapper;
        this.stateHandler = stateHandler;
    }

    @DSTransactional
    public String createCommand(TaskCommandInfo commandInfo) {
        String uuid = commandMapper.queryCommandInfo(commandInfo);
        if (uuid != null) {
            return uuid;
        }
        commandInfo.setDisplayId(
                "ZL-"
                        + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))
                        + "-"
                        + String.format("%06d", counter.generateId("command"))
        );

        //指令：白名单检查
        List<WhiteListObject> whiteListsHit = commandMapper.whiteListCommandCheck(commandInfo.getFiveTupleWithMask());
        if (!whiteListsHit.isEmpty()) {
            commandInfo.setUUID(UUID.randomUUID().toString());
            commandMapper.createCommandInWhiteListHit(commandInfo);
            commandMapper.createCommandWhiteListConnect(commandInfo.getUUID(), whiteListsHit);
            //写入历史表
            //insertCommandHistory(commandInfo.getUUID());
            return commandInfo.getUUID();
        }

        commandInfo.setUUID(UUID.randomUUID().toString());
        commandMapper.createCommand(commandInfo);
        //写入历史表
        //insertCommandHistory(commandInfo.getUUID());
        return commandInfo.getUUID();
    }

    @DSTransactional
    public String createCommand2(TaskCommandInfo commandInfo, Integer isJudged) {
        String uuid = commandMapper.queryCommandInfo(commandInfo);
        //如果指令已经存在，除了研判状态为2,时需要改为0，其他情况都直接返回uuid
        if (uuid != null) {
            if (isJudged == 0){
                //研判后任务，查询指令当前研判状态
                Integer originalIsJudged = commandMapper.queryCommandIsJudged(uuid);
                //如果研判状态为2，表示之前设置了本次忽略，那这次生成指令后，将其研判状态改为0，需要再次研判
                if (originalIsJudged == 2){
                    commandMapper.updateCommandIsJudgedIfIgnoreThisTime(uuid);
                }
            }
            return uuid;
        }
        commandInfo.setDisplayId(
                "ZL-"
                        + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))
                        + "-"
                        + String.format("%06d", counter.generateId("command"))
        );

        //指令：白名单检查
        List<WhiteListObject> whiteListsHit = commandMapper.whiteListCommandCheck(commandInfo.getFiveTupleWithMask());
        if (!whiteListsHit.isEmpty()) {
            commandInfo.setUUID(UUID.randomUUID().toString());
            commandMapper.createCommandInWhiteListHit(commandInfo);
            commandMapper.createCommandWhiteListConnect(commandInfo.getUUID(), whiteListsHit);
            //写入历史表
            //insertCommandHistory(commandInfo.getUUID());
            return commandInfo.getUUID();
        }

        commandInfo.setUUID(UUID.randomUUID().toString());
        commandMapper.createCommand(commandInfo);
        //写入历史表,避免t_command_log表并发update冲突，这里先不写入历史表
        //insertCommandHistory(commandInfo.getUUID());

        //发送指令新建信号...实时任务 isJudged=1 才首次立刻下发
        try {
            if (isJudged == 1){
                stateHandler.sendCommandDistributeSignal(Collections.singletonList(commandInfo.getUUID()));
            }
        }catch (Exception e) {
            log.info(String.format("实时任务首次指令下发c3出错，commandUUIDs: %s",
                    commandInfo.getUUID()));
        }
        //发送RCP查询信号
        try {
            if (isJudged == 1){
                stateHandler.sendCommandRcpQuerySignal(Collections.singletonList(commandInfo.getUUID()));
            }
        }catch (Exception e) {
            log.info(String.format("实时任务首次指令查询RCP出错，commandUUIDs: %s",
                    commandInfo.getUUID()));
        }

        return commandInfo.getUUID();
    }

    public List<String> createCommands(List<TaskCommandInfo> taskCommandInfos) {

        List<String> commandUUIDs = ListUtils.newArrayListWithExpectedSize(taskCommandInfos.size());
        AtomicInteger i = new AtomicInteger();
        Function<CommandMapper, Function<List<TaskCommandInfo>, Boolean>> function = mapper -> list -> {
            List<TaskCommandInfo> taskCommandInfoBatch = ListUtils.newArrayListWithExpectedSize(BatchSize);
            for (TaskCommandInfo info : list) {
                info.setUUID(UUID.randomUUID().toString());
                commandUUIDs.add(info.getUUID());
                info.setDisplayId(
                        "ZL-"
                                + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))
                                + "-"
                                + String.format("%06d", counter.generateId("command"))
                );
                taskCommandInfoBatch.add(info);

                if (taskCommandInfoBatch.size() < BatchSize) {
                    continue;
                }
                System.out.println("batch insert " + i.getAndIncrement());
                //因为createCommands只用于静态规则生成command，静态规则已经检查了白名单，所以不检查了
                commandMapper.createCommands(taskCommandInfoBatch);
                //insertCommandHistoryBatch(taskCommandInfoBatch);
                taskCommandInfoBatch.clear();
            }

            if (!taskCommandInfoBatch.isEmpty()) {
                commandMapper.createCommands(taskCommandInfoBatch);
                //insertCommandHistoryBatch(taskCommandInfoBatch);
                taskCommandInfoBatch.clear();
            }

            return true;
        };

        sqlSessionWrapper.startBatchSession(CommandMapper.class, function, taskCommandInfos);

        return commandUUIDs;
    }

    public List<TaskCommandInfo> queryCommandInfos(Long taskId,
                                                   String sourceIP, String sourcePort,
                                                   String destinationIP, String destinationPort,
                                                   Integer page, Integer pageNum) {
        return commandMapper.queryCommandInfos(taskId,
                sourceIP, sourcePort,
                destinationIP, destinationPort,
                page, pageNum);
    }

    public TaskCommandInfo queryCommandInfoByUUID(String uuid) {
        return commandMapper.queryCommandInfoByUUID(uuid);
    }

    public Boolean startCommandsByTaskId(Long taskId) {
        return commandMapper.startCommandsByTaskId(taskId);
    }

    public Boolean stopCommandsByTaskId(Long taskId) {
        return commandMapper.stopCommandsByTaskId(taskId);
    }

    public Boolean removeCommandsByTaskId(Long taskId) {
        return commandMapper.removeCommandsByTaskId(taskId);
    }

    public Boolean setCommandJudged(String commandId, Integer isJudged) {
        //查詢指令当前is_judged状态,如果为0才可以被修改

        //设置指令是否已经研判
        Boolean success = commandMapper.setCommandJudged(commandId, isJudged);

        try {
            List<String> commandUUIDs = Collections.singletonList(commandId);
            if (isJudged != 1) {
                return success;
            }
            //如果isJudged=1,则发送指令首次下发信号和RCP首次查询信号
            //指令首次下发
            try {
                stateHandler.sendCommandDistributeSignal(commandUUIDs);
            } catch (Exception e) {
                log.info(String.format("动态任务研判后任务首次指令下发c3出错，任务id: %d,commandUUIDs: %s",
                        queryCommandInfoByUUID(commandId).getTaskId(),
                        commandId));
            }
            //指令首次查询RCP
            try {
                stateHandler.sendCommandRcpQuerySignal(commandUUIDs);
            } catch (Exception e) {
                log.info(String.format("动态任务研判后任务首次查询RCP出错，任务id: %d,commandUUIDs: %s",
                        queryCommandInfoByUUID(commandId).getTaskId(),
                        commandId));
            }
        }catch (Exception e){
            throw new IllegalArgumentException("指令研判状态修改失败,无效的指令");
        }
        return success;
    }

    public Integer queryCommandTotalNum(Long taskId, String sourceIP, String sourcePort,
                                        String destinationIP, String destinationPort){
        return commandMapper.queryCommandTotalNum(taskId, sourceIP, sourcePort, destinationIP, destinationPort);
    }

    public void insertCommandHistory(String commandUUID) {
        //todo: 不update， insert加入uuid
//        commandMapper.updateCommandHistoryExpireTime(commandUUID);
        String logId = UUID.randomUUID().toString();
        commandMapper.insertCommandHistory(commandUUID, logId);
    }

    public void insertCommandHistoryBatch(List<TaskCommandInfo> commandIdList) {
        List<String> commandIds = ListUtils.newArrayListWithExpectedSize(commandIdList.size());
        commandIdList.forEach(item -> commandIds.add(item.getUUID()));
        //todo: 不update， insert加入uuid
//        commandMapper.updateCommandHistoryExpireTimeBatch(commandIds);
//        List<String> logIds;
//        logIds = ListUtils.newArrayListWithExpectedSize(commandIds.size());
//        for (int i = 0; i < commandIds.size(); i++) {
//            logIds.add(UUID.randomUUID().toString());
//        }
        //新建的loguuid拿commannd_id来定顶一会吧
        commandMapper.insertCommandHistoryBatch(commandIds);
    }
}
