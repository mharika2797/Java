package com.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

// record = immutable DTO; Jackson serializes/deserializes it automatically
// Validation annotations are checked by @Valid in the controller before reaching the service
public record ProductDTO(
        Long id,

        @NotBlank(message = "name must not be blank")
        String name,

        String description,

        @Positive(message = "price must be greater than zero")
        double price
) {}
