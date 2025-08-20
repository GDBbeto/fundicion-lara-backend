package com.fundicion.lara.controller;

import com.fundicion.lara.commons.data.ApiResponse;
import com.fundicion.lara.commons.data.Pagination;
import com.fundicion.lara.dto.TransactionDto;
import com.fundicion.lara.dto.request.RequestParams;
import com.fundicion.lara.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "TRANSACTION")
@RestController
@AllArgsConstructor
@RequestMapping(value = "v1/management/transaction")
public class TransactionController {
    private TransactionService transactionService;

    @GetMapping
    @Operation(
            operationId = "findAllTransactions",
            description = "Gets transactions by parameters",
            summary = "Gets  transactions by parameters"
    )
    public ApiResponse<List<TransactionDto>> findAllTransactions(
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

        return ApiResponse.ok(this.transactionService.findAllTransactions(requestParams), pagination);
    }

    @GetMapping("/{transactionId}")
    @Operation(
            operationId = "findTransactionById",
            description = "Gets the payment transactions by ID",
            summary = "Gets the payment transactions by ID"
    )
    public ApiResponse<TransactionDto> findTransactionById(
            @Parameter(name = "transactionId", description = "transaction key")
            @PathVariable Long transactionId
    ) {
        return ApiResponse.ok(this.transactionService.findTransactionById(transactionId));
    }

    @PostMapping
    @Operation(
            operationId = "saveTransaction",
            description = "Save a new Transaction in the system.",
            summary = "Save a new Transaction in the system."
    )
    public ApiResponse<TransactionDto> saveTransaction(
            @RequestBody TransactionDto transactionDto
    ) {
        return ApiResponse.ok(this.transactionService.saveTransaction(transactionDto));
    }


    @PutMapping("/{transactionId}")
    @Operation(
            operationId = "updateTransaction",
            description = "Update a new Transaction in the system.",
            summary = "Update a new Transaction in the system."
    )
    public ApiResponse<TransactionDto> updateTransaction(
            @RequestBody TransactionDto transactionDto,
            @Parameter(name = "transactionId", description = "Order transaction key")
            @PathVariable Long transactionId
    ) {
        return ApiResponse.ok(this.transactionService.updateTransactionById(transactionDto, transactionId));
    }

    @DeleteMapping("/{transactionId}")
    @Operation(
            operationId = "deleteTransactionById",
            description = "Delete Transaction in the system.",
            summary = "Delete Transaction in the system."
    )
    public ApiResponse<String> deleteTransactionById(
            @Parameter(name = "transactionId", description = "Order transaction key")
            @PathVariable Long transactionId
    ) {
        return ApiResponse.ok(this.transactionService.deleteTransactionById(transactionId));
    }

}
