package com.realtime.protection.configuration.entity.whitelist;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class WhiteListObject {
    @JsonProperty("whiteobj_id")
    @ExcelIgnore
    private int whiteListId;

    @NotNull
    @JsonProperty("whiteobj_name")
    @ExcelProperty("名称")
    private String whiteListName;

    @JsonProperty("whiteobj_system_name")
    @ExcelProperty("系统名称")
    private String whiteListSystemName;

    @JsonProperty("whiteobj_ip_address")
    @Pattern(regexp = "^(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})$", message = "Invalid IPv4 Address")
    @ExcelProperty("IP地址")
    private String whiteListIP;

    @JsonProperty("whiteobj_port")
    @Max(value = 65535, message = "port should not be more than 65535")
    @Min(value = 1, message = "port should not be less than 1")
    @ExcelProperty("端口")
    private int whiteListPort;

    @JsonProperty("whiteobj_url")
    @ExcelProperty("URL")
    private String whiteListUrl;

    @JsonProperty("whiteobj_protocol")
    @ExcelProperty("协议")
    private String whiteListProtocol;

    @JsonProperty("audit_status")
    @ExcelIgnore
    private String whiteListAuditStatus;
}
