package com.realtime.protection.configuration.entity.task;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.realtime.protection.configuration.utils.enums.ProtocolEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class FiveTupleWithMask {
    @Schema(description = "地址类型(IPv4 or IPv6)", example = "4")
    @JsonProperty("addr_type")
    private Integer addrType = 4;

    @Schema(description = "源IP", example = "192.168.104.14")
    @Pattern(regexp = "^(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})$", message = "源IP：无效IPv4地址")
    @JsonProperty("src_ip")
    private String sourceIP;

    @Schema(description = "源端口", example = "114")
    @Max(value = 65535, message = "源端口不可大于65535")
    @Min(value = 1, message = "源端口不可小于1")
    @JsonProperty("src_port")
    private String sourcePort;

    @Schema(description = "目的IP", example = "102.165.11.39")
    @Pattern(regexp = "^(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})$", message = "目的IP：无效IPv4地址")
    @JsonProperty("dst_ip")
    private String destinationIP;

    @Schema(description = "目的端口", example = "514")
    @Max(value = 65535, message = "目的端口不可大于65535")
    @Min(value = 1, message = "目的端口不可小于1")
    @JsonProperty("dst_port")
    private String destinationPort;

    @Schema(description = "协议名称", example = "TCP", accessMode = Schema.AccessMode.WRITE_ONLY)
    private String protocol;

    @Schema(description = "协议号", example = "6", accessMode = Schema.AccessMode.READ_ONLY)
    @JsonProperty("protocol_num")
    private Integer protocolNum;

    @Schema(description = "源IP掩码", example = "255.255.255.0")
    @Pattern(regexp = "^(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})$", message = "源IP掩码：无效IPv4地址")
    @JsonProperty("mask_src_ip")
    private String maskSourceIP;

    @Schema(description = "源端口掩码", example = "0")
    @Max(value = 65535, message = "源端口掩码不可大于65535")
    @Min(value = 1, message = "源端口掩码不可小于1")
    @JsonProperty("mask_src_port")
    private String maskSourcePort;

    @Schema(description = "目的IP掩码", example = "255.255.0.0")
    @Pattern(regexp = "^(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})$", message = "目的IP掩码：无效IPv4地址")
    @JsonProperty("mask_dst_ip")
    private String maskDestinationIP;

    @Schema(description = "目的端口掩码", example = "0")
    @Max(value = 65535, message = "目的端口掩码不可大于65535")
    @Min(value = 1, message = "目的端口掩码不可小于1")
    @JsonProperty("mask_dst_port")
    private String maskDestinationPort;

    @Schema(description = "协议掩码", example = "0")
    @JsonProperty("mask_protocol")
    private String maskProtocol;

    // 复制构造函数
    public void copyFiveTupleWithMask(FiveTupleWithMask original) {
        this.addrType = original.addrType;
        this.sourceIP = original.sourceIP;
        this.sourcePort = original.sourcePort;
        this.destinationIP = original.destinationIP;
        this.destinationPort = original.destinationPort;
        this.protocol = original.protocol;
        this.protocolNum = original.protocolNum;
        this.maskSourceIP = original.maskSourceIP;
        this.maskSourcePort = original.maskSourcePort;
        this.maskDestinationIP = original.maskDestinationIP;
        this.maskDestinationPort = original.maskDestinationPort;
        this.maskProtocol = original.maskProtocol;
    }

    public void setProtocolNum() {
        ProtocolEnum protocol = ProtocolEnum.getProtocolEnumByProtocol(this.protocol);
        assert protocol != null;
        this.protocolNum = protocol.getNumber();
    }
}
