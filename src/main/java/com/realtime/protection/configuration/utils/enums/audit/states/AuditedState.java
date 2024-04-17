package com.realtime.protection.configuration.utils.enums.audit.states;

import com.realtime.protection.configuration.utils.enums.audit.AuditStatusEnum;

public class AuditedState implements State {
    @Override
    public Boolean checkValidate(State newState) {
        return switch (AuditStatusEnum.getAuditStatusEnumByState(newState)) {
            case RETURNED -> false;
            case PENDING, USING, AUDITED -> true;
        };
    }
}
