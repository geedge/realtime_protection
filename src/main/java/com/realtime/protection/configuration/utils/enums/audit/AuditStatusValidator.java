package com.realtime.protection.configuration.utils.enums.audit;

import com.realtime.protection.configuration.utils.enums.audit.states.State;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AuditStatusValidator {

    private final State auditStatusOriginal;

    public AuditStatusValidator(Integer auditStatusOriginal) {
        this.auditStatusOriginal = AuditStatusEnum.getStateByNum(auditStatusOriginal);
    }

    public static AuditStatusValidator setOriginal(Integer auditStatusOriginal) {
        return new AuditStatusValidator(auditStatusOriginal);
    }

    public Boolean checkValidate(Integer newAuditStatus) {
        State newState = AuditStatusEnum.getStateByNum(newAuditStatus);
        if (newState == null) {
            return false;
        }
        return auditStatusOriginal.checkValidate(newState);
    }
}
