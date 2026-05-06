package edu.hyf.invoice.invoice;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="invoice_item")

public class InvoiceItem {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "invoiceItemSeqGen")
    @SequenceGenerator(name = "invoiceItemSeqGen", sequenceName = "invoice_item_id_sequence", allocationSize = 1)
    private Long id;

    @NotBlank
    @Size(min=10, message = "Description should be more than 10 characters.")
    @Column(nullable = false)
    private String description;

    @NotNull
    @Column(nullable = false)
    private Integer quantity;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal lineTotal;

    // Invoice 1:N InvoiceItem
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="invoice_id")
    private Invoice invoice;

    // Audition
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
