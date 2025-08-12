package com.fundicion.lara.config;

import com.fundicion.lara.commons.data.CommonErrorResponse;
import com.fundicion.lara.exception.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import static org.springframework.http.HttpStatus.*;

@RestControllerAdvice
public class ControllerHandler {

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(NOT_FOUND)
    public ResponseEntity<CommonErrorResponse> handleNotFoundException(NotFoundException ex) {
        CommonErrorResponse error = CommonErrorResponse.builder()
                .errors(ex.getErrors())
                .message(ex.getMessage()).build();
        return new ResponseEntity<>(error, NOT_FOUND);
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(BAD_REQUEST)
    public ResponseEntity<CommonErrorResponse> handleBadRequestException(BadRequestException ex) {
        CommonErrorResponse error = CommonErrorResponse.builder()
                .errors(ex.getErrors())
                .message(ex.getMessage()).build();
        return new ResponseEntity<>(error, BAD_REQUEST);
    }

    @ExceptionHandler(ConflictException.class)
    @ResponseStatus(CONFLICT)
    public ResponseEntity<CommonErrorResponse> handleConflictException(ConflictException ex) {
        CommonErrorResponse error = CommonErrorResponse.builder()
                .errors(ex.getErrors())
                .message(ex.getMessage()).build();
        return new ResponseEntity<>(error, CONFLICT);
    }

    @ExceptionHandler(InternalException.class)
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    public ResponseEntity<CommonErrorResponse> handleIInternalException(InternalException ex) {
        CommonErrorResponse error = CommonErrorResponse.builder()
                .errors(ex.getErrors())
                .message(ex.getMessage()).build();
        return new ResponseEntity<>(error, INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(UnauthorizedException.class)
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    public ResponseEntity<CommonErrorResponse> handleUnauthorizedException(UnauthorizedException ex) {
        CommonErrorResponse error = CommonErrorResponse.builder()
                .errors(ex.getErrors())
                .message(ex.getMessage()).build();
        return new ResponseEntity<>(error, INTERNAL_SERVER_ERROR);
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<CommonErrorResponse> handleAllExceptions(Exception ex, WebRequest request) {
        CommonErrorResponse error = CommonErrorResponse.builder()
                .message(ex.getMessage()).build();
        return new ResponseEntity<>(error, INTERNAL_SERVER_ERROR);
    }
}
