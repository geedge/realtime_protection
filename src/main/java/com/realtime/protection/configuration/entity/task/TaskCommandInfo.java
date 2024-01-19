package com.realtime.protection.configuration.entity.task;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TaskCommandInfo {
    @Schema(description = "指令UUID", accessMode = Schema.AccessMode.READ_ONLY)
    private String UUID;

    @Schema(description = "任务ID", accessMode = Schema.AccessMode.READ_ONLY)
    private Long taskId;

    @Schema(description = "规则ID", hidden = true)
    private Long ruleId;

    @Schema(description = "任务创建人名称", accessMode = Schema.AccessMode.READ_ONLY)
    private String taskCreateUsername;

    @Schema(description = "任务创建人处室", accessMode = Schema.AccessMode.READ_ONLY)
    private String taskCreateDepart;

    @Schema(description = "任务创建人ID", accessMode = Schema.AccessMode.READ_ONLY)
    private Integer taskCreateUserId;

    @Schema(description = "任务名称", example = "API测试任务")
    @NotNull(message = "任务名称不能为空")
    private String taskName;

    @Schema(description = "任务类型", example = "1")
    @NotNull(message = "任务类型不能为空")
    private Integer taskType;

    @Schema(description = "任务操作", example = "阻断")
    @NotNull(message = "任务操作不能为空。")
    private String taskAct;

    @Schema(description = "指令下发频率", example = "30")
    @NotNull(message = "指令下发频率不能为空。")
    private Integer frequency;

    @Schema(description = "任务开始时间", example = "2025-10-14T10:23:33")
    @NotNull(message = "任务开始时间不能为空。")
    private LocalDateTime startTime;

    @Schema(description = "任务结束时间", example = "2026-10-22T10:33:22")
    @NotNull(message = "指令结束时间不能为空。")
    private LocalDateTime endTime;

    @Schema(description = "指令是否生效", example = "false")
    private Boolean isValid = true;

    @Schema(description = "五元组信息")
    @NotNull(message = "五元组信息不能为空。")
    private FiveTupleWithMask fiveTupleWithMask;

    @Schema(description = "指令下发次数", accessMode = Schema.AccessMode.READ_ONLY)
    private Integer commandSentTimes;

    @Schema(description = "指令成功次数", accessMode = Schema.AccessMode.READ_ONLY)
    private Integer commandSuccessTimes;

    @Schema(description = "首次下发时间", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime earliestSendTime;

    @Schema(description = "最新下发时间", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime latestSendTime;

    @Schema(description = "防御策略模板ID", accessMode = Schema.AccessMode.READ_ONLY)
    private Integer templateId;

    @Schema(description = "防护等级", accessMode = Schema.AccessMode.READ_ONLY)
    private Integer protectLevel;
}
