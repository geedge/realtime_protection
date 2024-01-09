package com.realtime.protection.server.task.state;

public class StopState implements State {

    @Override
    public Boolean handle(State newState) {
        if (newState instanceof RunningState) {
            return handleRun();
        }

        return false;
    }

    public Boolean handleRun() {
        return true;
    }
}
