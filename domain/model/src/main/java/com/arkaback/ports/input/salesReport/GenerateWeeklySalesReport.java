package com.arkaback.ports.input.salesReport;

import com.arkaback.entity.salesReport.SalesReport;
import java.time.LocalDate;

public interface GenerateWeeklySalesReport {
    SalesReport generate(LocalDate startDate, LocalDate endDate);
    SalesReport generateCurrentWeek();
    SalesReport generatePreviousWeek();
}
