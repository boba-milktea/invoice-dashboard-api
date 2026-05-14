package edu.hyf.invoice.invoice;


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
            where i.user.id = :userId
            and i.dueDate < CURRENT_DATE
            and i.status in :statuses
            """)
    List<@NonNull Invoice> getDueInvoice(UUID userId, List<Status> statuses);

    @Query("""
            select i from Invoice i
            where i.user.id = :userId
            and i.totalAmount between :min and :max
            """)
    List<@NonNull Invoice> findInvoicesWithMinMax(UUID userId, BigDecimal min, BigDecimal max);

    boolean existsByReference(String ref);

    boolean existsByReferenceAndUserId(String reference, UUID userId);

}
