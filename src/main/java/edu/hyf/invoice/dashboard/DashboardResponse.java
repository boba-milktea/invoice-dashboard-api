package edu.hyf.invoice.dashboard;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
@AllArgsConstructor

public class DashboardResponse {
    private BigDecimal totalRevenue;
    private BigDecimal unpaidAmount;
    private Long totalInvoicesCount;
    private Long paidInvoicesCount;
    private Long pendingInvoicesCount;
    private Long overdueInvoicesCount;
}
