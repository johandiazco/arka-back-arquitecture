package com.arkaback.ports.output;

import com.arkaback.entity.salesReport.SalesReport;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface SalesReportPersistencePort {
    BigDecimal getTotalSales(LocalDate startDate, LocalDate endDate);
    Integer countOrders(LocalDate startDate, LocalDate endDate);
    Integer countProductsSold(LocalDate startDate, LocalDate endDate);
    List<SalesReport.TopProduct> getTopProducts(LocalDate startDate, LocalDate endDate, Integer limit);
    List<SalesReport.TopCustomer> getTopCustomers(LocalDate startDate, LocalDate endDate, Integer limit);
}













