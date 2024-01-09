package com.realtime.protection.server.task.state;

public interface State {

    Boolean handle(State newState);
}
