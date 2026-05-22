package edu.hyf.invoice.invoice.dto;

import edu.hyf.invoice.invoice.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.UUID;

// TODO dates validation - createdAt and updatedAt dates should be reasonable
// TODO reference to be varified with pattern


public record InvoiceRequest (@NotBlank(message = "Invoice reference is required.")
                              @Size(min = 3, max = 100)
                              String reference,
                              @NotNull(message = "Issue date is required.")
                              LocalDate issueDate,
                              @NotNull(message = "Due date is required.")
                              LocalDate dueDate,
                              @NotNull(message = "Status is required.")
                              Status status,
                              @NotNull(message = "Client id is required.")
                              UUID clientId,
                              UUID ownerUserId
) {}
