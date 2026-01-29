package com.arkaback.dto.salesReport;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SalesReportResponse {

    private LocalDate startDate;
    private LocalDate endDate;
    private String period;
    private BigDecimal totalSales;
    private Integer totalOrders;
    private Integer totalProducts;
    private BigDecimal averageOrderValue;

    //Productos más vendidos
    private List<TopProductResponse> topProducts;

    //Clientes más frecuentes
    private List<TopCustomerResponse> topCustomers;

    private BigDecimal previousPeriodSales;
    private BigDecimal salesGrowthPercentage;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TopProductResponse {
        private Long productId;
        private String productName;
        private String productSku;
        private String categoryName;
        private Integer quantitySold;
        private BigDecimal totalRevenue;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TopCustomerResponse {
        private Long customerId;
        private String customerName;
        private String customerEmail;
        private Integer totalOrders;
        private BigDecimal totalSpent;
    }
}













