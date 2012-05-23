package org.glite.ce.creamapi.activitymanagement;

public class ActivityException extends Exception {

    public ActivityException() {
    }

    public ActivityException(String message) {
        super(message);
    }

    public ActivityException(Throwable cause) {
        super(cause);
    }

    public ActivityException(String message, Throwable cause) {
        super(message, cause);
    }
}
