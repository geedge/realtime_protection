package com.realtime.protection.configuration.entity.task;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Task {
    @JsonProperty("task_id")
    private Integer taskId;

    @JsonProperty("task_name")
    @NotNull(message = "task_name should not be empty")
    private String taskName;

    @JsonProperty("task_start_time")
    @NotNull(message = "task_start_time should not be empty")
    private LocalDateTime taskStartTime;

    @JsonProperty("task_end_time")
    @NotNull(message = "task_end_time should not be empty")
    private LocalDateTime taskEndTime;

    @JsonProperty("task_create_time")
    private LocalDateTime taskCreateTime;

    @JsonProperty("task_modify_time")
    private LocalDateTime taskModifyTime;

    @JsonProperty("task_type")
    @NotNull(message = "task_type should not be empty")
    private String taskType;

    @JsonProperty("task_act")
    @NotNull(message = "task_act should not be empty")
    private String taskAct;

    // These three attributes will be gained by user in the future
    // -----------------------------------------------------------
    @JsonProperty("task_create_username")
    @NotNull(message = "task_create_username should not be empty")
    private String taskCreateUsername;

    @JsonProperty("task_create_depart")
    @NotNull(message = "task_create_depart should not be empty")
    private String taskCreateDepart;

    @JsonProperty("task_create_userid")
    @NotNull(message = "task_create_userid should not be empty")
    private Integer taskCreateUserId;
    // -----------------------------------------------------------

    @JsonProperty("static_rule_ids")
    private Integer[] staticRuleIds;

    @JsonProperty("dynamic_rule_ids")
    private Integer[] dynamicRuleIds;

    @JsonProperty("protect_object_ids")
    private Integer[] protectObjectIds;

    @JsonProperty("task_status")
    private Integer taskStatus;

    @JsonProperty("task_audit_status")
    private Integer taskAuditStatus;
}
