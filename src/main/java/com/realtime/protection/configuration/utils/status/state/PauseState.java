package com.realtime.protection.configuration.utils.status.state;

public class PauseState implements State {
    @Override
    public Boolean handle(State newState) {
        if (!(newState instanceof RunningState)) {
            return false;
        }

        return handleRun();
    }

    private Boolean handleRun() {
        return true;
    }
}
