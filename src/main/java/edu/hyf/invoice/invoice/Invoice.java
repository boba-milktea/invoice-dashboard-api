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
@Table(name="invoice")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter

/*
To do:
- add Validation for dates
 */

public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "invoiceSeqGen")
    @SequenceGenerator(name = "invoiceSeqGen", sequenceName = "invoice_id_sequence", allocationSize = 1)
    private Long id;

    @NotBlank(message = "Invoice Reference can't be blank")
    @Column(name="invoice_ref", nullable = false, unique = true)
    @Size(min = 8, message = "Reference should be more than 8 characters.")
    private String reference;

    @NotBlank
    @Column(name="issue_date", nullable = false)
    private LocalDate issueDate;

    @NotBlank
    @Column(name="due_date", nullable = false)
    private LocalDate dueDate;

    @NotBlank
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;

    @NotNull
    @DecimalMin(value = "0.0")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal taxAmount;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    //User 1:N Invoice
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="person_id")
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
