package com.realtime.protection.server.task.state;

import com.baomidou.dynamic.datasource.annotation.DS;

public class PauseState implements State {
    @Override
    public Boolean handle(State newState) {
        if (newState instanceof RunningState) {
            return handleRun();
        }

        return false;
    }

    @DS("oracle")
    private Boolean handleRun() {
        return true;
    }
}
