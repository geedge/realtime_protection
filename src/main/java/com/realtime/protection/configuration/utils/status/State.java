package com.realtime.protection.configuration.utils.status;

import com.realtime.protection.configuration.exception.DorisStartException;
import com.realtime.protection.server.command.CommandService;
import com.realtime.protection.server.task.TaskService;

public interface State {
    Boolean handle(State newState, CommandService commandService, TaskService taskService, Long taskId) throws DorisStartException;
}
