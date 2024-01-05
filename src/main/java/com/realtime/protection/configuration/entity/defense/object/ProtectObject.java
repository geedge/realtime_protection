package com.realtime.protection.configuration.entity.defense.object;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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
    @Pattern(regexp = "^(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})$", message = "Invalid IPv4 Address")
    private String protectObjectIPAddress;

    @JsonProperty("proobj_port")
    @NotNull(message = "proobj_port should not be empty.")
    @Max(value = 65535, message = "port should not be more than 65535")
    @Min(value = 1, message = "port should not be less than 1")
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
