package com.realtime.protection.server.task;

import com.realtime.protection.server.task.state.State;

public class StatusChanger {

    private final State state;

    public StatusChanger(State state) {
        this.state = state;
    }

    public static StatusChanger setOriginal(State original) {
        return new StatusChanger(original);
    }

    public Boolean changeState(State newState) {
        return this.state.handle(newState);
    }
}
