package com.realtime.protection.server.command;

import com.alibaba.excel.util.ListUtils;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.realtime.protection.configuration.entity.task.TaskCommandInfo;
import com.realtime.protection.configuration.utils.SqlSessionWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.function.Function;

@Service
@Slf4j
@DS("doris")
public class CommandService {

    private final CommandMapper commandMapper;
    private final SqlSessionWrapper sqlSessionWrapper;
    private static final int BatchSize = 100;

    public CommandService(CommandMapper commandMapper, SqlSessionWrapper sqlSessionWrapper) {
        this.commandMapper = commandMapper;
        this.sqlSessionWrapper = sqlSessionWrapper;
    }

    @DSTransactional
    public String createCommand(TaskCommandInfo commandInfo) {
        String uuid = commandMapper.queryCommandInfo(commandInfo);
        if (uuid != null) {
            return uuid;
        }

        commandInfo.setUUID(UUID.randomUUID().toString());
        commandMapper.createCommand(commandInfo);
        return commandInfo.getUUID();

    }

    public void createCommands(List<TaskCommandInfo> taskCommandInfos) {
        Function<CommandMapper, Function<List<TaskCommandInfo>, Boolean>> function = mapper -> list -> {
            List<TaskCommandInfo> taskCommandInfoBatch = ListUtils.newArrayListWithExpectedSize(BatchSize);
            for (TaskCommandInfo info : list) {
                info.setUUID(UUID.randomUUID().toString());
                taskCommandInfoBatch.add(info);
                if (taskCommandInfoBatch.size() < BatchSize) {
                    continue;
                }

                commandMapper.createCommands(taskCommandInfoBatch);
                taskCommandInfoBatch.clear();
            }

            if (!taskCommandInfoBatch.isEmpty()) {
                commandMapper.createCommands(taskCommandInfoBatch);
                taskCommandInfoBatch.clear();
            }

            return true;
        };

        sqlSessionWrapper.startBatchSession(CommandMapper.class, function, taskCommandInfos);
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
}
