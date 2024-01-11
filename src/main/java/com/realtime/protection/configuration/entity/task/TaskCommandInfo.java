package com.realtime.protection.configuration.entity.task;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TaskCommandInfo {
    private FiveTupleWithMask fiveTupleWithMask;

    private Long taskId;
    private Long ruleId;

    // 额外字段
    private String operation;
    private Integer frequency;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
