package edu.hyf.invoice.invoice;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import java.util.UUID;

@Repository

public interface InvoiceRepository extends JpaRepository<@NonNull Invoice, @NonNull Long> {

    Page<@NonNull Invoice> findByUserId(UUID userId, Pageable pageable);

    Optional<Invoice> findByReferenceAndUserId(String ref, UUID userId);

    @Query("""
            select i from Invoice i
            where i.dueDate < CURRENT_DATE
            and i.status in :statuses
            """)
    List<@NonNull Invoice> getDueInvoice(List<Status> statuses);

    @Query("""
            select i from Invoice i
            where i.user.id = :userId
            and i.dueDate < CURRENT_DATE
            and i.status in :statuses
            """)
    List<@NonNull Invoice> getDueInvoiceById(UUID userId, List<Status> statuses);

    @Query("""
            select i from Invoice i
            where i.totalAmount between :min and :max
            """)
    List<@NonNull Invoice> findInvoicesWithMinMax(BigDecimal min, BigDecimal max);

    @Query("""
            select i from Invoice i
            where i.user.id = :userId
            and i.totalAmount between :min and :max
            """)
    List<@NonNull Invoice> findInvoicesWithMinMaxById(UUID userId, BigDecimal min, BigDecimal max);

    Optional<Invoice> findByReference(String ref);

    boolean existsByReferenceAndUserId(String reference, UUID id);
}
