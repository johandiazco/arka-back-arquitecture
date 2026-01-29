package com.arkaback.controller;

import com.arkaback.dto.Product.ProductCreateRequest;
import com.arkaback.dto.Product.ProductResponse;
import com.arkaback.dto.Product.ProductUpdateRequest;
import com.arkaback.mapper.ProductDtoMapper;
import com.arkaback.entity.product.Product;
import com.arkaback.ports.input.Product.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductRestController {

    private final CreateProduct createProduct;
    private final ListProduct listProduct;
    private final GetProductById getProductById;
    private final UpdateProduct updateProduct;
    private final DeleteProduct deleteProduct;
    private final ProductDtoMapper mapper;

    //Crear Producto
    @PostMapping("/create")
    public ResponseEntity<ProductResponse> create(@Valid @RequestBody ProductCreateRequest request) {
        Product product = mapper.toDomain(request);
        Product created = createProduct.create(product, request.getWarehouseId(), request.getSupplierId());
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(created));
    }

    //Listar productos
    @GetMapping("/productsAll")
    public ResponseEntity<List<ProductResponse>> getAll() {
        List<Product> products = listProduct.getAll();
        return ResponseEntity.ok(products.stream().map(mapper::toResponse).toList());
    }

    //Obtener producto por id
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getById(@PathVariable Long id) {
        return getProductById.getById(id)
                .map(mapper::toResponse)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    //Actualizar producto
    @PutMapping("/update/{id}")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductUpdateRequest request) {
        Product product = mapper.requestToDomain(request);
        return updateProduct.update(id, product)
                .map(mapper::toResponse)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    //Eliminar producto por id
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        deleteProduct.delete(id);
        return ResponseEntity.noContent().build();
    }
}

    /*
    GET /api/products?category={id}&brand={name} - Búsqueda con filtros
     */

