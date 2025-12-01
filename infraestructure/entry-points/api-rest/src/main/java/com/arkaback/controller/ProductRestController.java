package com.arkaback.controller;

import com.arkaback.dto.ProductCreateRequest;
import com.arkaback.dto.ProductResponse;
import com.arkaback.mapper.ProductDtoMapper;
import com.arkaback.entity.Product;
import com.arkaback.ports.input.CreateProduct;
import com.arkaback.useCase.Product.GetAllProductsUseCase;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/products")
public class ProductRestController {

    private final CreateProduct createProductUseCase;
    private final ProductDtoMapper mapper;
    private final GetAllProductsUseCase getAllProductsUseCase;

    @PostMapping("/create")
    public ResponseEntity<ProductResponse> create(
            @Valid @RequestBody ProductCreateRequest request) {
        Product product = mapper.toDomain(request);
        Product created = createProductUseCase.execute(product, request.getWarehouseId(), request.getSupplierId());
        ProductResponse response = mapper.toResponse(created);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/productsAll")
    public ResponseEntity<List<ProductResponse>> getAll() {
        List<Product> products = getAllProductsUseCase.getAll();

        List<ProductResponse> responses = products.stream()
                .map(mapper::toResponse)
                .toList();

        return ResponseEntity.ok(responses);
    }


}
