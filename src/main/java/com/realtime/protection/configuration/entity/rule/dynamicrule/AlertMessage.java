package com.realtime.protection.configuration.entity.rule.dynamicrule;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.realtime.protection.configuration.entity.task.FiveTupleWithMask;
import lombok.Data;

@Data
public class AlertMessage {

    @JsonProperty("task_id")
    private Long taskId;
    @JsonProperty("five_tuple_with_mask")
    private FiveTupleWithMask fiveTupleWithMask;

}
