package com.example.exception;

import com.example.utils.StringProjectUtils;

public class ExternalServiceException extends RuntimeException{
    private static final long serialVersionUID = -6324501846354372026L;
    private String errorResponseBody;

    public String getErrorResponseBody() {
        return errorResponseBody;
    }

    public void setErrorResponseBody(String errorResponseBody) {
        this.errorResponseBody = errorResponseBody;
    }

    public ExternalServiceException() {
    }

    public ExternalServiceException(String message, Object... params) {
        super(StringProjectUtils.format(message,params));
    }

    public ExternalServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExternalServiceException(Throwable cause) {
        super(cause);
    }

    public ExternalServiceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
