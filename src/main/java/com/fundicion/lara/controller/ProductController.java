package com.fundicion.lara.controller;


import com.fundicion.lara.commons.data.ApiResponse;
import com.fundicion.lara.commons.data.Pagination;
import com.fundicion.lara.dto.ProductDto;
import com.fundicion.lara.dto.request.RequestParams;
import com.fundicion.lara.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "PRODUCTS")
@RestController
@AllArgsConstructor
@RequestMapping(value = "v1/management/products")
public class ProductController {
    private ProductService productService;


    @GetMapping
    @Operation(
            operationId = "findAllProducts",
            description = "Gets the products by parameters",
            summary = "Gets the products by parameters"
    )
    public ApiResponse<List<ProductDto>> findAllProducts(
            @Parameter(name = "page", description = "Número de página")
            @RequestParam(defaultValue = "1") int page,
            @Parameter(name = "pageSize", description = "Tamaño de página")
            @RequestParam(defaultValue = "10") int pageSize,
            @Parameter(name = "order", description = "asc | desc")
            @RequestParam(defaultValue = "asc", required = false) String order,
            @Parameter(name = "orderBy", description = "campo para el ordenamiento")
            @RequestParam(defaultValue = "productId", required = false) String orderBy
    ) {
        Pagination pagination = Pagination.builder()
                .page(page)
                .pageSize(pageSize)
                .build();

        RequestParams requestParams = RequestParams.builder()
                .order(order)
                .orderBy(orderBy)
                .pagination(pagination)
                .build();

        return ApiResponse.ok(this.productService.findAllProducts(requestParams), pagination);
    }

    @GetMapping("/{productId}")
    @Operation(
            operationId = "findProductById",
            description = "Gets the products by ID",
            summary = "Gets the products by ID"
    )
    public ApiResponse<ProductDto> findProductById(
            @Parameter(name = "productId", description = "Product key")
            @PathVariable Integer productId
    ) {
        return ApiResponse.ok(this.productService.findProductById(productId));
    }

    @PostMapping
    @Operation(
            operationId = "saveProduct",
            description = "Save a new product in the system.",
            summary = "Save a new product in the system."
    )
    public ApiResponse<ProductDto> saveProduct(
            @RequestBody ProductDto productDto
    ) {
        return ApiResponse.ok(this.productService.saveProduct(productDto));
    }


    @PutMapping("/{productId}")
    @Operation(
            operationId = "updateProduct",
            description = "Update  product in the system..",
            summary = "Update  product in the system."
    )
    public ApiResponse<ProductDto> updateProduct(
            @RequestBody ProductDto productDto,
            @Parameter(name = "productId", description = "Product key")
            @PathVariable Integer productId
    ) {
        return ApiResponse.ok(this.productService.updateProduct(productDto, productId));
    }

    @DeleteMapping("/{productId}")
    @Operation(
            operationId = "deleteProduct",
            description = "Delete product in the system..",
            summary = "Delete product in the system."
    )
    public ApiResponse<String> deleteProduct(
            @Parameter(name = "productId", description = "Product key")
            @PathVariable Integer productId
    ) {
        return ApiResponse.ok(this.productService.deleteProductById(productId));
    }

}
