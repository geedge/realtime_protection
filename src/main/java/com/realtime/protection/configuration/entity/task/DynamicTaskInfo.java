package com.realtime.protection.configuration.entity.task;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class DynamicTaskInfo {

    @Data
    private static class SimpleProtectObject {

        @JsonProperty("ip")
        private String IP;

        @JsonProperty("port")
        private Integer port;

        @JsonProperty("url")
        private String URL;

        @JsonProperty("protocol")
        private String protocol;
    }

    // 从任务中获取
    @JsonProperty("task_id")
    private Long taskId;

    @JsonProperty("start_time")
    private LocalDateTime startTime;

    @JsonProperty("end_time")
    private LocalDateTime endTime;

    // 从规则中获取
    @JsonProperty("rule_id")
    private Integer ruleId;

    @JsonProperty("source_system")
    private String sourceSystem;

    @JsonProperty("event_type")
    private String eventType;

    @JsonProperty("log_rule_id")
    private Long logRuleId;

    // 从防护对象列表中获取
    @JsonProperty("protect_objects")
    private List<SimpleProtectObject> protectObjects;
}
