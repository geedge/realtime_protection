package com.realtime.protection.configuration.kafka;


import com.realtime.protection.configuration.entity.alert.AlertMessage;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

//@Configuration
//@EnableKafka
public class KafkaConsumerConfig {
//    @Value("${spring.kafka.bootstrap-servers}")
//    private String bootstrapServers;
//
//    @Value("${spring.kafka.consumer.group-id}")
//    private String groupId;
//
//    @Autowired
//    private KafkaProperties kafkaProperties;
//
//    @Bean
//    public Map<String, Object> consumerConfigs() {
//        Map<String, Object> props = new HashMap<>();
//        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
//        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
//        return props;
//    }
//
//    @Bean
//    public ConsumerFactory<String, AlertMessage> consumerFactory() {
//        return new DefaultKafkaConsumerFactory<>(consumerConfigs(), new StringDeserializer(),
//                new JsonDeserializer<>(AlertMessage.class));
//    }
//
//    @Bean
//    public ConcurrentKafkaListenerContainerFactory<String, AlertMessage> kafkaListenerContainerFactory() {
//        ConcurrentKafkaListenerContainerFactory<String, AlertMessage> factory = new ConcurrentKafkaListenerContainerFactory<>();
//        factory.setConsumerFactory(consumerFactory());
//        return factory;
//    }
}

