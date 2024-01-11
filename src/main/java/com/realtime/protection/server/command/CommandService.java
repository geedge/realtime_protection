package com.realtime.protection.server.command;

import com.alibaba.excel.util.ListUtils;
import com.realtime.protection.configuration.entity.task.Command;
import com.realtime.protection.configuration.entity.task.TaskCommandInfo;
import com.realtime.protection.configuration.exception.DorisStartException;
import com.realtime.protection.configuration.utils.SqlSessionWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Function;

@Service
@Slf4j
public class CommandService {

    private final CommandMapper commandMapper;
    private final SqlSessionWrapper sqlSessionWrapper;
    private static final int BatchSize = 1000;
    private final Function<CommandMapper, Function<TaskCommandInfo, Void>> createCommandBatchFunction;

    public CommandService(CommandMapper commandMapper, SqlSessionWrapper sqlSessionWrapper) {
        this.commandMapper = commandMapper;
        this.sqlSessionWrapper = sqlSessionWrapper;
        this.createCommandBatchFunction = mapper -> info -> {
            if (info.getFrequency() == null) {
                Command command = Command.generateCommand(info, info.getStartTime());
                commandMapper.createCommand(command);
            }

            List<Command> commandBatch = ListUtils.newArrayListWithExpectedSize(BatchSize);
            LocalDateTime validTime = info.getStartTime();

            while (validTime.isBefore(info.getEndTime())) {
                Command command = Command.generateCommand(info, validTime);
                commandBatch.add(command);

                validTime = validTime.plusMinutes(info.getFrequency());

                if (commandBatch.size() < BatchSize) {
                    continue;
                }
                commandMapper.createCommands(commandBatch);
                commandBatch.clear();
            }

            if (!commandBatch.isEmpty()) {
                commandMapper.createCommands(commandBatch);
                commandBatch.clear();
            }

            log.debug(String.format("create all the commands from task(%d), rule(%d)",
                    info.getTaskId(), info.getRuleId()));
            return null;
        };
    }

    @Async
    public void createCommand(TaskCommandInfo commandInfo) throws DorisStartException {
        try {
            sqlSessionWrapper.startBatchSession(CommandMapper.class, createCommandBatchFunction, commandInfo);
        } catch (Exception e) {
            throw new DorisStartException(e);
        }
    }

    @Async
    public void createCommands(List<TaskCommandInfo> taskCommandInfos) throws DorisStartException {
        Function<CommandMapper, Function<List<TaskCommandInfo>, Void>> function = mapper -> list -> {
            if (list == null || list.isEmpty()) {
                return null;
            }

            for (TaskCommandInfo info : list) {
                createCommandBatchFunction.apply(mapper).apply(info);
            }
            return null;
        };

        try {
            sqlSessionWrapper.startBatchSession(CommandMapper.class, function, taskCommandInfos);
        } catch (Exception e) {
            throw new DorisStartException(e);
        }
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
