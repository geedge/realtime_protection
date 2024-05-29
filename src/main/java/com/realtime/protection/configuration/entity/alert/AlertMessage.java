package com.realtime.protection.configuration.entity.alert;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.realtime.protection.configuration.entity.task.FiveTupleWithMask;
import lombok.Data;


@Data
public class AlertMessage {

    @JsonProperty("task_id")
    private Long taskId;

    @JsonProperty("rule_id")
    private Integer dynamicRuleId;


    @JsonProperty("five_tuple_with_mask")
    private FiveTupleWithMask fiveTupleWithMask;

    @JsonProperty("content")
    private String content;

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
//1代表防护对象命中告警信息里的是目的ip，0代表防护对象命中告警信息里的是源ip
    @JsonProperty("protect_object_is_src_dst")
    private int protectIsSrcOrDst;
}
