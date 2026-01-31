package com.fundicion.lara.controller;


import com.fundicion.lara.commons.data.ApiResponse;
import com.fundicion.lara.commons.data.Pagination;
import com.fundicion.lara.commons.emuns.Status;
import com.fundicion.lara.dto.ProductDto;
import com.fundicion.lara.dto.request.RequestParams;
import com.fundicion.lara.service.FileService;
import com.fundicion.lara.service.ProductExcelService;
import com.fundicion.lara.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.HttpHeaders;

import java.io.IOException;
import java.util.List;

@Tag(name = "PRODUCTS")
@RestController
@AllArgsConstructor
@RequestMapping(value = "v1/management/products")
public class ProductController {
    private ProductService productService;
    private FileService fileService;
    private ProductExcelService excelService;


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
            @RequestParam(required = false) Integer page,
            @Parameter(name = "pageSize", description = "The number of products to return per page.")
            @RequestParam(required = false) Integer pageSize,
            @Parameter(name = "order", description = "Sorting order: 'asc' for ascending or 'desc' for descending.")
            @RequestParam(defaultValue = "asc", required = false) String order,
            @Parameter(name = "orderBy", description = "The field by which to sort the products.")
            @RequestParam(defaultValue = "productId", required = false) String orderBy,
            @Parameter(name = "search", description = "search.")
            @RequestParam( required = false) String search,
            @Parameter(name = "client", description = "client.")
            @RequestParam( required = false) String client
    ) {
        Pagination pagination = Pagination.builder()
                .page(page)
                .pageSize(pageSize)
                .build();

        RequestParams requestParams = RequestParams.builder()
                .order(order)
                .orderBy(orderBy)
                .search(search)
                .client(client)
                .status(Status.ACTIVE.getValue())
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

    @PostMapping("/{productId}/upload")
    @Operation(
            operationId = "uploadImageById",
            summary = "Upload an image for a specific product",
            description = "This endpoint allows users to upload an image associated with a specific product identified by its unique product ID. The uploaded image will be processed and linked to the product."
    )
    public ApiResponse<String> uploadImageById(
            @RequestParam("file") MultipartFile file,
            @Parameter(name = "productId", description = "The unique identifier of the product")
            @PathVariable Integer productId

    ) throws IOException {
        return ApiResponse.ok(this.fileService.uploadImageById(file, productId));
    }


    @GetMapping("/clients")
    @Operation(
            operationId = "getUniqueClients",
            summary = "Retrieve unique clients in uppercase",
            description = "This endpoint retrieves a list of unique client names associated with products. " +
                    "The comparison is case-insensitive and the response is returned in uppercase format."
    )
    public ApiResponse<List<String>> getUniqueClients() {
        return ApiResponse.ok(productService.getUniqueClients());
    }

    @GetMapping("/download")
    @Operation(
            operationId = "downloadProducts",
            summary = "Download products list as an Excel file",
            description = "This endpoint allows downloading the list of products as an Excel (.xlsx) file. " +
                    "The exported file respects the same filters available in the product listing endpoint, " +
                    "including search criteria, client filtering, and sorting options. " +
                    "Pagination is disabled for this operation, and all matching products will be included in the file. " +
                    "This endpoint is intended for reporting and offline usage."
    )
    public ResponseEntity<byte[]> downloadProducts(
            @Parameter(name = "order", description = "Sorting order: 'asc' for ascending or 'desc' for descending.")
            @RequestParam(defaultValue = "asc", required = false) String order,
            @Parameter(name = "orderBy", description = "The field by which to sort the products.")
            @RequestParam(defaultValue = "productId", required = false) String orderBy,
            @Parameter(name = "search", description = "search.")
            @RequestParam( required = false) String search,
            @Parameter(name = "client", description = "client.")
            @RequestParam( required = false) String client
    ) {


        RequestParams requestParams = RequestParams.builder()
                .order(order)
                .orderBy(orderBy)
                .search(search)
                .client(client)
                .status(Status.ACTIVE.getValue())
                .build();

        byte[] excel = excelService.generateExcel(productService.findAllProducts(requestParams));

        String timestamp = java.time.LocalDateTime.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));

        String filename = "productos_fundicion_" + timestamp + ".xlsx";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=" + filename)
                .header("File-Name", filename )
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(excel);
    }
}
