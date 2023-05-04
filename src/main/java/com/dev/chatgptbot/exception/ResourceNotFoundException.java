package com.dev.chatgptbot.exception;

import lombok.Getter;

@Getter
public class ResourceNotFoundException extends RuntimeException {
    private final Long id;
    private final String resourceName;
    private final String fieldName;

    public ResourceNotFoundException(String resourceName, String fieldName, Long id) {
        super(String.format("%s not found with %s : %d", resourceName, fieldName, id));
        this.id = id;
        this.resourceName = resourceName;
        this.fieldName = fieldName;
    }
}
