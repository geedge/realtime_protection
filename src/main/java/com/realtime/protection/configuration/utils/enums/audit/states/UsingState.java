package com.realtime.protection.configuration.utils.enums.audit.states;

import com.realtime.protection.configuration.utils.enums.audit.AuditStatusEnum;

public class UsingState implements State {
    @Override
    public Boolean checkValidate(State newState) {
        return switch (AuditStatusEnum.getAuditStatusEnumByState(newState)) {
            case AUDITED, USING -> true;
            case PENDING, RETURNED -> false;
        };
    }
}
