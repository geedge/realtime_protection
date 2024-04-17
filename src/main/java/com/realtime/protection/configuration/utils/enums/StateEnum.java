package com.realtime.protection.configuration.utils.enums;

import com.realtime.protection.server.task.status.states.*;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public enum StateEnum {
    // 仅需修改此处即可将任务状态以及对应的State和Num进行对应
    // 使用状态模式进行任务状态的管理和切换
    PENDING(0, new PendingState()),
    GENERATING(1, new GeneratingState()),
    RUNNING(2, new RunningState()),
    PAUSED(3, new PauseState()),
    STOP(4, new StopState()),
    FINISHED(5, new FinishedState()),
    FAILED(6, new FailedState());
    // ----------------------------------------------

    private final State state;
    private final Integer stateNum;
    private static final Map<Integer, State> numToStateMap = new HashMap<>();
    private static final Map<State, Integer> stateToNumMap = new HashMap<>();
    private static final Map<State, StateEnum> stateToStateEnumMap = new HashMap<>();

    static {
        for (StateEnum stateEnum : StateEnum.values()) {
            numToStateMap.put(stateEnum.getStateNum(), stateEnum.getState());
            stateToNumMap.put(stateEnum.getState(), stateEnum.getStateNum());
            stateToStateEnumMap.put(stateEnum.getState(), stateEnum);
        }
    }

    StateEnum(int stateNum, State state) {
        this.stateNum = stateNum;
        this.state = state;
    }

    public static State getStateByNum(Integer stateNum) {
        return numToStateMap.get(stateNum);
    }

    public static Integer getNumByState(State state) {
        return stateToNumMap.get(state);
    }

    public static StateEnum getStateEnumByState(State state) {
        return stateToStateEnumMap.get(state);
    }

    public static StateEnum getStateEnumByNum(Integer stateNum) {
        return getStateEnumByState(getStateByNum(stateNum));
    }
}
