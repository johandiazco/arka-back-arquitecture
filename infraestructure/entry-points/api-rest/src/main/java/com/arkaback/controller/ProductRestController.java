package com.arkaback.controller;

import com.arkaback.dto.ProductCreateRequest;
import com.arkaback.dto.ProductResponse;
import com.arkaback.dto.ProductUpdateRequest;
import com.arkaback.mapper.ProductDtoMapper;
import com.arkaback.entity.Product;
import com.arkaback.ports.input.CreateProduct;
import com.arkaback.ports.input.DeleteProduct;
import com.arkaback.ports.input.ListProduct;
import com.arkaback.ports.input.UpdateProduct;
import com.arkaback.useCase.Product.GetAllProductsUseCase;
import com.arkaback.useCase.Product.GetIdProductUseCase;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
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
    private final ProductDtoMapper mapper;
    private final GetAllProductsUseCase getAllProductsUseCase;
    private final GetIdProductUseCase getIdProductUseCase;
    private final UpdateProduct updateProduct;
    private final ListProduct listProduct;
    private final DeleteProduct deleteProduct;

    //Crear Producto
    @PostMapping("/create")
    public ResponseEntity<ProductResponse> create(
            @Valid @RequestBody ProductCreateRequest request) {
        Product product = mapper.toDomain(request);
        Product created = createProduct.create(product, request.getWarehouseId(), request.getSupplierId());
        ProductResponse response = mapper.toResponse(created);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    //Listar productos
    @GetMapping("/productsAll")
    public ResponseEntity<List<ProductResponse>> getAlll() {
        List<Product> products = listProduct.getAll();
        return ResponseEntity.ok(products.stream().map(mapper::toResponse).toList());
    }

    //Obtener productor por id
    @GetMapping("/products/{id}")
    public ResponseEntity<ProductResponse> getById(@PathVariable Long id) {

        return getIdProductUseCase.getById(id)
                .map(mapper::toResponse)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    //Actualizar producto
    @PutMapping("/products/{id}")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable Long id,
            @RequestBody ProductUpdateRequest request) {

        return updateProduct.update(id, request)
                .map(mapper::toResponse)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }


    //Eliminar producto por id
    @DeleteMapping("/products/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        deleteProduct.delete(id);
        return ResponseEntity.noContent().build();
    }


}

    /*
    GET /api/inventory/low-stock - Productos con bajo stock (HU3)
    GET /api/products?category={id}&brand={name} - Búsqueda con filtros
     */

