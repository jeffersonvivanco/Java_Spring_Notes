package app.controllers;

import app.exceptions.ErisAppException;
import app.exceptions.ErisAppRuntimeException;
import app.models.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    private static Logger loggerz = LoggerFactory.getLogger(RestResponseEntityExceptionHandler.class);

    @ExceptionHandler(value = {ErisAppException.class})
    protected ResponseEntity handleConflict(ErisAppException ex, WebRequest request){
        Status bodyResponse = new Status(HttpStatus.CONFLICT.value(), ex.getMessage());
        loggerz.error("Eris app error: {{}}", ex.getMessage());
        return handleExceptionInternal(ex, bodyResponse, new HttpHeaders(), HttpStatus.CONFLICT, request);
    }

    @ExceptionHandler(value = ErisAppRuntimeException.class)
    protected ResponseEntity handleRuntimeConflict(ErisAppRuntimeException ex, WebRequest request){
        Status bodyResponse = new Status(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage());
        loggerz.error("Eris runtime app error: {{}}", ex.getMessage());
        return handleExceptionInternal(ex, bodyResponse, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }


}
