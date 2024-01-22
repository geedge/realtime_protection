package com.realtime.protection.configuration.entity.task;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TaskCommandInfo {
    @Schema(description = "指令UUID", accessMode = Schema.AccessMode.READ_ONLY)
    @JsonProperty("uuid")
    private String UUID;

    @Schema(description = "任务ID", accessMode = Schema.AccessMode.READ_ONLY)
    @JsonProperty("task_id")
    private Long taskId;

    @Schema(description = "规则ID", hidden = true)
    @JsonProperty("rule_id")
    private Long ruleId;

    @Schema(description = "任务创建人名称", accessMode = Schema.AccessMode.READ_ONLY)
    @JsonProperty("task_create_username")
    private String taskCreateUsername;

    @Schema(description = "任务创建人处室", accessMode = Schema.AccessMode.READ_ONLY)
    @JsonProperty("task_create_depart")
    private String taskCreateDepart;

    @Schema(description = "任务创建人ID", accessMode = Schema.AccessMode.READ_ONLY)
    @JsonProperty("task_create_userid")
    private Integer taskCreateUserId;

    @Schema(description = "任务名称", example = "API测试任务")
    @NotNull(message = "任务名称不能为空")
    @JsonProperty("task_name")
    private String taskName;

    @Schema(description = "任务类型", example = "1")
    @NotNull(message = "任务类型不能为空")
    @JsonProperty("task_type")
    private Integer taskType;

    @Schema(description = "任务操作", example = "阻断")
    @NotNull(message = "任务操作不能为空。")
    @JsonProperty("task_act")
    private String taskAct;

    @Schema(description = "指令下发频率", example = "30")
    @NotNull(message = "指令下发频率不能为空。")
    @JsonProperty("frequency")
    private Integer frequency;

    @Schema(description = "任务开始时间", example = "2025-10-14T10:23:33")
    @NotNull(message = "任务开始时间不能为空。")
    @JsonProperty("start_time")
    private LocalDateTime startTime;

    @Schema(description = "任务结束时间", example = "2026-10-22T10:33:22")
    @NotNull(message = "指令结束时间不能为空。")
    @JsonProperty("end_time")
    private LocalDateTime endTime;

    @Schema(description = "指令是否生效", example = "false")
    @JsonProperty("is_valid")
    private Boolean isValid = true;

    @Schema(description = "五元组信息")
    @NotNull(message = "五元组信息不能为空。")
    @JsonProperty("five_tuple_with_mask")
    private FiveTupleWithMask fiveTupleWithMask;

    @Schema(description = "指令下发次数", accessMode = Schema.AccessMode.READ_ONLY)
    @JsonProperty("command_send_times")
    private Integer commandSentTimes;

    @Schema(description = "指令成功次数", accessMode = Schema.AccessMode.READ_ONLY)
    @JsonProperty("command_success_times")
    private Integer commandSuccessTimes;

    @Schema(description = "首次下发时间", accessMode = Schema.AccessMode.READ_ONLY)
    @JsonProperty("earliest_send_times")
    private LocalDateTime earliestSendTime;

    @Schema(description = "最新下发时间", accessMode = Schema.AccessMode.READ_ONLY)
    @JsonProperty("latest_send_times")
    private LocalDateTime latestSendTime;

    @Schema(description = "指令是否研判", example = "true")
    @JsonProperty("is_judeged")
    private Boolean isJudged = true;
    /*
      以下属性用于动态规则生成，不写入doris：t_command表
     */
    @Schema(description = "防御策略模板ID", accessMode = Schema.AccessMode.READ_ONLY)
    private Integer templateId;

    @Schema(description = "防护等级", accessMode = Schema.AccessMode.READ_ONLY)
    private Integer protectLevel;

    @Schema(description = "指令所属任务的运行状态", accessMode = Schema.AccessMode.READ_ONLY)
    private Integer taskStatus;

}
