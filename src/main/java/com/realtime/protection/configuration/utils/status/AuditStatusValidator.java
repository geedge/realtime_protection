package com.realtime.protection.configuration.utils.status;

public class AuditStatusValidator {

    private final Integer auditStatusOriginal;

    public AuditStatusValidator(Integer auditStatusOriginal) {
        this.auditStatusOriginal = auditStatusOriginal;
    }

    public static AuditStatusValidator setOriginal(Integer auditStatusOriginal) {
        return new AuditStatusValidator(auditStatusOriginal);
    }

    public Boolean checkValidate(Integer auditStatusNow) {
        switch (auditStatusNow) {
            case 0, 1 -> {
                return auditStatusOriginal != 2;
            }
            case 2 -> {
                return auditStatusOriginal != 1;
            }
            default -> {
                return false;
            }
        }
    }
}
