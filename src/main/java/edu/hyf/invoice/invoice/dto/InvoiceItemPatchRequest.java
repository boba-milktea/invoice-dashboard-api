package edu.hyf.invoice.invoice.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record InvoiceItemPatchRequest(
        @Size(min = 10, max = 255)
        String description,
        @Positive
        Integer quantity,
        @DecimalMin(value = "0.0", inclusive = false)
        BigDecimal unitPrice
) {}