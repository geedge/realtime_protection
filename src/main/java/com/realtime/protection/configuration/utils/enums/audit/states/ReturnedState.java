package com.realtime.protection.configuration.utils.enums.audit.states;

import com.realtime.protection.configuration.utils.enums.audit.AuditStatusEnum;

public class ReturnedState implements State {
    @Override
    public Boolean checkValidate(State newState) {
        return switch (AuditStatusEnum.getAuditStatusEnumByState(newState)) {
            case PENDING, RETURNED -> true;
            case AUDITED, USING -> false;
        };
    }
}
