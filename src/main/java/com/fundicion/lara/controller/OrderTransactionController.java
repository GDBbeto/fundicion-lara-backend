package com.fundicion.lara.controller;

import com.fundicion.lara.commons.data.ApiResponse;
import com.fundicion.lara.commons.data.Pagination;
import com.fundicion.lara.dto.OrderTransactionDto;
import com.fundicion.lara.dto.request.RequestParams;
import com.fundicion.lara.service.OrderTransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "ORDER-TRANSACTION")
@RestController
@AllArgsConstructor
@RequestMapping(value = "v1/management/order/transaction")
public class OrderTransactionController {
    private OrderTransactionService orderTransactionService;

    @GetMapping
    @Operation(
            operationId = "findAllPaymentTransactions",
            description = "Gets the payment transactions by parameters",
            summary = "Gets the payment transactions by parameters"
    )
    public ApiResponse<List<OrderTransactionDto>> findAllPaymentTransactions(
            @Parameter(name = "page", description = "Número de página")
            @RequestParam(defaultValue = "1") int page,
            @Parameter(name = "pageSize", description = "Tamaño de página")
            @RequestParam(defaultValue = "10") int pageSize,
            @Parameter(name = "order", description = "asc | desc")
            @RequestParam(defaultValue = "asc", required = false) String order,
            @Parameter(name = "orderBy", description = "campo para el ordenamiento")
            @RequestParam(defaultValue = "orderTransactionId", required = false) String orderBy,
            @Parameter(name = "startDate", description = "Fecha inicio")
            @RequestParam(value = "startDate")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(name = "endDate", description = "Fecha fin")
            @RequestParam(value = "endDate")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
            ) {
        Pagination pagination = Pagination.builder()
                .page(page)
                .pageSize(pageSize)
                .build();

        RequestParams requestParams = RequestParams.builder()
                .order(order)
                .orderBy(orderBy)
                .startDate(startDate)
                .endDate(endDate)
                .pagination(pagination)
                .build();

        return ApiResponse.ok(this.orderTransactionService.findAllPaymentTransactions(requestParams), pagination);
    }

    @GetMapping("/{orderTransactionId}")
    @Operation(
            operationId = "findPaymentTransactionById",
            description = "Gets the payment transactions by ID",
            summary = "Gets the payment transactions by ID"
    )
    public ApiResponse<OrderTransactionDto> findPaymentTransactionById(
            @Parameter(name = "orderTransactionId", description = "Order transaction key")
            @PathVariable Integer orderTransactionId
    ) {
        return ApiResponse.ok(this.orderTransactionService.findPaymentTransactionById(orderTransactionId));
    }

    @PostMapping
    @Operation(
            operationId = "savePaymentTransaction",
            description = "Save a new Payment Transaction in the system.",
            summary = "Save a new Payment Transaction in the system."
    )
    public ApiResponse<OrderTransactionDto> savePaymentTransaction(
            @RequestBody OrderTransactionDto orderTransactionDto
    ) {
        return ApiResponse.ok(this.orderTransactionService.savePaymentTransaction(orderTransactionDto));
    }

    @PutMapping("/{orderTransactionId}")
    @Operation(
            operationId = "updatePaymentTransaction",
            description = "Update a new Payment Transaction in the system.",
            summary = "Update a new Payment Transaction in the system."
    )
    public ApiResponse<OrderTransactionDto>  updatePaymentTransaction(
            @RequestBody OrderTransactionDto orderTransactionDto,
            @Parameter(name = "orderTransactionId", description = "Order transaction key")
            @PathVariable Integer orderTransactionId
    ) {
        return ApiResponse.ok(this.orderTransactionService.updatePaymentTransaction(orderTransactionDto,  orderTransactionId));
    }

    @DeleteMapping("/{orderTransactionId}")
    @Operation(
            operationId = "deletePaymentTransactionById",
            description = "Delete a new Payment Transaction in the system.",
            summary = "Delete a new Payment Transaction in the system."
    )
    public ApiResponse<String> deletePaymentTransactionById(
            @Parameter(name = "orderTransactionId", description = "Order transaction key")
            @PathVariable Integer orderTransactionId
    ) {
        return ApiResponse.ok(this.orderTransactionService.deleteTransactionById(orderTransactionId));
    }


}
