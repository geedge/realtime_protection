package com.realtime.protection.configuration.entity.defense.template;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class Template {
    @JsonProperty("template_id")
    private Integer templateId;

    @JsonProperty("template_name")
    @NotNull(message = "template name should not be empty.")
    private String templateName;

    @JsonProperty("template_running_tasks")
    private Integer templateRunningTasks;

    @JsonProperty("template_used")
    private Integer templateUsedTimes;

    @JsonProperty("source_system")
    @NotNull(message = "source_system should not be empty. ")
    private String sourceSystem;

    @JsonProperty("protect_level_low")
    @NotNull(message = "protect_level_low should not be empty. ")
    private ProtectLevel protectLevelLow;

    @JsonProperty("protect_level_medium")
    @NotNull(message = "protect_level_medium should not be empty. ")
    private ProtectLevel protectLevelMedium;

    @JsonProperty("protect_level_high")
    @NotNull(message = "protect_level_high should not be empty. ")
    private ProtectLevel protectLevelHigh;

    @JsonProperty("template_used_times")
    private Integer usedTimes;

    @JsonProperty("running_tasks")
    private Integer runningTasks;

    private Integer createUserId;

    private String createUsername;

    private String createDepart;
}
