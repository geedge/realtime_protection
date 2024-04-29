package com.realtime.protection.configuration.entity.task;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(description = "一个任务对象包含的所有信息")
public class Task {
    @JsonProperty("task_id")
    @Schema(description = "任务ID", accessMode = Schema.AccessMode.READ_ONLY)
    private Long taskId;

    @JsonProperty("task_display_id")
    @Schema(description = "任务显示ID", accessMode = Schema.AccessMode.READ_ONLY)
    private String taskDisplayId;

    @JsonProperty("task_name")
    @NotNull(message = "task_name字段不能为空。")
    @Schema(description = "任务名称", example = "静态任务")
    private String taskName;

    @JsonProperty("task_start_time")
    @NotNull(message = "task_start_time字段不能为空。")
    @Future(message = "task_start_time必须晚于当前时间。")
    @Schema(description = "任务开始时间，必须晚于当前时间", example = "2024-10-23T00:00:00")
    private LocalDateTime taskStartTime;

    @JsonProperty("task_end_time")
    @NotNull(message = "task_end_time字段不能为空。")
    @Future(message = "task_end_time必须晚于当前时间。")
    @Schema(description = "任务结束时间，必须晚于开始时间", example = "2024-10-24T00:00:00")
    private LocalDateTime taskEndTime;

    @JsonProperty("task_create_time")
    @Schema(hidden = true)
    private LocalDateTime taskCreateTime;

    @JsonProperty("task_modify_time")
    @Schema(hidden = true)
    private LocalDateTime taskModifyTime;

    @JsonProperty("task_type")
    @NotNull(message = "task_type字段不能为空。")
    @Schema(description = "任务类型，1为静态任务，2为实时任务，3为研判后任务", example = "1")
    private Integer taskType;

    @JsonProperty("task_act")
    @NotNull(message = "task_act字段不能为空。")
    @Schema(description = "任务行为，目前只能为【阻断】", example = "阻断")
    private String taskAct;

    @JsonProperty("task_create_username")
    @Schema(description = "任务创建人名称", accessMode = Schema.AccessMode.READ_ONLY)
    private String taskCreateUsername;

    @JsonProperty("task_create_depart")
    @Schema(description = "任务创建人处室", accessMode = Schema.AccessMode.READ_ONLY)
    private String taskCreateDepart;

    @JsonProperty("task_create_userid")
    @Schema(description = "任务创建人ID", accessMode = Schema.AccessMode.READ_ONLY)
    private Integer taskCreateUserId;

    @JsonProperty("static_rule_ids")
    @Schema(description = "静态规则ID列表，动态和静态至少存在1个规则", example = "[10, 12]")
    private List<Integer> staticRuleIds;

    @JsonProperty("dynamic_rule_ids")
    @Schema(description = "动态规则ID列表，动态和静态至少存在1个规则", example = "[20, 30]")
    private List<Integer> dynamicRuleIds;

    @JsonProperty("task_status")
    @Schema(description = "任务状态（0为未启动，1为生成中，2为运行中，3为暂停中，4为已停止，5为已结束，6为失败）", accessMode = Schema.AccessMode.READ_ONLY)
    private Integer taskStatus;

    @JsonProperty("task_audit_status")
    @Schema(description = "任务审核状态（0为未审核，1为已退回，2为已通过）", accessMode = Schema.AccessMode.READ_ONLY)
    private Integer taskAuditStatus;

    @JsonProperty("task_range")
    @NotNull(message = "task_range字段不能为空。")
    @Schema(description = "局点范围，由规则移动到任务", example = "1009")
    private String taskRange;
}
