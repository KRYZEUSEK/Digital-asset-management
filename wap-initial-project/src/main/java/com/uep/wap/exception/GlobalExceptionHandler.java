package com.uep.wap.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException exception) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("error", "BAD_REQUEST");
        body.put("message", exception.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }
}
