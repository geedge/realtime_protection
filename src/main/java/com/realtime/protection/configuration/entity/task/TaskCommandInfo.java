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

    @Schema(description = "任务显示ID", accessMode = Schema.AccessMode.READ_ONLY)
    @JsonProperty("task_display_id")
    private String taskDisplayId;

    @Schema(description = "规则ID", hidden = true)
    @JsonProperty("rule_id")
    private Long ruleId;

    @Schema(description = "任务创建人名称", accessMode = Schema.AccessMode.READ_ONLY)
    @JsonProperty("task_create_username")
    private String taskCreateUsername;

    @Schema(description = "任务创建人处室", example = "XXX")
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

    @Schema(description = "局点", example = "123456")
    @JsonProperty("distribute_point")
    private String distributePoint;

    @Schema(description = "事件类型", example = "DDOS")
    @JsonProperty("event_type")
    private String eventType;

    @Schema(description = "指令下发频率", example = "30")
//    @NotNull(message = "指令下发频率不能为空。")
    @JsonProperty("frequency")
    private Integer frequency = 3;

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

    @Schema(description = "指令是否已研判,0待研判，1下发，2本次忽略，3永久忽略", example = "true")
    @JsonProperty("is_judged")
    private Integer isJudged = 1;

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
    /*
      以下属性用于动态规则生成，不写入doris：t_command表
     */
    @Schema(description = "防御策略模板ID", accessMode = Schema.AccessMode.READ_ONLY)
    private Integer templateId;

    @Schema(description = "防护等级,1代表low、2代表medium、3代表high", accessMode = Schema.AccessMode.READ_ONLY)
    private Integer protectLevel;

    @Schema(description = "指令所属任务的运行状态", accessMode = Schema.AccessMode.READ_ONLY)
    private Integer taskStatus;

    @Schema(description = "指令展示id", accessMode = Schema.AccessMode.READ_ONLY)
    private String displayId;

    @Schema(description = "规则名称", accessMode = Schema.AccessMode.READ_ONLY)
    private String ruleName;

    @Schema(description = "源ip整数形式", accessMode = Schema.AccessMode.READ_ONLY)
    private Long sipInt;

    @Schema(description = "目的ip整数形式", accessMode = Schema.AccessMode.READ_ONLY)
    private Long dipInt;

    private Long rcpHitCount;

    private Long totalPacketNum;


//    private String hashValue;

    // 复制构造函数
    public void copyTaskCommandInfo(TaskCommandInfo original) {
        this.UUID = original.UUID;
        this.taskId = original.taskId;
        this.ruleId = original.ruleId;
        this.taskCreateUsername = original.taskCreateUsername;
        this.taskCreateDepart = original.taskCreateDepart;
        this.taskCreateUserId = original.taskCreateUserId;
        this.taskName = original.taskName;
        this.taskType = original.taskType;
        this.taskAct = original.taskAct;
        this.distributePoint = original.distributePoint;
        this.eventType = original.eventType;
        this.frequency = original.frequency;
        this.startTime = original.startTime;
        this.endTime = original.endTime;
        this.isValid = original.isValid;
        this.isJudged = original.isJudged;
        this.fiveTupleWithMask = original.fiveTupleWithMask;
        this.commandSentTimes = original.commandSentTimes;
        this.commandSuccessTimes = original.commandSuccessTimes;
        this.earliestSendTime = original.earliestSendTime;
        this.latestSendTime = original.latestSendTime;
        this.templateId = original.templateId;
        this.protectLevel = original.protectLevel;
        this.taskStatus = original.taskStatus;
        this.ruleName = original.ruleName;
        this.displayId = original.displayId;
        this.sipInt = original.sipInt;
        this.dipInt = original.dipInt;
    }

    public void setProtocolNum() {
        if (this.fiveTupleWithMask == null) {
            return;
        }
        this.fiveTupleWithMask.setProtocolNum();
    }

    public void setMask() {
        if (this.fiveTupleWithMask == null) {
            return;
        }
        this.fiveTupleWithMask.setMask();
    }
}
