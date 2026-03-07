package com.bridgelabz.addressbook.dto;

public class ApiResponse {
    private final String message;
    private final String timestamp;

    public ApiResponse(String message, String timestamp) {
        this.message = message;
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public String getTimestamp() {
        return timestamp;
    }
}
