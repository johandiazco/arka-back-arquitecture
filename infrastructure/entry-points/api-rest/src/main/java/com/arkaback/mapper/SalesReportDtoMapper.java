package com.arkaback.mapper;

import com.arkaback.dto.salesReport.SalesReportResponse;
import com.arkaback.entity.salesReport.SalesReport;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class SalesReportDtoMapper {

    public SalesReportResponse toResponse(SalesReport report) {
        if (report == null) return null;

        List<SalesReportResponse.TopProductResponse> products = report.getTopProducts().stream()
                .map(this::topProductToResponse)
                .collect(Collectors.toList());

        List<SalesReportResponse.TopCustomerResponse> customers = report.getTopCustomers().stream()
                .map(this::topCustomerToResponse)
                .collect(Collectors.toList());

        return SalesReportResponse.builder()
                .startDate(report.getStartDate())
                .endDate(report.getEndDate())
                .period(report.getPeriod())
                .totalSales(report.getTotalSales())
                .totalOrders(report.getTotalOrders())
                .totalProducts(report.getTotalProducts())
                .averageOrderValue(report.getAverageOrderValue())
                .topProducts(products)
                .topCustomers(customers)
                .previousPeriodSales(report.getPreviousPeriodSales())
                .salesGrowthPercentage(report.getSalesGrowthPercentage())
                .build();
    }

    private SalesReportResponse.TopProductResponse topProductToResponse(SalesReport.TopProduct product) {
        return SalesReportResponse.TopProductResponse.builder()
                .productId(product.getProductId())
                .productName(product.getProductName())
                .productSku(product.getProductSku())
                .categoryName(product.getCategoryName())
                .quantitySold(product.getQuantitySold())
                .totalRevenue(product.getTotalRevenue())
                .build();
    }

    private SalesReportResponse.TopCustomerResponse topCustomerToResponse(SalesReport.TopCustomer customer) {
        return SalesReportResponse.TopCustomerResponse.builder()
                .customerId(customer.getCustomerId())
                .customerName(customer.getCustomerName())
                .customerEmail(customer.getCustomerEmail())
                .totalOrders(customer.getTotalOrders())
                .totalSpent(customer.getTotalSpent())
                .build();
    }
}
