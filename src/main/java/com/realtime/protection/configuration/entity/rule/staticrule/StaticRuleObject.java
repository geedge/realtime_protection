package com.realtime.protection.configuration.entity.rule.staticrule;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.realtime.protection.configuration.utils.enums.audit.AuditStatusEnum;
import com.realtime.protection.configuration.utils.enums.audit.AuditStatusValidator;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StaticRuleObject {
    @JsonProperty("static_rule_id")
    @ExcelIgnore
    @Schema(description = "静态规则ID", accessMode = Schema.AccessMode.READ_ONLY)
    private Integer staticRuleId;

    @JsonProperty("static_rule_display_id")
    @ExcelIgnore
    @Schema(description = "静态规则显示ID", accessMode = Schema.AccessMode.READ_ONLY)
    private String staticRuleDisplayId;

    @NotNull
    @JsonProperty("static_rule_name")
    @ExcelProperty("名称")
    @Schema(description = "静态规则名称", example = "静态规则测试", requiredMode = Schema.RequiredMode.REQUIRED)
    private String staticRuleName;

    @JsonProperty("static_rule_create_time")
    @ExcelIgnore
    @Schema(hidden = true)
    private LocalDateTime staticRuleCreateTime;
    @JsonProperty("static_rule_modify_time")
    @ExcelIgnore
    @Schema(hidden = true)
    private LocalDateTime staticRuleModifyTime;

    @JsonProperty("static_rule_create_username")
    @ExcelIgnore
    @Schema(description = "静态规则创建用户名称")
    private String staticRuleCreateUsername;
    @JsonProperty("static_rule_audit_status")
    @ExcelIgnore
    @Schema(description = "静态规则审核状态（0为未审核，1为已退回，2为审核通过）", example = "2")
    private Integer auditStatus;
    @JsonProperty("static_rule_create_depart")
    @ExcelIgnore
    @Schema(description = "静态规则创建用户所属部门")
    private String staticRuleCreateDepart;
    @JsonProperty("static_rule_create_user_id")
    @ExcelIgnore
    @Schema(description = "静态规则创建人ID")
    private Integer staticRuleCreateUserId;
    @JsonProperty("static_rule_used_task_id")
    @ExcelIgnore
    @Schema(hidden = true)
    private Integer staticRuleUsedTaskId;
    @JsonProperty("static_rule_used_task_status")
    @ExcelIgnore
    @Schema(hidden = true)
    private Integer usedTaskStatus;

    @JsonProperty("static_rule_sip")
    @ExcelProperty("源IP地址")
    @Pattern(regexp = "^(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})$", message = "Invalid IPv4 Address")
    @Schema(description = "源IP地址", example = "1.1.1.1")
    private String staticRuleSip;
    @JsonProperty("static_rule_msip")
    @Pattern(regexp = "^(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})$", message = "Invalid IPv4 Address")
    @ExcelProperty("源IP地址掩码")
    @Schema(description = "源IP地址掩码", example = "255.255.255.0")
    private String staticRuleMsip;
    @JsonProperty("static_rule_sport")
    @ExcelProperty("源端口")
    @Max(value = 65535, message = "port should not be more than 65535")
    @Min(value = 1, message = "port should not be less than 1")
    @Schema(description = "源端口", example = "8080")
    private Integer staticRuleSport;
    @JsonProperty("static_rule_msport")
    @ExcelProperty("源端口掩码")
    @Max(value = 65535, message = "port should not be more than 65535")
    @Min(value = 1, message = "port should not be less than 1")
    @Schema(description = "源端口掩码", example = "65535")
    private Integer staticRuleMsport;
    @JsonProperty("static_rule_dip")
    @Pattern(regexp = "^(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})$", message = "Invalid IPv4 Address")
    @ExcelProperty("目的IP地址")
    @Schema(description = "目的IP地址", example = "2.2.2.2")
    private String staticRuleDip;
    @JsonProperty("static_rule_mdip")
    @ExcelProperty("目的IP地址掩码")
    @Pattern(regexp = "^(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})$", message = "Invalid IPv4 Address")
    @Schema(description = "目的IP地址掩码", example = "255.255.255.0")
    private String staticRuleMdip;
    @JsonProperty("static_rule_dport")
    @ExcelProperty("目的端口")
    @Max(value = 65535, message = "port should not be more than 65535")
    @Min(value = 1, message = "port should not be less than 1")
    @Schema(description = "目的端口", example = "8080")
    private Integer staticRuleDport;
    @JsonProperty("static_rule_mdport")
    @ExcelProperty("目的端口掩码")
    @Max(value = 65535, message = "port should not be more than 65535")
    @Min(value = 1, message = "port should not be less than 1")
    @Schema(description = "目的端口掩码", example = "65535")
    private Integer staticRuleMdport;
    @JsonProperty("static_rule_protocol")
    @ExcelProperty("协议")
    @Schema(description = "协议", example = "TCP")
    private String staticRuleProtocol;
    @JsonProperty("static_rule_mprotocol")
    @ExcelProperty("协议掩码")
    @Schema(description = "协议掩码", example = "TCP")
    private String staticRuleMprotocol;
    @JsonProperty("static_rule_dns")
    @ExcelProperty("域名")
    @Schema(description = "DNS", example = "www.baidu.com")
    private String staticRuleDns;
    @JsonProperty("static_rule_url")
    @ExcelProperty("URL")
    @Schema(description = "URL", example = "www.baidu.com")
    private String staticRuleURL;

    @JsonProperty("static_rule_priority")
    @ExcelIgnore
    @Max(value = 3)
    @Min(value = 1)
    @Schema(description = "优先级,1代表高2代表中3代表低", example = "1")
    private Integer staticRulePriority = 1;
//    @JsonProperty("static_rule_range")
//    @ExcelProperty("范围")
//    @Schema(description = "范围", example = "北京", requiredMode = Schema.RequiredMode.REQUIRED)
//    private String staticRuleRange;
    @JsonProperty("static_rule_frequency")
    @ExcelIgnore
    @Min(value = 1)
    @Schema(description = "频率，最低为1", example = "1")
    private Integer staticRuleFrequency = 2;

    @JsonProperty("audit_user_name")
    @ExcelIgnore
    @Schema(description = "审核用户名称", example = "user11")
    private String auditUserName;
    @JsonProperty("audit_user_id")
    @ExcelIgnore
    @Schema(description = "审核用户id", example = "11111")
    private String auditUserId;
    @JsonProperty("audit_user_depart")
    @ExcelIgnore
    @Schema(description = "审核用户部门", example = "部门1")
    private String auditUserDepart;

    @Schema(description = "事件类型", example = "DDos")
    private String eventType;


    public void checkAuditStatusValidate(AuditStatusEnum newAuditStatus) {
        if (!List.of(AuditStatusEnum.AUDITED.getNum(), AuditStatusEnum.USING.getNum()).contains(this.getAuditStatus())) {
            throw new IllegalArgumentException("规则《" + this.getStaticRuleName() + "》原审批状态非法");
        }

        if (!AuditStatusValidator
                .setOriginal(this.getAuditStatus())
                .checkValidate(newAuditStatus.getNum())) {
            throw new IllegalArgumentException("规则《" + this.getStaticRuleName() + "》审核状态错误");
        }
    }
}
