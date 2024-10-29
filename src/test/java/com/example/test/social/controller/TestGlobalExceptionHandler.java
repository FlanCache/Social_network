package com.example.test.social.controller;

import com.example.social.common.CommonMessage;
import com.example.social.common.GlobalExceptionHandler;
import com.example.social.dto.response.Response;
import com.example.social.dto.response.postResponse.ExceptionResponse;
import com.example.social.dto.response.userResponse.UserSigninResponse;
import io.jsonwebtoken.ExpiredJwtException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TestGlobalExceptionHandler {
    @InjectMocks
    GlobalExceptionHandler globalExceptionHandler;
    @Test
    void handleValidationExceptions() {
        MethodArgumentNotValidException mockException = mock(MethodArgumentNotValidException.class);

        FieldError fieldError = new FieldError("objectName", "fieldName", "error message");

        when(mockException.getFieldErrors()).thenReturn(List.of(fieldError));

        ResponseEntity<?> result = globalExceptionHandler.handleValidationExceptions(mockException);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
    }
    @Test
    void badCredentialsException() {
        BadCredentialsException mockException = mock(BadCredentialsException.class);
        ExceptionResponse response = new ExceptionResponse();
        response.setMessage(CommonMessage.WRONG_PASSWORD);
        ResponseEntity<?> result = globalExceptionHandler.badCredentialsException(mockException);

        assertEquals(HttpStatus.UNAUTHORIZED, result.getStatusCode());
        assertEquals(response, result.getBody());
    }

    @Test
    void handleNoSuchElementException() {
        NoSuchElementException mockException = mock(NoSuchElementException.class);
        ExceptionResponse exceptionResponse = new ExceptionResponse();
        ResponseEntity<Object> result = globalExceptionHandler.handleNoSuchElementException(mockException);

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertEquals(exceptionResponse, result.getBody());
    }

    @Test
    void handleExpiredJwtException() {
        ExpiredJwtException mockException = mock(ExpiredJwtException.class);

        ResponseEntity<String> result = globalExceptionHandler.handleExpiredJwtException(mockException);

        assertEquals(HttpStatus.UNAUTHORIZED, result.getStatusCode());
        assertEquals("JWT Token has expired", result.getBody());
    }
}
