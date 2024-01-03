package com.realtime.protection.configuration.entity.defense.template;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class Template {
    @JsonProperty("template_id")
    private Integer templateId;

    @JsonProperty("template_name")
    @NotNull(message = "template name should not be empty")
    private String templateName;

    @JsonProperty("template_elements")
    private String[] templateElements;

    @JsonProperty("default_op")
    @NotNull(message = "default_op should not be empty")
    private String defaultOp;

    private boolean hasProtectObjectIP;

    private boolean hasProtectObjectPort;

    private boolean hasPeerIP;

    private boolean hasPeerPort;

    private boolean hasProtocol;

    private boolean hasURL;

    private boolean hasDNS;
}
