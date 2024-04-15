package com.realtime.protection.configuration.utils.status;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AuditStatusValidator {

    private final Integer auditStatusOriginal;

    public AuditStatusValidator(Integer auditStatusOriginal) {
        this.auditStatusOriginal = auditStatusOriginal;
    }

    public static AuditStatusValidator setOriginal(Integer auditStatusOriginal) {
        return new AuditStatusValidator(auditStatusOriginal);
    }

    public Boolean checkValidate(Integer newAuditStatus) {
        switch (newAuditStatus) {
            case 0, 1 -> {
                return auditStatusOriginal != 2;
            }
            case 2 -> {
                return auditStatusOriginal != 1;
            }
            default -> {
                log.debug("欲修改的审核状态不正确，需要使用正确的审核状态，当前的审核状态：{}", auditStatusOriginal);
                return false;
            }
        }
    }
}
