package com.realtime.protection.configuration.entity.alert;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.realtime.protection.configuration.entity.task.FiveTupleWithMask;
import com.realtime.protection.configuration.kafka.KafkaMessage;
import lombok.Data;

@Data
public class AlertMessage implements KafkaMessage {

    @JsonProperty("task_id")
    private Long taskId;

    @JsonProperty("dynamic_rule_id")
    private Integer dynamicRuleId;


    @JsonProperty("five_tuple_with_mask")
    private FiveTupleWithMask fiveTupleWithMask;

//    @JsonProperty("is_distribute")
//    private Boolean isDistribute;//待删除

    @JsonProperty("command_uuid")
    private String commandUUID;

    @JsonProperty("create_time")
    private String createTime;
    @JsonProperty("modify_time")
    private String modifyTime;
    @JsonProperty("alert_message_uuid")
    private String alertMessageUUID;

}
