package com.realtime.protection.server.alertmessage.kafkaProducer;

import com.realtime.protection.configuration.entity.alert.AlertMessage;
import com.realtime.protection.server.alertmessage.AlertMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class KafkaConsumerService {
    private final AlertMessageService alertMessageService;
    public KafkaConsumerService(AlertMessageService alertMessageService){
        this.alertMessageService = alertMessageService;
    }

    @KafkaListener(id = "AlertGroup", topics = "topic-alert",
            errorHandler = "myKafkaListenerErrorHandler",
            properties = {"spring.json.value.default.type=com.realtime.protection.configuration.entity.alert.AlertMessage"})
    public void consume(AlertMessage alert, Acknowledgment ack) {
        log.info("消费者监听到数据：{}", alert);

        alertMessageService.processAlertMessage(alert);
        ack.acknowledge();
        // 手动提交

    }


}
