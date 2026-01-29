package com.arkaback.entity.salesReport;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class SalesReport {

    private LocalDate startDate;
    private LocalDate endDate;
    private String period;  //Semanal, Mensual u diario.

    //Total general
    private BigDecimal totalSales;
    private Integer totalOrders;
    private Integer totalProducts;
    private BigDecimal averageOrderValue;

    //Productos más vendidos
    @Builder.Default
    private List<TopProduct> topProducts = new ArrayList<>();

    //Clientes más frecuentes
    @Builder.Default
    private List<TopCustomer> topCustomers = new ArrayList<>();

    //Comparativa con período anterior
    private BigDecimal previousPeriodSales;
    private BigDecimal salesGrowthPercentage;

    //Calcula valor promedio de las órdenes
    public BigDecimal calculateAverageOrderValue() {
        if (totalOrders == null || totalOrders == 0) {
            return BigDecimal.ZERO;
        }
        return totalSales.divide(BigDecimal.valueOf(totalOrders), 2, BigDecimal.ROUND_HALF_UP);
    }

    //Calcula porcentaje de crecimiento
    public BigDecimal calculateGrowthPercentage() {
        if (previousPeriodSales == null || previousPeriodSales.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal difference = totalSales.subtract(previousPeriodSales);
        return difference.divide(previousPeriodSales, 4, BigDecimal.ROUND_HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }

    //Valida que el reporte tenga datos mínimos
    public void validate() {
        if (startDate == null) {
            throw new IllegalArgumentException("La fecha de inicio es obligatoria");
        }
        if (endDate == null) {
            throw new IllegalArgumentException("La fecha de fin es obligatoria");
        }
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("La fecha de inicio debe ser anterior a la fecha de fin");
        }
    }

    // Clase interna. Producto más vendido
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TopProduct {
        private Long productId;
        private String productName;
        private String productSku;
        private String categoryName;
        private Integer quantitySold;
        private BigDecimal totalRevenue;
    }

    //Clase interna. Cliente más frecuente
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TopCustomer {
        private Long customerId;
        private String customerName;
        private String customerEmail;
        private Integer totalOrders;
        private BigDecimal totalSpent;
    }
}













