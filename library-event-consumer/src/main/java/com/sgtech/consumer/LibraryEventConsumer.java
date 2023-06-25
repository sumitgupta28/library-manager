package com.sgtech.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sgtech.service.LibraryEventsService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class LibraryEventConsumer {


    @Autowired
    private LibraryEventsService libraryEventsService;

    @KafkaListener(topics = {"${spring.kafka.topic}"})
    public void onMessage(ConsumerRecord<Integer,String> consumerRecord) throws JsonProcessingException {
        log.info("Consumer Record - {} ", consumerRecord);
        libraryEventsService.processLibraryEvent(consumerRecord);

    }
}
