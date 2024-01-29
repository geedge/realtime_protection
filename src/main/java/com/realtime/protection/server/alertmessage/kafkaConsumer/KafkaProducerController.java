package com.realtime.protection.server.alertmessage.kafkaConsumer;

import com.realtime.protection.configuration.entity.alert.AlertMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class KafkaProducerController {
    KafkaTemplate<String, AlertMessage> kafkaTemplate;
    public KafkaProducerController(KafkaTemplate<String, AlertMessage> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @PostMapping("/kafkasend")
    public void sendMessage(@RequestBody AlertMessage alerm) {
        kafkaTemplate.send("topic-test", alerm);

    }


}
