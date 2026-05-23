package edu.hyf.invoice.invoice;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
    List<@NonNull Invoice> getDueInvoice(@Param("statuses") List<Status> statuses);

    @Query("""
            select i from Invoice i
            where i.user.id = :userId
            and i.dueDate < CURRENT_DATE
            and i.status in :statuses
            """)
    List<@NonNull Invoice> getDueInvoiceById(@Param("userId") UUID userId, @Param("statuses") List<Status> statuses);

    @Query("""
            select i from Invoice i
            where i.totalAmount between :min and :max
            """)
    List<@NonNull Invoice> findInvoicesWithMinMax(@Param("min") BigDecimal min, @Param("max") BigDecimal max);

    @Query("""
            select i from Invoice i
            where i.user.id = :userId
            and i.totalAmount between :min and :max
            """)
    List<@NonNull Invoice> findInvoicesWithMinMaxById(@Param("userId") UUID userId, @Param("min") BigDecimal min, @Param("max") BigDecimal max);

    Optional<Invoice> findByReference(String ref);

    boolean existsByReferenceAndUserId(String reference, UUID id);

    // Dashboard

    @Query("""
            SELECT SUM(i.totalAmount) FROM Invoice i
            WHERE i.status = :status
            """)
    BigDecimal sumPaidTotalAmount(@Param("status") Status status);

    @Query("""
            SELECT SUM(i.totalAmount) FROM Invoice i
            WHERE i.user.id = :userId
            AND i.status = :status
            """)
    BigDecimal sumPaidTotalAmountByUserId(@Param("userId") UUID userId, @Param("status") Status status);

    @Query("""
            SELECT SUM(i.totalAmount)
            FROM Invoice i
            WHERE i.status in :statuses
            """)
    BigDecimal sumUnpaidTotalAmount(@Param("statuses") List<Status> statuses);

    @Query("""
            SELECT SUM(i.totalAmount)
            FROM Invoice i
            WHERE i.user.id = :userId
            AND i.status in :statuses
            """)
    BigDecimal sumUnpaidTotalAmountByUserId(@Param("userId") UUID userId, @Param("statuses") List<Status> statuses);

    Long countByUserId(UUID userId);

    @Query("""
            SELECT COUNT(i)
            FROM Invoice i
            WHERE i.status = :overdue
            """)
    Long countOverdueInvoices(@Param("overdue") Status overdue);

    @Query("""
        SELECT COUNT(i)
        FROM Invoice i
        WHERE i.user.id = :userId
        AND i.status = :overdue
        """)
    Long countOverdueInvoicesByUserId(
            @Param("userId") UUID userId,
            @Param("overdue") Status overdue
    );

    @Query("""
            SELECT COUNT(i)
            FROM Invoice i
            WHERE i.status = :pending
            """)
    Long countPendingInvoices(@Param("pending") Status pending);

    @Query("""
        SELECT COUNT(i)
        FROM Invoice i
        WHERE i.user.id = :userId
        AND i.status = :pending
        """)
    Long countPendingInvoicesByUserId(
            @Param("userId") UUID userId,
            @Param("pending") Status pending
    );


    @Query("""
            SELECT COUNT(i)
            FROM Invoice i
            WHERE i.status = :paid
            """)
    Long countPaidInvoices(@Param("paid") Status paid);

    @Query("""
        SELECT COUNT(i)
        FROM Invoice i
        WHERE i.user.id = :userId
        AND i.status = :paid
        """)
    Long countPaidInvoicesByUserId(
            @Param("userId") UUID userId,
            @Param("paid") Status paid
    );





}
