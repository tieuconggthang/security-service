package vn.napas.security.controller;

import vn.napas.security.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({IllegalArgumentException.class, MethodArgumentNotValidException.class})
    public ErrorResponse badRequest(Exception e) {
        return new ErrorResponse("BAD_REQUEST", e.getMessage());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ErrorResponse serverError(Exception e) {
        return new ErrorResponse("INTERNAL_ERROR", e.getMessage());
    }
}
