package com.realtime.protection.configuration.entity.whitelist;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class WhiteListObject {
    @JsonProperty("whiteobj_id")
    @ExcelIgnore
    @Schema(description = "白名单对象ID", accessMode = Schema.AccessMode.READ_ONLY)
    private int whiteListId;

    @NotNull
    @JsonProperty("whiteobj_name")
    @ExcelProperty("名称")
    @Schema(description = "白名单对象名称", example = "白名单测试", requiredMode = Schema.RequiredMode.REQUIRED)
    private String whiteListName;

    @NotNull
    @JsonProperty("whiteobj_system_name")
    @ExcelProperty("系统名称")
    @Schema(description = "白名单对象系统名称", example = "xxx系统", requiredMode = Schema.RequiredMode.REQUIRED)
    private String whiteListSystemName;

    @NotNull
    @JsonProperty("whiteobj_ip_address")
    @Pattern(regexp = "^(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})$", message = "错误 IPv4 地址")
    @ExcelProperty("IP地址")
    @Schema(description = "白名单对象IPv4地址", example = "1.1.1.1", requiredMode = Schema.RequiredMode.REQUIRED)
    private String whiteListIP;

    @NotNull
    @JsonProperty("whiteobj_port")
    @Max(value = 65535, message = "端口不应大于 65535")
    @Min(value = 1, message = "端口不应小于 1")
    @ExcelProperty("端口")
    @Schema(description = "白名单对象端口", maximum = "65535", minimum = "1", example = "8080", requiredMode = Schema.RequiredMode.REQUIRED)
    private int whiteListPort;

    @NotNull
    @JsonProperty("whiteobj_url")
    @ExcelProperty("URL")
    @Schema(description = "白名单对象URL", example = "alice.bob.com", requiredMode = Schema.RequiredMode.REQUIRED)
    private String whiteListUrl;

    @NotNull
    @JsonProperty("whiteobj_protocol")
    @ExcelProperty("协议")
    @Schema(description = "白名单对象网络协议", example = "TCP", requiredMode = Schema.RequiredMode.REQUIRED)
    private String whiteListProtocol;

    @JsonProperty("audit_status")
    @ExcelIgnore
    @Schema(description = "白名单对象审核状态（0为未审核，1为已退回，2为审核通过）", example = "2")
    private Integer whiteListAuditStatus;

    @JsonProperty("white_list_display_id")
    @ExcelIgnore
    @Schema(description = "白名单展示id",  example = "BMD-20200101-123456", accessMode = Schema.AccessMode.READ_ONLY)
    private String whiteListDisplayId;

    @JsonProperty("white_list_create_user_name")
    @ExcelIgnore
    @Schema(description = "白名单对象创建人名称")
    private String createUserName;

    @JsonProperty("white_list_create_user_id")
    @Schema(description = "白名单对象创建人id")
    @ExcelIgnore
    private String createUserId;

    @JsonProperty("white_list_create_user_department")
    @Schema(description = "白名单对象创建人部门")
    @ExcelIgnore
    private String createUserDepartment;

    @JsonProperty("audit_user_name")
    @Schema(description = "审核用户名称", example = "user11")
    @ExcelIgnore
    private String auditUserName;
    @JsonProperty("audit_user_id")
    @Schema(description = "审核用户id", example = "11111")
    @ExcelIgnore
    private String auditUserId;
    @JsonProperty("audit_user_depart")
    @Schema(description = "审核用户部门", example = "部门1")
    @ExcelIgnore
    private String auditUserDepart;

    @JsonProperty("create_time")
    @Schema(description = "创建时间", example = "")
    @ExcelIgnore
    private LocalDateTime createTime;
    @JsonProperty("modify_time")
    @Schema(description = "修改时间", example = "")
    @ExcelIgnore
    private LocalDateTime modifyTime;

    @JsonProperty("effecive_time")
    @ExcelIgnore
    @Schema(description = "日志表中有效开始时间", example = "", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime effeciveTime;

    @JsonProperty("expire_time")
    @ExcelIgnore
    @Schema(description = "日志表中到期时间", example = "", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime expireTime;
}
