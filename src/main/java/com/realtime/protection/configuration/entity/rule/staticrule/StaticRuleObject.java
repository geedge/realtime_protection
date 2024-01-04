package com.realtime.protection.configuration.entity.rule.staticrule;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StaticRuleObject {
    @JsonProperty("static_rule_id")
    private Integer staticRuleId;

    @NotNull
    @JsonProperty("static_rule_name")
    private String staticRuleName;

    @JsonProperty("static_rule_create_time")
    private LocalDateTime staticRuleCreateTime;
    @JsonProperty("static_rule_modify_time")
    private LocalDateTime staticRuleModifyTime;

    @JsonProperty("static_rule_create_username")
    private String staticRuleCreateUsername;
    @JsonProperty("static_rule_audit_status")
    private Integer staticRuleAuditStatus;
    @JsonProperty("static_rule_create_depart")
    private String staticRuleCreateDepart;
    @JsonProperty("static_rule_create_user_id")
    private Integer staticRuleCreateUserId;
    @JsonProperty("static_rule_used_task_id")
    private Integer staticRuleUsedTaskId;

    @JsonProperty("static_rule_sip")
    private String staticRuleSip;
    @JsonProperty("static_rule_msip")
    private String staticRuleMsip;
    @JsonProperty("static_rule_sport")
    private Integer staticRuleSport;
    @JsonProperty("static_rule_msport")
    private Integer staticRuleMsport;
    @JsonProperty("static_rule_dip")
    private String staticRuleDip;
    @JsonProperty("static_rule_mdip")
    private String staticRuleMdip;
    @JsonProperty("static_rule_dport")
    private Integer staticRuleDport;
    @JsonProperty("static_rule_mdport")
    private Integer staticRuleMdport;
    @JsonProperty("static_rule_protocol")
    private String staticRuleProtocol;
    @JsonProperty("static_rule_mprotocol")
    private String staticRuleMprotocol;
    @JsonProperty("static_rule_dns")
    private String staticRuleDns;
    @JsonProperty("static_rule_url")
    private String staticRuleURL;

    @JsonProperty("static_rule_priority")
    private Integer staticRulePriority;
    @JsonProperty("static_rule_range")
    private String staticRuleRange;
    @JsonProperty("static_rule_frequency")
    private Integer staticRuleFrequency;
    @JsonProperty("static_rule_protect_level")
    private Integer staticRuleProtectLevel;
}
