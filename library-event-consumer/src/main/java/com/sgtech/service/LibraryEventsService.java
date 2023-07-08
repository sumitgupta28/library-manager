package com.sgtech.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sgtech.entity.LibraryEvent;
import com.sgtech.jpa.LibraryEventsRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.dao.RecoverableDataAccessException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor
public class LibraryEventsService {

    private ObjectMapper objectMapper;

    private LibraryEventsRepository libraryEventsRepository;

    public void processLibraryEvent(ConsumerRecord<Integer, String> consumerRecord) throws JsonProcessingException {

        try {
            log.info("processLibraryEvent at consumerRecord.value : {} ", consumerRecord.value());
            LibraryEvent  libraryEvent = objectMapper.readValue(consumerRecord.value(), LibraryEvent.class);
            log.info("libraryEvent : {} ", libraryEvent);
            if(libraryEvent.getLibraryEventId()!=null && ( libraryEvent.getLibraryEventId()==999 )){
                throw new RecoverableDataAccessException("Temporary Network Issue");
            }
            switch (libraryEvent.getLibraryEventType()) {
                case NEW:
                    save(libraryEvent);
                    break;
                case UPDATE:
                    //validate the libraryevent
                    validate(libraryEvent);
                    save(libraryEvent);
                    break;
                default:
                    log.info("Invalid Library Event Type");
            }

        } catch (JsonProcessingException e) {
            log.info("processLibraryEvent at JsonProcessingException  {} ", e);
            throw e;
        }


    }

    private void validate(LibraryEvent libraryEvent) {
        if (libraryEvent.getLibraryEventId() == null) {
            throw new IllegalArgumentException("Library Event Id is missing");
        }

        Optional<LibraryEvent> libraryEventOptional = libraryEventsRepository.findById(libraryEvent.getLibraryEventId());
        if (!libraryEventOptional.isPresent()) {
            throw new IllegalArgumentException("Not a valid library Event");
        }
        log.info("Validation is successful for the library Event : {} ", libraryEventOptional.get());
    }

    private void save(LibraryEvent libraryEvent) {
        libraryEvent.getBook().setLibraryEvent(libraryEvent);
        libraryEventsRepository.save(libraryEvent);
        log.info("Successfully Persisted the library Event {} ", libraryEvent);
    }


}
