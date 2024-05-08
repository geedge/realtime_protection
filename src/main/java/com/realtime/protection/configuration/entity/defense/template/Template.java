package com.realtime.protection.configuration.entity.defense.template;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.realtime.protection.configuration.utils.NonEmptyFieldFetcher;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class Template {
    @JsonProperty("template_id")
    @Schema(description = "防御策略模板ID", example = "2", accessMode = Schema.AccessMode.READ_ONLY)
    private Integer templateId;

    @JsonProperty("template_name")
    @NotNull(message = "template_name字段不能为空。")
    @Schema(description = "防御策略模板名称(事件类型)", example = "APT攻击事件")
    private String templateName;

    @JsonProperty("source_system")
    @NotNull(message = "source_system字段不能为空。")
    @Schema(description = "防御策略模板数据来源系统", example = "BW系统")
    private String sourceSystem;

    @JsonProperty("description")
    @Schema(description = "对策略模板的文字描述。方便用户使用", example = "zd防护对象的全流量")
    private String description;

    @JsonProperty("protect_level_low")
    @NotNull(message = "protect_level_low字段不能为空。")
    @Schema(description = "防御策略模板日常态字段提取选项")
    private ProtectLevel protectLevelLow;

    @JsonProperty("protect_level_medium")
    @NotNull(message = "protect_level_medium字段不能为空。")
    @Schema(description = "防御策略模板应急态字段提取选项")
    private ProtectLevel protectLevelMedium;

    @JsonProperty("protect_level_high")
    @NotNull(message = "protect_level_high字段不能为空。")
    @Schema(description = "防御策略模板紧急态字段提取选项")
    private ProtectLevel protectLevelHigh;

    @JsonProperty("has_protect_level_low")
    @Schema(description = "日常态字段是否不空", accessMode = Schema.AccessMode.READ_ONLY)
    private Boolean hasProtectLevelLow;

    @JsonProperty("has_protect_level_medium")
    @Schema(description = "应急态字段是否不空", accessMode = Schema.AccessMode.READ_ONLY)
    private Boolean hasProtectLevelMedium;

    @JsonProperty("has_protect_level_high")
    @Schema(description = "紧急态字段是否不空", accessMode = Schema.AccessMode.READ_ONLY)
    private Boolean hasProtectLevelHigh;

    @JsonProperty("template_used_times")
    @Schema(description = "防御策略模板使用次数", example = "20", accessMode = Schema.AccessMode.READ_ONLY)
    private Integer usedTimes;

    @JsonProperty("running_tasks")
    @Schema(description = "防御策略模板已运行的任务数量", example = "30", accessMode = Schema.AccessMode.READ_ONLY)
    private Integer runningTasks;

    @JsonProperty("create_user_id")
    @Schema(description = "防御策略模板创建人ID", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Integer createUserId;

    @JsonProperty("create_user_name")
    @Schema(description = "防御策略模板创建人名称", example = "xxx", accessMode = Schema.AccessMode.READ_ONLY)
    private String createUsername;

    @JsonProperty("create_user_depart")
    @Schema(description = "防御策略模板创建人处室", example = "xxx", accessMode = Schema.AccessMode.READ_ONLY)
    private String createDepart;

    @JsonProperty("audit_status")
    @Schema(description = "防御策略模板审核状态（0为未审核，1为已退回，2为审核通过）", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private String auditStatus;

    @JsonProperty("template_display_id")
    @Schema(description = "策略模板展示id", accessMode = Schema.AccessMode.READ_ONLY)
    private String templateDisplayId;


    /**
     * 设置是否含有日常/应急/紧急防护等级态字段的字段
     */
    public void setHasProtectLevel() throws IllegalAccessException {
        this.hasProtectLevelHigh = hasProtectLevelFields(this.protectLevelHigh);
        this.hasProtectLevelMedium = hasProtectLevelFields(this.protectLevelMedium);
        this.hasProtectLevelLow = hasProtectLevelFields(this.protectLevelLow);
    }

    /**
     *仅保留是否含有日常/应急/紧急防护等级态字段以及策略模板名称和ID
     */
    public void shortenTemplate() {
        this.sourceSystem = null;
        this.description = null;
        this.protectLevelLow = null;
        this.protectLevelMedium = null;
        this.protectLevelHigh = null;
        this.usedTimes = null;
        this.runningTasks = null;
        this.createUserId = null;
        this.createUsername = null;
        this.createDepart = null;
    }

    private Boolean hasProtectLevelFields(ProtectLevel protectLevel) throws IllegalAccessException {
        return NonEmptyFieldFetcher
                .getNonEmptyFields(protectLevel) // 获取所有非空字段
                .stream() // 流式处理
                .filter(field -> field.getType().getName().contains("Boolean")) // 获取所有类型为Boolean的字段
                .anyMatch(field -> {
            try {
                return (Boolean) field.get(protectLevel); // 返回值为true的字段
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
