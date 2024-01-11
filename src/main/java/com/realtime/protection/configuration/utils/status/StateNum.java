package com.realtime.protection.configuration.utils.status;

import lombok.Getter;

@Getter
public enum StateNum {
    PENDING(0),
    RUNNING(1),
    PAUSED(2),
    STOPPED(3),
    FAILED(4),
    FINISHED(5);

    private final int stateNum;

    StateNum(int stateNum) {
        this.stateNum = stateNum;
    }

}
