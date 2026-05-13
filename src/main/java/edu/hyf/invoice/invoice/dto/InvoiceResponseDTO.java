package edu.hyf.invoice.invoice.dto;


import edu.hyf.invoice.invoice.Status;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;


public record InvoiceResponseDTO (String invoiceReference,
                                  LocalDate issueDate, LocalDate dueDate,
                                  Status status,
                                  BigDecimal subtotal, BigDecimal taxAmount, BigDecimal totalAmount,
                                  UUID userId, UUID clientId) { }
