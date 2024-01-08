package com.realtime.protection.configuration.entity.defense.template;

import lombok.Data;

@Data
public class ProtectLevel {
    private Integer protectLevelId;

    private Boolean hasProtectObjectIP = false;

    private Boolean hasProtectObjectPort = false;

    private Boolean hasPeerIP = false;

    private Boolean hasPeerPort = false;

    private Boolean hasProtocol = false;

    private Boolean hasURL = false;

    private Boolean hasDNS = false;
}
