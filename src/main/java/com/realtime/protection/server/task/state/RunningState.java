package com.realtime.protection.server.task.state;

public class RunningState implements State {
    @Override
    public Boolean handle(State newState) {
        if (newState instanceof RunningState) {
            return false;
        }

        if (newState instanceof PauseState) {
            return handlePause();
        }

        if (newState instanceof StopState) {
            return handleStop();
        }

        return false;
    }

    private Boolean handlePause() {
        return true;
    }

    private Boolean handleStop() {
        return true;
    }
}
