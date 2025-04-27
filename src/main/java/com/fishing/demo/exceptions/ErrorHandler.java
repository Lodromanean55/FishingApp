package com.fishing.demo.exceptions;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice  // Această adnotare permite aplicarea handler-ului la nivel global în cadrul aplicației
public class ErrorHandler {


    // Exemplu: Tratăm o excepție custom, dacă ai definit-o
    @ExceptionHandler(UserValidationException.class)
    public ResponseEntity<?> handleResourceNotFoundException(UserValidationException ex, WebRequest request) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
