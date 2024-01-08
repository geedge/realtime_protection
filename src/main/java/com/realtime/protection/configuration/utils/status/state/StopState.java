package com.realtime.protection.configuration.utils.status.state;

public class StopState implements State {

    @Override
    public Boolean handle(State newState) {
        if (!(newState instanceof RunningState)) {
            return false;
        }

        return handleRun();
    }

    public Boolean handleRun() {
        return true;
    }
}
