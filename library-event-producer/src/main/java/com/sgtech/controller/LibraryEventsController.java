package com.sgtech.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sgtech.domain.LibraryEvent;
import com.sgtech.domain.LibraryEventType;
import com.sgtech.producer.LibraryEventProducer;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

@RestController
@Slf4j
public class LibraryEventsController {

    @Autowired
    private LibraryEventProducer libraryEventProducer;

    @PostMapping({"/v1/libraryevent","/v1/libraryevent-async"})
    public ResponseEntity<LibraryEvent> postLibraryEventAsync(@RequestBody LibraryEvent libraryEvent) throws JsonProcessingException, ExecutionException, InterruptedException, TimeoutException {
        libraryEvent.setLibraryEventType(LibraryEventType.NEW);
        log.info(" Before Send.....");
        libraryEventProducer.sendLibraryEventAsync(libraryEvent);
        log.info(" After Send.....");
        return ResponseEntity.status(HttpStatus.CREATED).body(libraryEvent);
    }


    @PostMapping("/v1/libraryevent-async-pr")
    public ResponseEntity<LibraryEvent> postLibraryEventAsyncWithPR(@RequestBody LibraryEvent libraryEvent) throws JsonProcessingException, ExecutionException, InterruptedException, TimeoutException {
        libraryEvent.setLibraryEventType(LibraryEventType.NEW);
        log.info(" Before Send.....");
        libraryEventProducer.sendLibraryEventWithProducerRecord(libraryEvent);
        log.info(" After Send.....");
        return ResponseEntity.status(HttpStatus.CREATED).body(libraryEvent);
    }


    @PostMapping("/v1/libraryevent-sync")
    public ResponseEntity<LibraryEvent> postLibraryEventSynchronous(@RequestBody LibraryEvent libraryEvent) throws JsonProcessingException, ExecutionException, InterruptedException, TimeoutException {
        libraryEvent.setLibraryEventType(LibraryEventType.NEW);
        log.info(" Before Send.....");
        libraryEventProducer.sendLibraryEventSynchronous(libraryEvent);
        log.info(" After Send.....");
        return ResponseEntity.status(HttpStatus.CREATED).body(libraryEvent);
    }

    //PUT
    @PutMapping("/v1/libraryevent")
    public ResponseEntity<?> putLibraryEvent(@RequestBody @Valid LibraryEvent libraryEvent) throws JsonProcessingException, ExecutionException, InterruptedException {

        log.info("LibraryEvent : {} ", libraryEvent);
        ResponseEntity<String> BAD_REQUEST = validateLibraryEvent(libraryEvent);
        if (BAD_REQUEST != null) return BAD_REQUEST;
        log.info(" Before Send.....");
        libraryEvent.setLibraryEventType(LibraryEventType.UPDATE);
        libraryEventProducer.sendLibraryEventAsync(libraryEvent);
        log.info(" After Send.....");
        return ResponseEntity.status(HttpStatus.OK).body(libraryEvent);
    }

    private ResponseEntity<String> validateLibraryEvent(LibraryEvent libraryEvent) {
        if (libraryEvent.getLibraryEventId() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("LibraryEventId can not be null");
        }
        if (null!= libraryEvent.getLibraryEventType() && !libraryEvent.getLibraryEventType().equals(LibraryEventType.NEW)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("LibraryEventType not supported");
        }
        return null;
    }

}
