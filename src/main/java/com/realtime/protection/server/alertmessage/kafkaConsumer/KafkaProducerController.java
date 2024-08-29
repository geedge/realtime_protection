package com.realtime.protection.server.alertmessage.kafkaConsumer;

import com.realtime.protection.configuration.entity.alert.AlertMessage;
import com.realtime.protection.configuration.entity.task.FiveTupleWithMask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@Slf4j
@RestController
public class KafkaProducerController {
    KafkaTemplate<String, AlertMessage> kafkaTemplate;
    public KafkaProducerController(KafkaTemplate<String, AlertMessage> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @GetMapping("/kafkasend/{taskId}/{dynamicRuleId}")
    public void sendMessage(@PathVariable Integer dynamicRuleId, @PathVariable Long taskId ) {
        AlertMessage alert = new AlertMessage();

        FiveTupleWithMask fiveTupleWithMask = new FiveTupleWithMask();
        fiveTupleWithMask.setSourceIP("1.1.1.1");
        fiveTupleWithMask.setSourcePort("80");
        fiveTupleWithMask.setDestinationIP("2.2.2.2");
        fiveTupleWithMask.setDestinationPort("80");
        fiveTupleWithMask.setProtocol("TCP");

        alert.setDynamicRuleId(dynamicRuleId);
        alert.setTaskId(taskId);
        alert.setFiveTupleWithMask(fiveTupleWithMask);
        alert.setContent("testcontent");
        kafkaTemplate.send("topic-alert", alert);

    }


}
