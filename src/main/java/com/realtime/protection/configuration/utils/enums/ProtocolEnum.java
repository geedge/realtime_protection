package com.realtime.protection.configuration.utils.enums;

import java.util.HashMap;
import java.util.Map;

public enum ProtocolEnum {
    TCP(6),
    UDP(17);

    private final Integer number;
    private static final Map<Integer, ProtocolEnum> map = new HashMap<>();

    static {
        for (ProtocolEnum protocol : ProtocolEnum.values()) {
            map.put(protocol.getProtocolNumber(), protocol);
        }
    }

    ProtocolEnum(int protocolNumber) {
        this.number = protocolNumber;
    }

    public Integer getProtocolNumber() {
        return this.number;
    }

    public static ProtocolEnum getProtocolEnumByNumber(Integer protocolNum) {
        return map.get(protocolNum);
    }
}
