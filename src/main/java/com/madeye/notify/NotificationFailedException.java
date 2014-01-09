package com.madeye.notify;

class NotificationFailedException extends NotificationException {
    public NotificationFailedException(String message) {
        super(message);
    }

    public NotificationFailedException() {
    }

    public NotificationFailedException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotificationFailedException(Throwable cause) {
        super(cause);
    }

    public NotificationFailedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

