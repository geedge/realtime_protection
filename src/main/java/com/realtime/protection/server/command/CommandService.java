package com.realtime.protection.server.command;

import com.alibaba.excel.util.ListUtils;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.realtime.protection.configuration.entity.task.TaskCommandInfo;
import com.realtime.protection.configuration.utils.SqlSessionWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
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

    public Boolean createCommand(TaskCommandInfo commandInfo) {
        return commandMapper.createCommand(commandInfo);
    }

    public void createCommands(List<TaskCommandInfo> taskCommandInfos) {
        Function<CommandMapper, Function<List<TaskCommandInfo>, Boolean>> function = mapper -> list -> {
            List<TaskCommandInfo> taskCommandInfoBatch = ListUtils.newArrayListWithExpectedSize(BatchSize);
            for (TaskCommandInfo info : list) {
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

    public List<TaskCommandInfo> queryCommandInfoByTaskId(Long taskId) {
        return commandMapper.queryCommandInfoByTaskId(taskId);
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
}
