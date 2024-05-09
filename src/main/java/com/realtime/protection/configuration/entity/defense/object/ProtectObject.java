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

    @JsonProperty("proobj_display_id")
    @ExcelProperty("ID")
    @Schema(description = "防护对象显示ID", accessMode = Schema.AccessMode.READ_ONLY)
    private String protectObjectDisplayId;

    @JsonProperty("proobj_name")
    @NotNull(message = "proobj_name字段不能为空。")
    @ExcelProperty("名称")
    @Schema(description = "防护对象名称", example = "静态对象测试")
    private String protectObjectName;

    @JsonProperty("proobj_system_name")
    @ExcelProperty("操作系统名称")
    @Schema(description = "防护对象操作系统名称", example = "xxx操作系统")
    private String protectObjectSystemName;

    @JsonProperty("proobj_ip_address")
    @Pattern(regexp = "^(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})$", message = "无效IPv4地址")
    @ExcelProperty("IP地址")
    @Schema(description = "防护对象IPv4地址", example = "192.168.0.1")
    private String protectObjectIPAddress;

    @JsonProperty("proobj_port")
    @NotNull(message = "proobj_port字段不能为空。")
    @Max(value = 65535, message = "端口号不能大于65535。")
    @Min(value = 1, message = "端口号不能小于1。")
    @ExcelProperty("端口")
    @Schema(description = "防护对象端口", maximum = "65535", minimum = "1", example = "8080")
    private Integer protectObjectPort;

    @JsonProperty("proobj_url")
    @NotNull(message = "proobj_url字段不能为空。")
    @ExcelProperty("URL")
    @Schema(description = "防护对象URL", example = "alice.bob.com")
    private String protectObjectURL;

    @JsonProperty("proobj_protocol")
    @NotNull(message = "proobj_protocol字段不能为空")
    @ExcelProperty("协议")
    @Schema(description = "防护对象网络协议（目前仅可以填写TCP或UDP）", example = "TCP")
    private String protectObjectProtocol;

    @JsonProperty("proobj_audit_status")
    @ExcelIgnore
    @Schema(description = "防护对象审核状态（0为未审核，1为已退回，2为审核通过）", accessMode = Schema.AccessMode.READ_ONLY)
    private Integer protectObjectAuditStatus;

    @JsonProperty("proobj_create_username")
    @ExcelIgnore
    @Schema(description = "防护对象创建人", example = "xxx")
    private String protectObjectCreateUsername;

    @JsonProperty("proobj_create_depart")
    @ExcelIgnore
    @Schema(description = "防护对象创建人处室", example = "xxx")
    private String protectObjectCreateDepart;

    @JsonProperty("proobj_create_userid")
    @ExcelIgnore
    @Schema(description = "防护对象创建人ID", example = "0")
    private Integer protectObjectCreateUserId;
}
