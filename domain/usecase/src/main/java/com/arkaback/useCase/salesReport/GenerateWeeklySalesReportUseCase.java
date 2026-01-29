package com.arkaback.useCase.salesReport;

import com.arkaback.entity.salesReport.SalesReport;
import com.arkaback.ports.input.salesReport.GenerateWeeklySalesReport;
import com.arkaback.ports.output.SalesReportPersistencePort;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@AllArgsConstructor
public class GenerateWeeklySalesReportUseCase implements GenerateWeeklySalesReport {

    private final SalesReportPersistencePort salesReportPort;
    private static final Integer TOP_PRODUCTS_LIMIT = 10;
    private static final Integer TOP_CUSTOMERS_LIMIT = 10;

    @Override
    public SalesReport generate(LocalDate startDate, LocalDate endDate) {

        //Valida fechas
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Las fechas son obligatorias");
        }
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("La fecha de inicio debe ser anterior a la fecha de fin");
        }

        //Obtiene métrica principal
        BigDecimal totalSales = salesReportPort.getTotalSales(startDate, endDate);
        Integer totalOrders = salesReportPort.countOrders(startDate, endDate);
        Integer totalProducts = salesReportPort.countProductsSold(startDate, endDate);

        //Calcula valor promedio de orden
        BigDecimal averageOrderValue = BigDecimal.ZERO;
        if (totalOrders > 0) {
            averageOrderValue = totalSales.divide(
                    BigDecimal.valueOf(totalOrders),
                    2,
                    BigDecimal.ROUND_HALF_UP
            );
        }

        //Obtiene productos más vendidos
        List<SalesReport.TopProduct> topProducts = salesReportPort.getTopProducts(
                startDate,
                endDate,
                TOP_PRODUCTS_LIMIT
        );

        //Obtiene clientes más frecuentes
        List<SalesReport.TopCustomer> topCustomers = salesReportPort.getTopCustomers(
                startDate,
                endDate,
                TOP_CUSTOMERS_LIMIT
        );

        //Calcula comparativa con período anterior
        long daysBetween = java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate) + 1;
        LocalDate previousStart = startDate.minusDays(daysBetween);
        LocalDate previousEnd = startDate.minusDays(1);

        BigDecimal previousPeriodSales = salesReportPort.getTotalSales(previousStart, previousEnd);

        //Calcula crecimiento
        BigDecimal growthPercentage = BigDecimal.ZERO;
        if (previousPeriodSales.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal difference = totalSales.subtract(previousPeriodSales);
            growthPercentage = difference.divide(previousPeriodSales, 4, BigDecimal.ROUND_HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
        }

        //Construye reporte
        SalesReport report = SalesReport.builder()
                .startDate(startDate)
                .endDate(endDate)
                .period("Semanal")
                .totalSales(totalSales)
                .totalOrders(totalOrders)
                .totalProducts(totalProducts)
                .averageOrderValue(averageOrderValue)
                .topProducts(topProducts)
                .topCustomers(topCustomers)
                .previousPeriodSales(previousPeriodSales)
                .salesGrowthPercentage(growthPercentage)
                .build();

        report.validate();

        return report;
    }

    @Override
    public SalesReport generateCurrentWeek() {
        //Obtiene lunes de la semana actual
        LocalDate today = LocalDate.now();
        LocalDate monday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate sunday = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        return generate(monday, sunday);
    }

    @Override
    public SalesReport generatePreviousWeek() {
        // Obtener lunes de la semana anterior
        LocalDate today = LocalDate.now();
        LocalDate lastMonday = today.with(TemporalAdjusters.previous(DayOfWeek.MONDAY));
        LocalDate lastSunday = lastMonday.plusDays(6);

        return generate(lastMonday, lastSunday);
    }
}

