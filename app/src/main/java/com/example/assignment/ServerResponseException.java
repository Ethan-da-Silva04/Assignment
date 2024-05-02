package com.example.assignment;

public class ServerResponseException extends Exception {
    private final String message;
    private final int statusCode;

    public ServerResponseException(String message, int statusCode) {
        super(String.format("Received error status code: %d with message: %s", statusCode, message));
        this.message = message;
        this.statusCode = statusCode;
    }

    public String getMessage() {
        return message;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
