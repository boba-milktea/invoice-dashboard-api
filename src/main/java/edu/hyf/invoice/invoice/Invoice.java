package edu.hyf.invoice.invoice;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import edu.hyf.invoice.client.Client;
import edu.hyf.invoice.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "invoice", uniqueConstraints = {
        @UniqueConstraint(name = "uk_invoice_user_ref", columnNames = {"user_id", "invoice_ref"})
})
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter

public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "invoiceSeqGen")
    @SequenceGenerator(name = "invoiceSeqGen", sequenceName = "invoice_id_sequence", allocationSize = 1)
    private Long id;

    @NotBlank(message = "Invoice Reference can't be blank")
    @Column(name = "invoice_ref", nullable = false)
    @Size(min = 3, max = 100, message = "Reference length must be between 3 and 100 characters.")
    private String reference;

    @NotNull
    @Column(name = "issue_date", nullable = false)
    private LocalDate issueDate;

    @NotNull
    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @NotNull
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = true)
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = true)
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal taxAmount;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = true)
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    //User 1:N Invoice
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="user_id")
    @JsonBackReference
    private User user;

    //Client 1:N Invoice
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="client_id")
    @JsonBackReference
    private Client client;

    // Invoice 1:N InvoiceItem
    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<InvoiceItem> invoiceItems = new ArrayList<>();

    // Audition
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
