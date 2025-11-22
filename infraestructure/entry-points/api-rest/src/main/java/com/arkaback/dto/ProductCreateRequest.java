package com.arka.dto;

import com.arkaback.entity.Categorie;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductCreateRequest {

    @NotBlank(message = "El nombre es obligatorio")
    private String name;

    private String description;

    @NotNull(message = "El precio es obligatorio")
    private BigDecimal price;

    @NotBlank(message = "El SKU es obligatorio")
    private String sku;

    private String brand;

    @Min(value = 0, message = "El stock mínimo no puede ser negativo")
    private Integer minStock;

    @NotNull(message = "La categoría es obligatoria")
    private Categorie category;

    @NotNull(message = "El warehouseId es obligatorio")
    private Long warehouseId;

    @NotNull(message = "El supplierId es obligatorio")
    private Long supplierId;
}
