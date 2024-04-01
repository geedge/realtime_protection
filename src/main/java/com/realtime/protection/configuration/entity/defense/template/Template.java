package com.realtime.protection.configuration.entity.defense.template;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    @NotNull(message = "source_system字段不能为空。")
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
}
