package com.realtime.protection.configuration.entity.rule.staticrule;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class StaticRuleObject {
    @JsonProperty("static_rule_id")
    private int staticRuleId;

    @NotNull
    @JsonProperty("static_rule_name")
    private String staticRuleName;

    private LocalDateTime staticRuleCreateTime;
    private String StaticRuleCreateUsername;
    private int StaticRuleAuditStatus;
    private String StaticRuleCreateDepart;
    private int StaticRuleCreateUserId;
    private String StaticRuleSip;
    private String StaticRuleMsip;
    private int StaticRuleSport;
    private int StaticRuleMsport;
    private String StaticRuleDip;
    private String StaticRuleMdip;
    private int StaticRuleDport;
    private int StaticRuleMdport;
    private String StaticRuleProtocol;
    private String StaticRuleMprotocol;
    private String StaticRuleDns;
    private String StaticRuleURL;
    private int StaticRulePriority;
    private String StaticRuleRange;
    private int StaticRuleFrequency;
    private int StaticRuleProtectLevel;
}
