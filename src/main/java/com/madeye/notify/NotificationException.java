package com.madeye.notify;

/**
 * Created with IntelliJ IDEA.
 * User: matt
 * Date: 1/9/14
 * Time: 5:40 PM
 * Copyright blinkbox books (c) 2013
 */
public class NotificationException extends Exception {
    public NotificationException() {
    }

    public NotificationException(String message) {
        super(message);
    }

    public NotificationException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotificationException(Throwable cause) {
        super(cause);
    }

    public NotificationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
