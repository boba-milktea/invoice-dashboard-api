package edu.hyf.invoice.invoice.dto;

import java.math.BigDecimal;

public record InvoiceItemResponse(
        Long id,
        String description,
        Integer quantity,
        BigDecimal unitPrice,
        BigDecimal lineTotal
) { }