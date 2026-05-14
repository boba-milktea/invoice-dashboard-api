package edu.hyf.invoice.invoice;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name="invoice_item")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter

public class InvoiceItem {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "invoiceItemSeqGen")
    @SequenceGenerator(name = "invoiceItemSeqGen", sequenceName = "invoice_item_id_sequence", allocationSize = 1)
    private Long id;

    @NotBlank
    @Size(min=10, max=255)
    @Column(nullable = false)
    private String description;

    @NotNull
    @Column(nullable = false)
    @Positive
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
    @JsonBackReference
    private Invoice invoice;

    // Audition
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

}
