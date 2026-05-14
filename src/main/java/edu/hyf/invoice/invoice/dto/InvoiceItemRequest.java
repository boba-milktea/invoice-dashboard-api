package edu.hyf.invoice.invoice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.DecimalMin;
import java.math.BigDecimal;



public record InvoiceItemRequest (
        @NotBlank
        @Size(min=10, max=255)
        String description,

        @NotNull
        @Positive
        Integer quantity,

        @NotNull
        @DecimalMin(value = "0.0", inclusive = false)
        BigDecimal unitPrice) { }
