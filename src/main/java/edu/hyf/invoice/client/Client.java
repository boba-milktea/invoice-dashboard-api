package edu.hyf.invoice.client;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import edu.hyf.invoice.invoice.Invoice;
import edu.hyf.invoice.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Entity
@Table(name="client")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter

public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank(message = "Name is required.")
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "Email is required.")
    @Email(message = "Email format is invalid.")
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank(message = "Address is required.")
    @Column(nullable = false)
    @Size(min = 5, max = 255)
    @Pattern(regexp = "^[a-zA-Z0-9\\s,.-]+$", message = "Address contains invalid characters.")
    private String address;

<<<<<<< HEAD
    //TODO having authentication for clients as well

=======
>>>>>>> 1c82202 (add invoice testing)
    // Client 1:N Invoice
    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Invoice> invoices = new ArrayList<>();

    // User 1:N Client
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private User user;

    // Audition
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
