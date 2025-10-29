package com.api.pos_backend.records;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"id", "role", "username", "message", "token", "status"})
public record AuthResponse(String id,
                           String role,
                           String username,
                           String message,
                           String token,
                           Boolean status) {

}
