package com.realtime.protection.configuration.utils.status;

import lombok.Getter;

@Getter
public enum AuditStatus {
    PENDING(0),
    UNAUDITED(1),
    AUDITED(2);

    private final int auditStatus;

    AuditStatus(int auditStatus) {
        this.auditStatus = auditStatus;
    }

}
