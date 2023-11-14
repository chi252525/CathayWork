package com.example.exception;

import com.example.utils.StringProjectUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code= HttpStatus.INTERNAL_SERVER_ERROR)
public class InternalServerException extends RuntimeException{
    private static final long serialVersionUID = -5324501846354372026L;

    public InternalServerException() {
    }

    public InternalServerException(String message,Object... params) {
        super(StringProjectUtils.format(message,params));
    }

    public InternalServerException(String message, Throwable cause) {
        super(message, cause);
    }

    public InternalServerException(Throwable cause) {
        super(cause);
    }

    public InternalServerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
