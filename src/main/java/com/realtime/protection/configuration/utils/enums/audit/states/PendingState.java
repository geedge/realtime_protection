package com.realtime.protection.configuration.utils.enums.audit.states;

import com.realtime.protection.configuration.utils.enums.audit.AuditStatusEnum;

public class PendingState implements State {
    @Override
    public Boolean checkValidate(State newState) {
        return switch (AuditStatusEnum.getAuditStatusEnumByState(newState)) {
            case USING -> false;
            case PENDING, RETURNED, AUDITED -> true;
        };
    }
}
