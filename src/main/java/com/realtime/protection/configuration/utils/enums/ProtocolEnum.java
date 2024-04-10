package com.realtime.protection.configuration.utils.enums;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public enum ProtocolEnum {
    TCP(6, "TCP"),
    UDP(17, "UDP");

    private final Integer number;
    private final String protocol;

    private static final Map<Integer, ProtocolEnum> protocolNumMap = new HashMap<>();
    private static final Map<String, ProtocolEnum> protocolMap = new HashMap<>();

    static {
        for (ProtocolEnum protocol : ProtocolEnum.values()) {
            protocolNumMap.put(protocol.getNumber(), protocol);
            protocolMap.put(protocol.getProtocol(), protocol);
        }
    }

    ProtocolEnum(int protocolNumber, String protocol) {
        this.number = protocolNumber;
        this.protocol = protocol;
    }

    public static ProtocolEnum getProtocolEnumByNumber(Integer protocolNum) {
        return protocolNumMap.get(protocolNum);
    }
    public static ProtocolEnum getProtocolEnumByProtocol(String protocol) { return protocolMap.get(protocol); }
}
