package com.realtime.protection.configuration.entity.rule.dynamicrule;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.realtime.protection.configuration.entity.defense.object.ProtectObject;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class DynamicRuleObject {

    @JsonProperty("dynamic_rule_id")
    private Integer dynamicRuleId;

    @NotNull
    @JsonProperty("dynamic_rule_name")
    private String dynamicRuleName;

    @JsonProperty("dynamic_rule_create_time")
    private LocalDateTime dynamicRuleCreateTime;
    @JsonProperty("dynamic_rule_modify_time")
    private LocalDateTime dynamicRuleModifyTime;

    @JsonProperty("dynamic_rule_create_username")
    private String dynamicRuleCreateUsername;

//    @JsonProperty("dynamic_rule_audit_status")
//    private Integer dynamicRuleAuditStatus;
    @JsonProperty("dynamic_rule_create_depart")
    private String dynamicRuleCreateDepart;

    //动态规则选择的防护对象ids？
    @JsonProperty("protect_object_ids")
    private List<Integer> protectObjectIds;
    @JsonProperty("protect_objects")
    private List<ProtectObject> protectObjects;


    //还没有建立外键
    @JsonProperty("dynamic_rule_create_user_id")
    private Integer dynamicRuleCreateUserId;
    @JsonProperty("dynamic_rule_used_task_id")
    private Integer dynamicRuleUsedTaskId;

    @JsonProperty("dynamic_rule_source_system")
    private Integer dynamicRuleSourceSystem;
    @JsonProperty("dynamic_rule_event_type")
    private Integer dynamicRuleEventType;
    @JsonProperty("dynamic_rule_protect_level")
    private Integer dynamicRuleProtectLevel;

    @JsonProperty("dynamic_rule_priority")
    private Integer dynamicRulePriority;
    @JsonProperty("dynamic_rule_range")
    private String dynamicRuleRange;
    @JsonProperty("dynamic_rule_frequency")
    private Integer dynamicRuleFrequency;

}
