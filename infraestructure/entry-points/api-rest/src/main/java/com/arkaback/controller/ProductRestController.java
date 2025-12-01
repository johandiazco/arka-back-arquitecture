package com.arkaback.controller;

import com.arkaback.dto.ProductCreateRequest;
import com.arkaback.dto.ProductResponse;
import com.arkaback.mapper.ProductDtoMapper;
import com.arkaback.entity.Product;
import com.arkaback.ports.input.CreateProduct;
import com.arkaback.useCase.Product.GetAllProductsUseCase;
import com.arkaback.useCase.Product.GetIdProductUseCase;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@AllArgsConstructor
@RequestMapping("/api/products")
public class ProductRestController {

    private final CreateProduct createProduct;
    private final ProductDtoMapper mapper;
    private final GetAllProductsUseCase getAllProductsUseCase;
    private final GetIdProductUseCase getIdProductUseCase;

    //Crear Producto
    @PostMapping("/create")
    public ResponseEntity<ProductResponse> create(
            @Valid @RequestBody ProductCreateRequest request) {
        Product product = mapper.toDomain(request);
        Product created = createProduct.execute(product, request.getWarehouseId(), request.getSupplierId());
        ProductResponse response = mapper.toResponse(created);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    //Listar productos
    @GetMapping("/productsAll")
    public ResponseEntity<List<ProductResponse>> getAll() {
        List<Product> products = getAllProductsUseCase.getAll();

        List<ProductResponse> responses = products.stream()
                .map(mapper::toResponse)
                .toList();

        return ResponseEntity.ok(responses);
    }

    //Obtener productor por id
    @GetMapping("/products/{id}")
    public ResponseEntity<ProductResponse> getById(@PathVariable Long id) {

        return getIdProductUseCase.getById(id)
                .map(mapper::toResponse)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

}

    /*
    GET /api/products - Listar productos
    GET /api/products/{id} - Obtener producto por ID
    PUT /api/products/{id} - Actualizar producto
    DELETE /api/products/{id} - Eliminar/desactivar producto
    GET /api/inventory/low-stock - Productos con bajo stock (HU3)
    GET /api/products?category={id}&brand={name} - Búsqueda con filtros
     */

