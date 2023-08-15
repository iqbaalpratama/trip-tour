package com.iqbaal.triptour.controller;

import com.iqbaal.triptour.dto.response.WebResponse;
import com.iqbaal.triptour.exception.FileTypeNotValidException;
import com.iqbaal.triptour.exception.ResourceNotFoundException;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class ErrorController {

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<WebResponse<String>> constraintViolationException(ConstraintViolationException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(WebResponse.<String>builder().errors(exception.getMessage()).build());
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<WebResponse<String>> apiException(ResponseStatusException exception) {
        return ResponseEntity.status(exception.getStatusCode())
                .body(WebResponse.<String>builder().errors(exception.getReason()).build());
    }

    @ExceptionHandler(FileTypeNotValidException.class)
    public ResponseEntity<WebResponse<String>> fileTypeNotValidException(FileTypeNotValidException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(WebResponse.<String>builder().errors(exception.getMessage()).build());
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<WebResponse<String>> handleMaxSizeException(MaxUploadSizeExceededException exception) {
        return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED)
                .body(WebResponse.<String>builder().errors(exception.getMessage()).build());}

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<WebResponse<String>> handleResourceNotFoundException(ResourceNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(WebResponse.<String>builder().errors(exception.getMessage()).build());}
}