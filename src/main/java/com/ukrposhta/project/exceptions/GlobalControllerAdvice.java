package com.ukrposhta.project.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalControllerAdvice {

    @ExceptionHandler({ManagerNotFoundException.class, ProgrammerNotFoundException.class, ProjectNotFoundException.class})
    public ResponseEntity<Object> handleDataNotFoundException() {

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("message", "Data doesn't match");

        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

}
