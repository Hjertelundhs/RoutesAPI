package com.hjertelundh.routes.exception;

import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(
        value = HttpStatus.BAD_GATEWAY,
        reason = "Failed to get successful response from external api")
@NoArgsConstructor
public class ExternalApiException extends RuntimeException {

    public ExternalApiException(Throwable throwable) {
        super(throwable);
    }
}