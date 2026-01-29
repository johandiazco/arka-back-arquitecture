package com.arkaback.entity.inventory;

import com.arkaback.entity.product.ProductEntity;
import com.arkaback.entity.supplier.SupplierEntity;
import com.arkaback.entity.warehouse.WarehouseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "inventory")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private ProductEntity product;

    @Column(name = "stock_actual")
    private Integer stockActual;

    @Column(name = "stock_reserved")
    private Integer stockReserved;

    @Column(name = "stock_available", insertable = false, updatable = false)
    private Integer stockAvailable;

    @ManyToOne
    @JoinColumn(name = "warehouse_id", nullable = false)
    private WarehouseEntity warehouse;

    @ManyToOne
    @JoinColumn(name = "supplier_id")
    private SupplierEntity supplier;
}
