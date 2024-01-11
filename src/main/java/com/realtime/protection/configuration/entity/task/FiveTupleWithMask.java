package com.realtime.protection.configuration.entity.task;

import lombok.Data;

@Data
public class FiveTupleWithMask {
    private Integer addrType;
    private String sourceIP;
    private String sourcePort;
    private String destinationIP;
    private String destinationPort;
    private String protocol;
    private Integer protocolNum;

    private String maskSourceIP;
    private String maskSourcePort;
    private String maskDestinationIP;
    private String maskDestinationPort;
}
