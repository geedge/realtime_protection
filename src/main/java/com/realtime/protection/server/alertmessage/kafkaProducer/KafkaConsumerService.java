package com.realtime.protection.server.alertmessage.kafkaProducer;

import com.realtime.protection.configuration.entity.alert.AlertMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class KafkaConsumerService {
    @KafkaListener(topics = "${spring.kafka.consumer.topic-name}")
    public void consume(AlertMessage alerm, Acknowledgment ack) {
        try {
            log.info("消费者监听到数据：{}", alerm);
            // 手动提交
            ack.acknowledge();
        } catch (Exception e) {
            throw new RuntimeException("消费失败，数据： " + alerm, e);
        }
    }


}
