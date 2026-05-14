package edu.hyf.invoice.invoice.dto;

import edu.hyf.invoice.invoice.Status;

import java.time.LocalDate;
import java.util.UUID;

public record InvoicePatchRequest(
        LocalDate issueDate,
        LocalDate dueDate,
        Status status,
        UUID clientId

) {}