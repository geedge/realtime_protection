package com.realtime.protection.server.command;

import com.alibaba.excel.util.ListUtils;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.realtime.protection.configuration.entity.task.TaskCommandInfo;
import com.realtime.protection.configuration.entity.whitelist.WhiteListObject;
import com.realtime.protection.configuration.utils.Counter;
import com.realtime.protection.configuration.utils.SqlSessionWrapper;
import com.realtime.protection.server.whitelist.WhiteListMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
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

    public CommandService(CommandMapper commandMapper, SqlSessionWrapper sqlSessionWrapper, Counter counter, WhiteListMapper whiteListMapper) {
        this.commandMapper = commandMapper;
        this.sqlSessionWrapper = sqlSessionWrapper;
        this.counter = counter;
        this.whiteListMapper = whiteListMapper;
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
            insertCommandHistory(commandInfo.getUUID());
            return commandInfo.getUUID();
        }

        commandInfo.setUUID(UUID.randomUUID().toString());
        commandMapper.createCommand(commandInfo);
        //写入历史表
        insertCommandHistory(commandInfo.getUUID());
        return commandInfo.getUUID();
    }

    public List<String> createCommands(List<TaskCommandInfo> taskCommandInfos) {

        List<String> commandUUIDs = ListUtils.newArrayListWithExpectedSize(taskCommandInfos.size());

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
                //因为createCommands只用于静态规则生成command，静态规则已经检查了白名单，所以不检查了
                commandMapper.createCommands(taskCommandInfoBatch);
                insertCommandHistoryBatch(taskCommandInfoBatch);
                taskCommandInfoBatch.clear();
            }

            if (!taskCommandInfoBatch.isEmpty()) {
                commandMapper.createCommands(taskCommandInfoBatch);
                insertCommandHistoryBatch(taskCommandInfoBatch);
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

    public Boolean setCommandJudged(String commandId, Boolean isValid) {
        return commandMapper.setCommandJudged(commandId, isValid);
    }

    public Integer queryCommandTotalNum(Long taskId, String sourceIP, String sourcePort,
                                        String destinationIP, String destinationPort){
        return commandMapper.queryCommandTotalNum(taskId, sourceIP, sourcePort, destinationIP, destinationPort);
    }

    public void insertCommandHistory(String commandUUID) {
        commandMapper.updateCommandHistoryExpireTime(commandUUID);
        commandMapper.insertCommandHistory(commandUUID);
    }

    public void insertCommandHistoryBatch(List<TaskCommandInfo> commandIdList) {
        List<String> commandIds = ListUtils.newArrayListWithExpectedSize(commandIdList.size());
        commandIdList.forEach(item -> commandIds.add(item.getUUID()));

        commandMapper.updateCommandHistoryExpireTimeBatch(commandIds);
        commandMapper.insertCommandHistoryBatch(commandIds);
    }
}
