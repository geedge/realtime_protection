package com.realtime.protection.configuration.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaTopicConfig {
    @Bean
    public NewTopic batchTopic() {
        return new NewTopic("topic-alert", 4, (short) 1);
    }
}
