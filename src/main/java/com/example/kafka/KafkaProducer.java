package com.example.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.kafka.support.converter.MessagingMessageConverter;
import org.springframework.kafka.support.converter.RecordMessageConverter;
import org.springframework.messaging.Message;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
public class KafkaProducer {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaProducer.class);

    @Autowired
    private KafkaTemplate<Integer, String> kafkaTemplate;

    private RecordMessageConverter messageConverter = new MessagingMessageConverter();

    public void sendMessage(String topic, final String message) {
        // the KafkaTemplate provides asynchronous send methods returning a
        // Future
        ListenableFuture<SendResult<Integer, String>> future = kafkaTemplate.send(topic, message);

        // you can register a callback with the listener to receive the result
        // of the send asynchronously
        future.addCallback(
                new ListenableFutureCallback<SendResult<Integer, String>>() {

                    @Override
                    public void onSuccess(
                            SendResult<Integer, String> result) {
                        LOGGER.info("sent message='{}' with offset={}", message, result.getRecordMetadata().offset());
                    }

                    @Override
                    public void onFailure(Throwable ex) {
                        LOGGER.error("unable to send message='{}'", message, ex);
                    }
                });

        // alternatively, to block the sending thread, to await the result,
        // invoke the future’s get() method
    }

    public void sendMessage(final Message<?> message) {
        ListenableFuture<SendResult<Integer, String>> future = kafkaTemplate.send(message);
        future.addCallback(
                new ListenableFutureCallback<SendResult<Integer, String>>() {
                    @Override
                    public void onSuccess(
                            SendResult<Integer, String> result) {
                        LOGGER.info("sent message='{}' with offset={}", message.getPayload().toString(), result.getRecordMetadata().offset());
                    }

                    @Override
                    public void onFailure(Throwable ex) {
                        LOGGER.error("unable to send message='{}'", message, ex);
                    }
                });

    }


}