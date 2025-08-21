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
            summary = "Retrieve a paginated list of products based on specified filters",
            description = "This endpoint retrieves a list of products from the system, allowing for pagination and sorting based on specified parameters. " +
                    "You can specify the page number and the number of products per page for efficient data retrieval. " +
                    "The results can be sorted in ascending or descending order based on a specified field, such as product ID or any other relevant attribute. " +
                    "If no sorting field is provided, the default sorting will be applied. " +
                    "This endpoint is useful for displaying products in a user-friendly manner, especially in applications with large inventories."
    )
    public ApiResponse<List<ProductDto>> findAllProducts(
            @Parameter(name = "page", description = "The page number to retrieve, starting from 1.")
            @RequestParam(defaultValue = "1") int page,
            @Parameter(name = "pageSize", description = "The number of products to return per page.")
            @RequestParam(defaultValue = "10") int pageSize,
            @Parameter(name = "order", description = "Sorting order: 'asc' for ascending or 'desc' for descending.")
            @RequestParam(defaultValue = "asc", required = false) String order,
            @Parameter(name = "orderBy", description = "The field by which to sort the products.")
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
            summary = "Retrieve a product by its unique identifier",
            description = "This endpoint retrieves the details of a specific product identified by its unique product ID. " +
                    "The product ID must correspond to an existing product in the system. " +
                    "The response will include all relevant information about the product, such as name, description, price, and stock status. " +
                    "If the specified product ID does not exist, an appropriate error response will be returned, indicating that the product was not found."
    )
    public ApiResponse<ProductDto> findProductById(
            @Parameter(name = "productId", description = "The unique identifier of the product")
            @PathVariable Integer productId
    ) {
        return ApiResponse.ok(this.productService.findProductById(productId));
    }

    @PostMapping
    @Operation(
            operationId = "saveProduct",
            summary = "Create a new product in the system",
            description = "This endpoint allows for the creation of a new product in the system. " +
                    "The request must include a ProductDto object containing all necessary details, such as name, description, price, and stock quantity. " +
                    "Upon successful creation, the system will return the complete details of the newly created product, including its unique identifier. " +
                    "If any required fields are missing or invalid, an error response will be returned, indicating the specific validation issues."
    )
    public ApiResponse<ProductDto> saveProduct(
            @RequestBody ProductDto productDto
    ) {
        return ApiResponse.ok(this.productService.saveProduct(productDto));
    }


    @PutMapping("/{productId}")
    @Operation(
            operationId = "updateProduct",
            summary = "Update an existing product in the system",
            description = "This endpoint allows for the modification of an existing product identified by its unique product ID. " +
                    "The request must include a ProductDto object containing the updated details, such as name, description, price, and stock quantity. " +
                    "Only the fields provided in the request will be updated, while other fields will remain unchanged. " +
                    "If the specified product ID does not correspond to an existing product, an error response will be returned, indicating that the product was not found. " +
                    "Additionally, if any required fields are missing or invalid, a validation error will be returned."
    )
    public ApiResponse<ProductDto> updateProduct(
            @RequestBody ProductDto productDto,
            @Parameter(name = "productId", description = "The unique identifier of the product")
            @PathVariable Integer productId
    ) {
        return ApiResponse.ok(this.productService.updateProduct(productDto, productId));
    }

    @DeleteMapping("/{productId}")
    @Operation(
            operationId = "deleteProduct",
            description = "Removes a product from the system using its unique identifier.",
            summary = "Product deletion."
    )
    public ApiResponse<String> deleteProduct(
            @Parameter(name = "productId", description = "The unique identifier of the product")
            @PathVariable Integer productId
    ) {
        return ApiResponse.ok(this.productService.deleteProductById(productId));
    }

}
