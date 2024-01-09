package com.realtime.protection.configuration.entity.task;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Command {
    private Integer id;

    private Integer type;
    private String sourceIP;
    private String sourcePort;
    private String destinationIP;
    private String destinationPort;
    private Integer protocol;

    private String maskSourceIP;
    private String maskSourcePort;
    private String maskDestinationIP;
    private String maskDestinationPort;
    private Integer direction;

    private LocalDateTime datetime;
}
