package com.realtime.protection.configuration.entity.defense.object;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class ProtectObject {
    @JsonProperty("proobj_id")
    @ExcelIgnore
    @Schema(description = "防护对象ID", accessMode = Schema.AccessMode.READ_ONLY)
    private Integer protectObjectId;

    @JsonProperty("proobj_name")
    @NotNull(message = "proobj_name should not be empty.")
    @ExcelProperty("名称")
    @Schema(description = "防护对象名称", example = "静态对象测试")
    private String protectObjectName;

    @JsonProperty("proobj_system_name")
    @ExcelProperty("操作系统名称")
    @Schema(description = "防护对象操作系统名称", example = "xxx操作系统")
    private String protectObjectSystemName;

    @JsonProperty("proobj_ip_address")
    @Pattern(regexp = "^(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})$", message = "Invalid IPv4 Address")
    @ExcelProperty("IP地址")
    @Schema(description = "防护对象IPv4地址", example = "192.168.0.1")
    private String protectObjectIPAddress;

    @JsonProperty("proobj_port")
    @NotNull(message = "proobj_port should not be empty.")
    @Max(value = 65535, message = "port should not be more than 65535")
    @Min(value = 1, message = "port should not be less than 1")
    @ExcelProperty("端口")
    @Schema(description = "防护对象端口", maximum = "65535", minimum = "1", example = "8080")
    private Integer protectObjectPort;

    @JsonProperty("proobj_url")
    @NotNull(message = "proobj_url should not be empty.")
    @ExcelProperty("URL")
    @Schema(description = "防护对象URL", example = "alice.bob.com")
    private String protectObjectURL;

    @JsonProperty("proobj_protocol")
    @NotNull(message = "proobj_protocol should not be empty.")
    @ExcelProperty("协议")
    @Schema(description = "防护对象网络协议（目前仅可以填写TCP或UDP）", example = "TCP")
    private String protectObjectProtocol;

    @JsonProperty("proobj_audit_status")
    @ExcelIgnore
    @Schema(description = "防护对象审核状态（0为未审核，1为已退回，2为审核通过）", example = "2")
    private Integer protectObjectAuditStatus;

    @JsonProperty("proobj_create_username")
    @ExcelIgnore
    @Schema(description = "防护对象创建人", example = "xxx", accessMode = Schema.AccessMode.READ_ONLY)
    private String protectObjectCreateUsername;

    @JsonProperty("proobj_create_depart")
    @ExcelIgnore
    @Schema(description = "防护对象创建人处室", example = "xxx", accessMode = Schema.AccessMode.READ_ONLY)
    private String protectObjectCreateDepart;

    @JsonProperty("proobj_create_userid")
    @ExcelIgnore
    @Schema(description = "防护对象创建人ID", example = "0", accessMode = Schema.AccessMode.READ_ONLY)
    private Integer protectObjectCreateUserId;
}
