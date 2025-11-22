package com.arkaback.controller;

import com.arkaback.dto.ProductCreateRequest;
import com.arkaback.dto.ProductResponse;
import com.arkaback.mapper.ProductDtoMapper;
import com.arkaback.entity.Product;
import com.arkaback.ports.in.CreateProductUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products")
public class ProductRestController {

    private final CreateProductUseCase createProductUseCase;
    private final ProductDtoMapper mapper;

    public ProductRestController(CreateProductUseCase createProductUseCase,
                                 ProductDtoMapper mapper) {
        this.createProductUseCase = createProductUseCase;
        this.mapper = mapper;
    }

    @PostMapping
    public ResponseEntity<ProductResponse> create(@Valid @RequestBody ProductCreateRequest request) {
        Product product = mapper.toDomain(request);
        Product created = createProductUseCase.execute(product, request.getWarehouseId(), request.getSupplierId());
        ProductResponse response = mapper.toResponse(created);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
