package com.realtime.protection.configuration.entity.rule.dynamicrule;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.realtime.protection.configuration.entity.defense.object.ProtectObject;
import com.realtime.protection.configuration.utils.enums.audit.AuditStatusEnum;
import com.realtime.protection.configuration.utils.enums.audit.AuditStatusValidator;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class DynamicRuleObject {

    @JsonProperty("dynamic_rule_id")
    @Schema(description = "动态规则ID", accessMode = Schema.AccessMode.READ_ONLY)
    private Integer dynamicRuleId;

    @JsonProperty("dynamic_rule_display_id")
    @Schema(description = "动态规则显示ID", accessMode = Schema.AccessMode.READ_ONLY)
    private String dynamicRuleDisplayId;

    @NotNull
    @JsonProperty("dynamic_rule_name")
    @Schema(description = "动态规则名称", example = "动态规则测试", requiredMode = Schema.RequiredMode.REQUIRED)
    private String dynamicRuleName;

    @JsonProperty("dynamic_rule_create_time")
    @Schema(hidden = true)
    private LocalDateTime dynamicRuleCreateTime;
    @JsonProperty("dynamic_rule_modify_time")
    @Schema(hidden = true)
    private LocalDateTime dynamicRuleModifyTime;

    @JsonProperty("dynamic_rule_create_username")
    @Schema(description = "动态规则创建用户名称", accessMode = Schema.AccessMode.READ_ONLY)
    private String dynamicRuleCreateUsername;

    @JsonProperty("dynamic_rule_create_depart")
    @Schema(description = "动态规则创建用户所属部门", accessMode = Schema.AccessMode.READ_ONLY)
    private String dynamicRuleCreateDepart;

    //动态规则选择的防护对象ids？
    @NotNull(message = "防护对象不能为NULL")
    @NotEmpty(message = "防护对象不能为空")
    @JsonProperty("protect_object_ids")
    @Schema(description = "请求时发送防护对象ID", accessMode = Schema.AccessMode.WRITE_ONLY, requiredMode = Schema.RequiredMode.REQUIRED)
    private List<Integer> protectObjectIds;
    @JsonProperty("protect_objects")
    @Schema(description = "响应时回复防护对象完整信息", accessMode = Schema.AccessMode.READ_ONLY)
    private List<ProtectObject> protectObjects;


    //还没有建立外键
    @JsonProperty("dynamic_rule_create_user_id")
    @Schema(hidden = true)
    private Integer dynamicRuleCreateUserId;
    @JsonProperty("dynamic_rule_used_task_id")
    @Schema(description = "使用该动态规则的任务ID", accessMode = Schema.AccessMode.READ_ONLY)
    private Long dynamicRuleUsedTaskId;

    //前端不发送事件类型和来源系统了，而是根据事件类型和来源系统确定策略模板id，发过来template_id
    @JsonProperty("dynamic_rule_source_system")
    @Schema(description = "动态规则数据来源系统", example = "bw系统", requiredMode = Schema.RequiredMode.REQUIRED)
    private String dynamicRuleSourceSystem;
    @JsonProperty("dynamic_rule_event_type")
    @Schema(description = "动态规则事件类型", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private String dynamicRuleEventType;
    @NotNull
    @JsonProperty("template_id")
    @Schema(description = "策略模板id", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer templateId;
//    @NotNull
    @JsonProperty("dynamic_rule_protect_level")
    @Max(value = 3)
    @Min(value = 1)
    @Schema(description = "防护等级：1代表日常态、2代表应急态、3代表紧急态", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer dynamicRuleProtectLevel;

    @NotNull
    @JsonProperty("dynamic_rule_priority")
    @Schema(description = "优先级", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer dynamicRulePriority;
    @NotNull
//    @JsonProperty("dynamic_rule_range")
//    @Schema(description = "范围", example = "北京", requiredMode = Schema.RequiredMode.REQUIRED)
//    private String dynamicRuleRange;
//    @NotNull
    @JsonProperty("dynamic_rule_frequency")
    @Schema(description = "频率", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer dynamicRuleFrequency;

    @NotNull
    @JsonProperty("log_rule_id")
    @Schema(description = "筛选条件-日志规则id", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long logRuleId;

    @JsonProperty("dynamic_rule_audit_status")
    @ExcelIgnore
    @Schema(description = "动态规则审核状态（0为未审核，1为已退回，2为审核通过）", example = "2", accessMode = Schema.AccessMode.READ_ONLY)
    private Integer auditStatus;

    @JsonProperty("audit_user_name")
    @Schema(description = "审核用户名称", example = "user11")
    private String auditUserName;
    @JsonProperty("audit_user_id")
    @Schema(description = "审核用户id", example = "11111")
    private String auditUserId;
    @JsonProperty("audit_user_depart")
    @Schema(description = "审核用户部门", example = "部门1")
    private String auditUserDepart;

    public void checkAuditStatusValidate(AuditStatusEnum newAuditStatus) {
        if (!List.of(AuditStatusEnum.AUDITED.getNum(), AuditStatusEnum.USING.getNum()).contains(this.getAuditStatus())) {
            throw new IllegalArgumentException("规则《" + this.getDynamicRuleName() + "》原审批状态非法");
        }

        if (!AuditStatusValidator
                .setOriginal(this.getAuditStatus())
                .checkValidate(newAuditStatus.getNum())) {
            throw new IllegalArgumentException("规则《" + this.getDynamicRuleName() + "》审核状态错误");
        }
    }
}
