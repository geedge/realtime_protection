package com.realtime.protection.configuration.entity.task;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class DynamicTaskInfo {

    @Data
    private static class SimpleProtectObject {
        private String IP;
        private Integer port;
        private String URL;
        private String protocol;
    }

    // 从任务中获取
    private Long taskId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    // 从规则中获取
    private Integer ruleId;
    private String sourceSystem;
    private String eventType;
    private Long logRuleId;

    // 从防护对象列表中获取
    private List<SimpleProtectObject> protectObjects;
}
