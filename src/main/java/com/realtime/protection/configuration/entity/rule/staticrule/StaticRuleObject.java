package com.realtime.protection.configuration.entity.rule.staticrule;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
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
    @ExcelIgnore
    private Integer staticRuleId;

    @NotNull
    @JsonProperty("static_rule_name")
    @ExcelProperty("名称")
    private String staticRuleName;

    @JsonProperty("static_rule_create_time")
    @ExcelIgnore
    private LocalDateTime staticRuleCreateTime;
    @JsonProperty("static_rule_modify_time")
    @ExcelIgnore
    private LocalDateTime staticRuleModifyTime;

    @JsonProperty("static_rule_create_username")
    @ExcelIgnore
    private String staticRuleCreateUsername;
    @JsonProperty("static_rule_audit_status")
    @ExcelIgnore
    private Integer staticRuleAuditStatus;
    @JsonProperty("static_rule_create_depart")
    @ExcelIgnore
    private String staticRuleCreateDepart;
    @JsonProperty("static_rule_create_user_id")
    @ExcelIgnore
    private Integer staticRuleCreateUserId;
    @JsonProperty("static_rule_used_task_id")
    @ExcelIgnore
    private Integer staticRuleUsedTaskId;

    @JsonProperty("static_rule_sip")
    @ExcelProperty("源IP地址")
    private String staticRuleSip;
    @JsonProperty("static_rule_msip")
    @ExcelProperty("源IP地址掩码")
    private String staticRuleMsip;
    @JsonProperty("static_rule_sport")
    @ExcelProperty("源端口")
    private Integer staticRuleSport;
    @JsonProperty("static_rule_msport")
    @ExcelProperty("源端口掩码")
    private Integer staticRuleMsport;
    @JsonProperty("static_rule_dip")
    @ExcelProperty("目的IP地址")
    private String staticRuleDip;
    @JsonProperty("static_rule_mdip")
    @ExcelProperty("目的IP地址掩码")
    private String staticRuleMdip;
    @JsonProperty("static_rule_dport")
    @ExcelProperty("目的端口")
    private Integer staticRuleDport;
    @JsonProperty("static_rule_mdport")
    @ExcelProperty("目的端口掩码")
    private Integer staticRuleMdport;
    @JsonProperty("static_rule_protocol")
    @ExcelProperty("协议")
    private String staticRuleProtocol;
    @JsonProperty("static_rule_mprotocol")
    @ExcelProperty("协议掩码")
    private String staticRuleMprotocol;
    @JsonProperty("static_rule_dns")
    @ExcelProperty("DNS")
    private String staticRuleDns;
    @JsonProperty("static_rule_url")
    @ExcelProperty("URL")
    private String staticRuleURL;

    @JsonProperty("static_rule_priority")
    @ExcelProperty("优先级")
    private Integer staticRulePriority;
    @JsonProperty("static_rule_range")
    @ExcelProperty("范围")
    private String staticRuleRange;
    @JsonProperty("static_rule_frequency")
    @ExcelProperty("频率")
    private Integer staticRuleFrequency;

}
