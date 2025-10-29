package com.api.pos_backend.records;

import org.springframework.validation.annotation.Validated;

import java.util.List;

@Validated
public record AuthCreateRoleRequest(List<String> roleListName) {
}

