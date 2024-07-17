package com.realtime.protection.configuration.utils;

import lombok.Data;

import java.io.Serializable;

@Data
public class File implements Serializable {
    private Integer id;
    private String fileName;
    private String filePath;
    private Long fileSize;
    private String fileType;
    private Long staticRuleId;
}
