package com.realtime.protection.server.task.status.states;

import com.realtime.protection.configuration.utils.status.State;
import com.realtime.protection.server.command.CommandService;
import com.realtime.protection.server.task.TaskService;

public class FinishedState implements State {
    @Override
    public Boolean handle(State newState, CommandService commandService, TaskService taskService, Long taskId) {
        return newState instanceof FinishedState;
    }
}
