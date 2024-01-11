package com.realtime.protection.configuration.exception;

public class DorisStartException extends Exception {
    public Long taskId;

    public DorisStartException(Exception e, Long taskId) {
        super(e.getMessage(), e.getCause());
        this.taskId = taskId;
    }

    public DorisStartException(Exception e) {
        super(e.getMessage(), e.getCause());
    }

    public DorisStartException(String message, Long taskId) {
        super(message);
        this.taskId = taskId;
    }
}
