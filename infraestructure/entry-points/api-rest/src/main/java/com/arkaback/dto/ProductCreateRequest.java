package com.arkaback.dto;

import com.arkaback.entity.Category;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductCreateRequest {

    @NotBlank(message = "El nombre es obligatorio")
    private String name;

    private String description;

    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio debe ser mayor a 0")
    private BigDecimal price;

    @NotBlank(message = "El SKU es obligatorio")
    private String sku;

    private String brand;

    @Min(value = 0, message = "El stock mínimo no puede ser negativo")
    private Integer minStock;

    @NotNull(message = "La categoría es obligatoria")
    private Category category;

    @NotNull(message = "El warehouseId es obligatorio")
    private Long warehouseId;

    @NotNull(message = "El supplierId es obligatorio")
    private Long supplierId;
}
