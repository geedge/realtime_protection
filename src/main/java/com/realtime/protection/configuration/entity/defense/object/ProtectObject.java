package com.realtime.protection.configuration.entity.defense.object;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProtectObject {
    @JsonProperty("proobj_id")
    private Integer protectObjectId;

    @JsonProperty("proobj_name")
    @NotNull(message = "proobj_name should not be empty.")
    private String protectObjectName;

    @JsonProperty("proobj_system_name")
    private String protectObjectSystemName;

    @JsonProperty("proobj_ip_address")
    private String protectObjectIPAddress;

    @JsonProperty("proobj_port")
    @NotNull(message = "proobj_port should not be empty.")
    private Integer protectObjectPort;

    @JsonProperty("proobj_url")
    @NotNull(message = "proobj_url should not be empty.")
    private String protectObjectURL;

    @JsonProperty("proobj_protocol")
    @NotNull(message = "proobj_protocol should not be empty.")
    private String protectObjectProtocol;

    @JsonProperty("proobj_audit_status")
    private Integer protectObjectAuditStatus;

    @JsonProperty("proobj_create_username")
    private String protectObjectCreateUsername;

    @JsonProperty("proobj_create_depart")
    private String protectObjectCreateDepart;

    @JsonProperty("proobj_create_userid")
    private Integer protectObjectCreateUserId;
}
