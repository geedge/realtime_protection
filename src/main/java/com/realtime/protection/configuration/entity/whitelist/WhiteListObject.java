package com.realtime.protection.configuration.entity.whitelist;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class WhiteListObject {
    @JsonProperty("whiteobj_id")
    private int whiteListId;

    @NotNull
    @JsonProperty("whiteobj_name")
    private String whiteListName;

    @JsonProperty("whiteobj_system_name")
    private String whiteListSystemName;

    @JsonProperty("whiteobj_ip_address")
    private String whiteListIP;

    @JsonProperty("whiteobj_port")
    private int whiteListPort;

    @JsonProperty("whiteobj_url")
    private String whiteListUrl;

    @JsonProperty("whiteobj_protocol")
    private String whiteListProtocol;

    @JsonProperty("audit_status")
    private String whiteListAuditStatus;
}
