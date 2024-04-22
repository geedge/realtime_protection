package com.realtime.protection.configuration.utils.enums.audit;

import com.realtime.protection.configuration.utils.enums.audit.states.*;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public enum AuditStatusEnum {
    PENDING(0, new PendingState()),   // 未审核状态
    RETURNED(1, new ReturnedState()),  // 退回状态
    AUDITED(2, new AuditedState()),   // 已审核状态
    USING(3, new UsingState());     // 使用中状态

    private final Integer num;
    private final State state;
    private static final Map<Integer, State> NumToStateMap = new HashMap<>();
    private static final Map<State, Integer> StateToNumMap = new HashMap<>();
    private static final Map<State, AuditStatusEnum> StateToAuditStatusEnumMap = new HashMap<>();

    static {
        for (AuditStatusEnum status : AuditStatusEnum.values()) {
            NumToStateMap.put(status.getNum(), status.getState());
            StateToNumMap.put(status.getState(), status.getNum());
            StateToAuditStatusEnumMap.put(status.getState(), status);
        }
    }

    AuditStatusEnum(int auditStatus, State state) {
        this.num = auditStatus;
        this.state = state;
    }

    public static State getStateByNum(Integer auditStatusNum) {
        if (auditStatusNum == null) {
            return null;
        }
        return NumToStateMap.get(auditStatusNum);
    }

    public static Integer getNumByState(State state) {
        if (state == null) {
            return null;
        }
        return StateToNumMap.get(state);
    }

    public static AuditStatusEnum getAuditStatusEnumByState(State state) {
        if (state == null) {
            return null;
        }
        return StateToAuditStatusEnumMap.get(state);
    }
}
