package edu.hyf.invoice.dashboard;

import edu.hyf.invoice.common.utils.AccessHelper;
import edu.hyf.invoice.invoice.InvoiceRepository;
import edu.hyf.invoice.invoice.Status;
import edu.hyf.invoice.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor

public class DashboardService {
    private final InvoiceRepository invoiceRepository;
    private final AccessHelper accessHelper;

    public DashboardResponse createSummary(UserPrincipal userPrincipal, UUID ownerUserId) {

        return new DashboardResponse(
                calculateTotalRevenue(userPrincipal, ownerUserId),
                calculateUnpaidAmount(userPrincipal,ownerUserId),
                countTotalInvoice(userPrincipal, ownerUserId),
                countPaidInvoices(userPrincipal, ownerUserId),
                countPendingInvoices(userPrincipal,ownerUserId),
                countOverdueInvoices(userPrincipal, ownerUserId)
        );
    }

    public BigDecimal calculateTotalRevenue(UserPrincipal userPrincipal, UUID ownerUserId) {

        Status invoiceStatus = Status.PAID;

        return calculateValue(
                userPrincipal,
                ownerUserId,
                () -> invoiceRepository.sumPaidTotalAmount(invoiceStatus),
                userId -> invoiceRepository.sumPaidTotalAmountByUserId(userId, invoiceStatus),
                BigDecimal.ZERO);

    }

    public BigDecimal calculateUnpaidAmount(UserPrincipal userPrincipal, UUID ownerUserId) {

        List<Status> statuses = List.of(Status.PENDING, Status.OVERDUE);

        return calculateValue(
                userPrincipal,
                ownerUserId,
                () -> invoiceRepository.sumUnpaidTotalAmount(statuses),
                userId -> invoiceRepository.sumUnpaidTotalAmountByUserId(userId, statuses),
                BigDecimal.ZERO
        );
    }

    public Long countTotalInvoice(UserPrincipal userPrincipal, UUID ownerUserId) {

        return calculateValue(
                userPrincipal,
                ownerUserId,
                invoiceRepository::count,
                invoiceRepository::countByUserId,
                0L
        );

    }

    public Long countOverdueInvoices(UserPrincipal userPrincipal, UUID ownerUserId) {

        Status invoiceStatus = Status.OVERDUE;

        return calculateValue(
                userPrincipal,
                ownerUserId,
                () -> invoiceRepository.countOverdueInvoices(invoiceStatus),
                userId -> invoiceRepository.countOverdueInvoicesByUserId(userId, invoiceStatus),
                0L
        );
    }


    public Long countPaidInvoices(UserPrincipal userPrincipal, UUID ownerUserId) {

        Status invoiceStatus = Status.PAID;

        return calculateValue(
                userPrincipal,
                ownerUserId,
                () -> invoiceRepository.countPaidInvoices(invoiceStatus),
                userId -> invoiceRepository.countPaidInvoicesByUserId(userId, invoiceStatus),
                0L
        );
    }


    public Long countPendingInvoices(UserPrincipal userPrincipal, UUID ownerUserId) {

        Status invoiceStatus = Status.PENDING;

        return calculateValue(
                userPrincipal,
                ownerUserId,
                () -> invoiceRepository.countPendingInvoices(invoiceStatus),
                userId -> invoiceRepository.countPendingInvoicesByUserId(userId, invoiceStatus),
                0L
        );
    }

    // Helper
    private <T> T calculateValue(
            UserPrincipal userPrincipal,
            UUID ownerUserId,
            Supplier<T> globalQuery,
            Function<UUID, T> userQuery,
            T defaultValue) {

        T result;

        if (ownerUserId == null && userPrincipal.isSuperAdmin() ){

            result = globalQuery.get();

        }  else  {

            UUID userId = accessHelper.resolveOwnerUserId(userPrincipal, ownerUserId);

           result = userQuery.apply(userId);
        }
        return  Optional.ofNullable(result).orElse(defaultValue);

    }



}
