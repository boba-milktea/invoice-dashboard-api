package edu.hyf.invoice.invoice.dto;

import edu.hyf.invoice.invoice.Status;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

// TODO still need to get the clientName

public record InvoiceResponse(
        String reference,
        LocalDate issueDate,
        LocalDate dueDate,
        Status status,

        BigDecimal subtotal,
        BigDecimal taxAmount,
        BigDecimal totalAmount,

        UUID userId,
        UUID clientId,
        String clientName,

        List<InvoiceItemResponse> items
) {}