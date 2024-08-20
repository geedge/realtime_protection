package com.realtime.protection.server.task.status.states;

import com.realtime.protection.configuration.utils.enums.StateEnum;
import com.realtime.protection.server.command.CommandService;
import com.realtime.protection.server.task.TaskService;
import com.realtime.protection.server.task.status.StateHandler;

public class RunningFailState extends StateHandler implements State {
    @Override
    public Boolean handle(State newState, CommandService commandService, TaskService taskService, Long taskId) {
        return switch (StateEnum.getStateEnumByState(newState)) {
            case PAUSED -> handlePause(commandService, taskId);
            case STOP -> handleStop(commandService, taskService, taskId);
            case FINISHED -> handleFinish(commandService, taskService, taskId);
            case FAILED -> handleFailed(commandService, taskService, taskId);
            case RUNNING_SUCCESS -> handleRunningSuccess(commandService, taskService, taskId);
            case RUNNING_PARTIAL_SUCCESS -> handleRunningPartialSuccess(commandService, taskService, taskId);
            default -> throw new IllegalStateException("错误的状态: " + StateEnum.getStateEnumByState(newState));
        };
    }
}