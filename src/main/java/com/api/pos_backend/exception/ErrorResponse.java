package com.api.pos_backend.exception;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Data
public class ErrorResponse {
    private String message;
    private int status;
    private LocalDate timestamp;
    private String details;

    public ErrorResponse(String message, int status, String details) {
        this.message = message;
        this.status = status;
        this.timestamp = LocalDate.now();
        this.details = details;
    }
}
