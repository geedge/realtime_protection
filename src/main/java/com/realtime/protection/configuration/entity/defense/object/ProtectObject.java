package com.realtime.protection.configuration.entity.defense.object;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class ProtectObject {
    @JsonProperty("proobj_id")
    @ExcelIgnore
    private Integer protectObjectId;

    @JsonProperty("proobj_name")
    @NotNull(message = "proobj_name should not be empty.")
    @ExcelProperty("名称")
    private String protectObjectName;

    @JsonProperty("proobj_system_name")
    @ExcelProperty("操作系统名称")
    private String protectObjectSystemName;

    @JsonProperty("proobj_ip_address")
    @Pattern(regexp = "^(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})$", message = "Invalid IPv4 Address")
    @ExcelProperty("IP地址")
    private String protectObjectIPAddress;

    @JsonProperty("proobj_port")
    @NotNull(message = "proobj_port should not be empty.")
    @Max(value = 65535, message = "port should not be more than 65535")
    @Min(value = 1, message = "port should not be less than 1")
    @ExcelProperty("端口")
    private Integer protectObjectPort;

    @JsonProperty("proobj_url")
    @NotNull(message = "proobj_url should not be empty.")
    @ExcelProperty("URL")
    private String protectObjectURL;

    @JsonProperty("proobj_protocol")
    @NotNull(message = "proobj_protocol should not be empty.")
    @ExcelProperty("协议")
    private String protectObjectProtocol;

    @JsonProperty("proobj_audit_status")
    @ExcelIgnore
    private Integer protectObjectAuditStatus;

    @JsonProperty("proobj_create_username")
    @ExcelIgnore
    private String protectObjectCreateUsername;

    @JsonProperty("proobj_create_depart")
    @ExcelIgnore
    private String protectObjectCreateDepart;

    @JsonProperty("proobj_create_userid")
    @ExcelIgnore
    private Integer protectObjectCreateUserId;
}
