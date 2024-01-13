package com.realtime.protection.server.command;

import com.alibaba.excel.util.ListUtils;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.realtime.protection.configuration.entity.task.Command;
import com.realtime.protection.configuration.entity.task.TaskCommandInfo;
import com.realtime.protection.configuration.exception.DorisStartException;
import com.realtime.protection.configuration.utils.SqlSessionWrapper;
import com.realtime.protection.configuration.utils.enums.StateEnum;
import com.realtime.protection.server.task.TaskService;
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
    private final TaskService taskService;
    private final SqlSessionWrapper sqlSessionWrapper;
    private static final int BatchSize = 100;
    private final Function<CommandMapper, Function<TaskCommandInfo, Void>> createCommandBatchFunction;

    public CommandService(CommandMapper commandMapper, TaskService taskService, SqlSessionWrapper sqlSessionWrapper) {
        this.commandMapper = commandMapper;
        this.taskService = taskService;
        this.sqlSessionWrapper = sqlSessionWrapper;
        this.createCommandBatchFunction = mapper -> info -> {
            if (info.getFrequency() == null) {
                Command command = Command.generateCommand(info, info.getStartTime());
                mapper.createCommand(command);
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
                mapper.createCommands(commandBatch);
                commandBatch.clear();
            }

            if (!commandBatch.isEmpty()) {
                mapper.createCommands(commandBatch);
                commandBatch.clear();
            }

            log.debug(String.format("在task(%d)和rule(%d)中构建了全部指令",
                    info.getTaskId(), info.getRuleId()));
            return null;
        };
    }

    @Async
    @DS("doris")
    public void createCommand(TaskCommandInfo commandInfo) throws DorisStartException {
        try {
            sqlSessionWrapper.startBatchSession(CommandMapper.class, createCommandBatchFunction, commandInfo);
            taskService.changeTaskStatus(commandInfo.getTaskId(), StateEnum.RUNNING.getStateNum());
        } catch (Exception e) {
            throw new DorisStartException(e, commandInfo.getTaskId());
        }
    }

    @Async
    @DS("doris")
    public void createCommands(List<TaskCommandInfo> taskCommandInfos) throws DorisStartException {
        Function<CommandMapper, Function<List<TaskCommandInfo>, Void>> function = mapper -> list -> {
            if (list == null || list.isEmpty()) {
                return null;
            }

            for (TaskCommandInfo info : list) {
                createCommandBatchFunction.apply(mapper).apply(info);
            }

            taskService.changeTaskStatus(list.get(0).getTaskId(), StateEnum.RUNNING.getStateNum());
            return null;
        };

        try {
            sqlSessionWrapper.startBatchSession(CommandMapper.class, function, taskCommandInfos);
        } catch (Exception e) {
            TaskCommandInfo info = null;
            if (taskCommandInfos != null) {
                info = taskCommandInfos.get(0);
            }
            Long taskId = null;
            if (info != null) {
                taskId = info.getTaskId();
            }
            throw new DorisStartException(e, taskId);
        }
    }

    @DS("doris")
    public Boolean startCommandsByTaskId(Long taskId) {
        return commandMapper.startCommandsByTaskId(taskId);
    }

    @DS("doris")
    public Boolean stopCommandsByTaskId(Long taskId) {
        return commandMapper.stopCommandsByTaskId(taskId);
    }

    @DS("doris")
    public Boolean removeCommandsByTaskId(Long taskId) {
        return commandMapper.removeCommandsByTaskId(taskId);
    }
}
