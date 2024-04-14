package com.youneskarir.springsecuritydemo.advice.custom;

public class NotValidTokenException extends RuntimeException {
    public NotValidTokenException() {
        super();
    }

    public NotValidTokenException(String message) {
        super(message);
    }

    public NotValidTokenException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotValidTokenException(Throwable cause) {
        super(cause);
    }

    protected NotValidTokenException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
