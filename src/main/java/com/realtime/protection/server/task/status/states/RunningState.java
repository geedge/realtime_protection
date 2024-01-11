package com.realtime.protection.server.task.status.states;

import com.realtime.protection.configuration.utils.enums.StateEnum;
import com.realtime.protection.configuration.utils.status.State;
import com.realtime.protection.server.command.CommandService;
import com.realtime.protection.server.task.TaskService;
import com.realtime.protection.server.task.status.StateHandler;

public class RunningState extends StateHandler implements State {
    @Override
    public Boolean handle(State newState, CommandService commandService, TaskService taskService, Long taskId) {
        return switch(StateEnum.getStateEnumByState(newState)) {
            case RUNNING -> true;
            case PAUSED -> handlePause(commandService, taskId);
            case STOP -> handleStop(commandService, taskId);
            case FINISHED -> handleFinish(commandService, taskId);
            case FAILED -> handleFailed(commandService, taskId);
            default -> throw new IllegalStateException("Unexpected value: " + StateEnum.getStateEnumByState(newState));
        };
    }
}
