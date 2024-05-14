package com.realtime.protection.configuration.entity.defense.template;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class ProtectLevel {
    @Schema(description = "防护等级ID", accessMode = Schema.AccessMode.READ_ONLY)
    private Integer protectLevelId;

    @Schema(description = "该防护等级是否需要提取防护对象IP地址字段")
    private Boolean hasProtectObjectIP = false;

    @Schema(description = "该防护等级是否需要提取防护对象端口字段")
    private Boolean hasProtectObjectPort = false;

    @Schema(description = "该防护等级是否需要提取对端IP地址字段")
    private Boolean hasPeerIP = false;

    @Schema(description = "该防护等级是否需要提取对端端口字段")
    private Boolean hasPeerPort = false;

    @Schema(description = "该防护等级是否需要提取网络协议字段")
    private Boolean hasProtocol = false;

    @Schema(description = "该防护等级是否需要提取URL字段")
    private Boolean hasURL = false;

    @Schema(description = "该防护等级是否需要提取DNS")
    private Boolean hasDNS = false;

    @Schema(description = "该防护等级是处置防护对象的全流量or单向流量")
    private Boolean isFullFlow = false;

    @Schema(description = "该防护等级防护对象ip地址是否作为源ip地址")
    private Boolean isProtectObjectIPSrc = false;
}
