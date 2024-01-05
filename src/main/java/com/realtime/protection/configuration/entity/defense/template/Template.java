package com.realtime.protection.configuration.entity.defense.template;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class Template {
    @JsonProperty("template_id")
    private Integer templateId;

    @JsonProperty("template_name")
    @NotNull(message = "template name should not be empty.")
    private String templateName;

    @JsonProperty("template_elements")
    private List<String> templateElements;

    @JsonProperty("default_op")
    @NotNull(message = "default_op should not be empty.")
    private String defaultOp;

    @JsonProperty("template_running_tasks")
    private Integer templateRunningTasks;

    @JsonProperty("template_used")
    private Integer templateUsedTimes;

    private Boolean hasProtectObjectIP;

    private Boolean hasProtectObjectPort;

    private Boolean hasPeerIP;

    private Boolean hasPeerPort;

    private Boolean hasProtocol;

    private Boolean hasURL;

    private Boolean hasDNS;
}
