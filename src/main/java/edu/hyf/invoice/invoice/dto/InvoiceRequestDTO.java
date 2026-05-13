package edu.hyf.invoice.invoice.dto;

import edu.hyf.invoice.invoice.Status;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data

public class InvoiceRequestDTO {

    @NotBlank (message="Invoice Reference is required.")
    @Size(min = 8, message="Reference should be more than 8 characters")
    public String invoiceReference;

    @NotBlank (message="Issue date is required.")
    private LocalDate issueDate;

    @NotBlank (message="Due date is required.")
    private LocalDate dueDate;

    @NotBlank (message="Status is required.")
    @Enumerated(EnumType.STRING)
    private Status status;

    @NotNull (message = "Subtotal should be a positive number.")
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal subtotal;

    @NotNull (message = "Tax amount should be a positive number.")
    @DecimalMin(value = "0.0")
    private BigDecimal taxAmount;

    @NotNull (message = "Total amount should be a positive number.")
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal totalAmount;

}
