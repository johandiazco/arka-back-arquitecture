package com.arkaback.adapter;

import com.arkaback.entity.salesReport.SalesReport;
import com.arkaback.ports.output.SalesReportPersistencePort;
import com.arkaback.repository.OrderJpaRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
@AllArgsConstructor
public class SalesReportJpaAdapter implements SalesReportPersistencePort {

    private final EntityManager entityManager;
    private final OrderJpaRepository orderRepository;

    @Override
    public BigDecimal getTotalSales(LocalDate startDate, LocalDate endDate) {
        String jpql = """
            SELECT COALESCE(SUM(od.quantity * od.unitPrice), 0)
            FROM OrderEntity o
            JOIN o.details od
            WHERE DATE(o.orderDate) BETWEEN :startDate AND :endDate
            AND o.orderStatus IN ('CONFIRMADO', 'ENVIADO', 'ENTREGADO')
        """;

        Query query = entityManager.createQuery(jpql);
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);

        Object result = query.getSingleResult();
        return result != null ? (BigDecimal) result : BigDecimal.ZERO;
    }

    @Override
    public Integer countOrders(LocalDate startDate, LocalDate endDate) {
        String jpql = """
            SELECT COUNT(o)
            FROM OrderEntity o
            WHERE DATE(o.orderDate) BETWEEN :startDate AND :endDate
            AND o.orderStatus IN ('CONFIRMADO', 'ENVIADO', 'ENTREGADO')
        """;

        Query query = entityManager.createQuery(jpql);
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);

        Long count = (Long) query.getSingleResult();
        return count != null ? count.intValue() : 0;
    }

    @Override
    public Integer countProductsSold(LocalDate startDate, LocalDate endDate) {
        String jpql = """
            SELECT COALESCE(SUM(od.quantity), 0)
            FROM OrderEntity o
            JOIN o.details od
            WHERE DATE(o.orderDate) BETWEEN :startDate AND :endDate
            AND o.orderStatus IN ('CONFIRMADO', 'ENVIADO', 'ENTREGADO')
        """;

        Query query = entityManager.createQuery(jpql);
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);

        Long sum = (Long) query.getSingleResult();
        return sum != null ? sum.intValue() : 0;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<SalesReport.TopProduct> getTopProducts(LocalDate startDate, LocalDate endDate, Integer limit) {
        String jpql = """
            SELECT 
                p.id,
                p.name,
                p.sku,
                c.name,
                SUM(od.quantity) as totalQuantity,
                SUM(od.quantity * od.unitPrice) as totalRevenue
            FROM OrderEntity o
            JOIN o.details od
            JOIN od.product p
            LEFT JOIN p.category c
            WHERE DATE(o.orderDate) BETWEEN :startDate AND :endDate
            AND o.orderStatus IN ('CONFIRMADO', 'ENVIADO', 'ENTREGADO')
            GROUP BY p.id, p.name, p.sku, c.name
            ORDER BY totalQuantity DESC
        """;

        Query query = entityManager.createQuery(jpql);
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);
        query.setMaxResults(limit);

        List<Object[]> results = query.getResultList();
        List<SalesReport.TopProduct> topProducts = new ArrayList<>();

        for (Object[] row : results) {
            SalesReport.TopProduct product = SalesReport.TopProduct.builder()
                    .productId((Long) row[0])
                    .productName((String) row[1])
                    .productSku((String) row[2])
                    .categoryName((String) row[3])
                    .quantitySold(((Long) row[4]).intValue())
                    .totalRevenue((BigDecimal) row[5])
                    .build();
            topProducts.add(product);
        }

        return topProducts;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<SalesReport.TopCustomer> getTopCustomers(LocalDate startDate, LocalDate endDate, Integer limit) {
        String jpql = """
            SELECT 
                p.id,
                p.name,
                p.email,
                COUNT(o.id) as totalOrders,
                SUM(od.quantity * od.unitPrice) as totalSpent
            FROM OrderEntity o
            JOIN o.person p
            JOIN o.details od
            WHERE DATE(o.orderDate) BETWEEN :startDate AND :endDate
            AND o.orderStatus IN ('CONFIRMADO', 'ENVIADO', 'ENTREGADO')
            GROUP BY p.id, p.name, p.email
            ORDER BY totalOrders DESC, totalSpent DESC
        """;

        Query query = entityManager.createQuery(jpql);
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);
        query.setMaxResults(limit);

        List<Object[]> results = query.getResultList();
        List<SalesReport.TopCustomer> topCustomers = new ArrayList<>();

        for (Object[] row : results) {
            SalesReport.TopCustomer customer = SalesReport.TopCustomer.builder()
                    .customerId((Long) row[0])
                    .customerName((String) row[1])
                    .customerEmail((String) row[2])
                    .totalOrders(((Long) row[3]).intValue())
                    .totalSpent((BigDecimal) row[4])
                    .build();
            topCustomers.add(customer);
        }

        return topCustomers;
    }
}

