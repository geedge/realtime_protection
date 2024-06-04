package com.realtime.protection.configuration.kafka;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.springframework.kafka.listener.KafkaListenerErrorHandler;

import org.springframework.kafka.listener.ListenerExecutionFailedException;
import org.springframework.lang.NonNull;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MyKafkaListenerErrorHandler implements KafkaListenerErrorHandler {

    @Override
    @NonNull
    public Object handleError(@NonNull Message<?> message, @NonNull ListenerExecutionFailedException exception) {
        return new Object();
    }

    @Override
    @NonNull
    public Object handleError(@NonNull Message<?> message, @NonNull ListenerExecutionFailedException exception, Consumer<?, ?> consumer) {

        log.error("消息详情：" + message);
        log.error("异常信息：：" + exception.getCause());
        exception.printStackTrace();
        log.error("消费者详情：：" + consumer.groupMetadata());
        log.error("监听主题：：" + consumer.listTopics());
        return KafkaListenerErrorHandler.super.handleError(message, exception, consumer);
    }


    }
