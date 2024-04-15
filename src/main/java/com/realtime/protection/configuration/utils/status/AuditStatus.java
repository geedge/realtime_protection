package com.realtime.protection.configuration.utils.status;

import lombok.Getter;

@Getter
public enum AuditStatus {
    PENDING(0),   // 未审核状态
    RETURNED(1),  // 退回状态
    AUDITED(2),   // 已审核状态
    DRAFT(3);     // 草稿

    private final int auditStatus;

    AuditStatus(int auditStatus) {
        this.auditStatus = auditStatus;
    }

}
