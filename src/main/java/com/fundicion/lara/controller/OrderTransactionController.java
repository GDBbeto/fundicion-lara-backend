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
            summary = "Retrieve a paginated list of payment transactions based on specified filters",
            description = "This endpoint retrieves a list of payment transactions filtered by various parameters such as date range, transaction type, and sorting options. " +
                    "You can specify the page number and page size for pagination. The transactions can be filtered by their operation date, " +
                    "and you can choose to sort the results in ascending or descending order based on a specified field. " +
                    "Ensure that the start date is earlier than the end date for valid results."
    )
    public ApiResponse<List<OrderTransactionDto>> findAllPaymentTransactions(
            @Parameter(name = "page", description = "The page number to retrieve, starting from 1.")
            @RequestParam(defaultValue = "1") int page,
            @Parameter(name = "pageSize", description = "The number of transactions to return per page.")
            @RequestParam(defaultValue = "10") int pageSize,
            @Parameter(name = "order", description = "Sorting order: 'asc' for ascending or 'desc' for descending.")
            @RequestParam(defaultValue = "asc", required = false) String order,
            @Parameter(name = "orderBy", description = "The field by which to sort the payment transactions.")
            @RequestParam(defaultValue = "transactionId", required = false) String orderBy,
            @Parameter(name = "startDate", description = "The start date for filtering transactions (inclusive). Format: YYYY-MM-DD")
            @RequestParam(value = "startDate")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(name = "endDate", description = "The end date for filtering transactions (inclusive). Format: YYYY-MM-DD")
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
            summary = "Retrieve payment transaction by its unique identifier",
            description = "This endpoint retrieves the details of a specific payment transaction identified by its unique transaction ID. " +
                    "The payment transaction ID must be a valid identifier for an existing payment transaction in the system. " +
                    "The response will include all relevant information about the payment transaction, such as amount, date, and status. " +
                    "If the transaction ID does not exist, an appropriate error response will be returned."
    )
    public ApiResponse<OrderTransactionDto> findPaymentTransactionById(
            @Parameter(name = "orderTransactionId", description = "The unique identifier of the payment transaction")
            @PathVariable Integer orderTransactionId
    ) {
        return ApiResponse.ok(this.orderTransactionService.findPaymentTransactionById(orderTransactionId));
    }

    @PostMapping
    @Operation(
            operationId = "savePaymentTransaction",
            summary = "Create a new Payment transaction",
            description = "Endpoint to record a new Payment transaction in the system. " +
                    "The system will validate the transaction data before saving and return " +
                    "the complete saved transaction with generated identifiers (transactionId). "
    )
    public ApiResponse<OrderTransactionDto> savePaymentTransaction(
            @RequestBody OrderTransactionDto orderTransactionDto
    ) {
        return ApiResponse.ok(this.orderTransactionService.savePaymentTransaction(orderTransactionDto));
    }

    @PutMapping("/{orderTransactionId}")
    @Operation(
            operationId = "updatePaymentTransaction",
            summary = "Update a  Payment transaction",
            description = "Endpoint to record a Update Payment transaction in the system. " +
                    "The system will validate the transaction data before saving and return "
    )
    public ApiResponse<OrderTransactionDto> updatePaymentTransaction(
            @RequestBody OrderTransactionDto orderTransactionDto,
            @Parameter(name = "orderTransactionId", description = "The unique identifier of the payment transaction")
            @PathVariable Integer orderTransactionId
    ) {
        return ApiResponse.ok(this.orderTransactionService.updatePaymentTransaction(orderTransactionDto, orderTransactionId));
    }

    @DeleteMapping("/{orderTransactionId}")
    @Operation(
            operationId = "deletePaymentTransactionById",
            summary = "Delete a Payment transaction by its unique identifier",
            description = "This endpoint allows for the deletion of a Payment transaction identified by its unique transaction ID. " +
                    "Upon successful deletion, a confirmation message will be returned. " +
                    "If the specified transaction ID does not exist, an error response will be provided. " +
                    "This operation is irreversible, and the transaction data will be permanently removed from the system."
    )
    public ApiResponse<String> deletePaymentTransactionById(
            @Parameter(name = "orderTransactionId", description = "The unique identifier of the payment transaction")
            @PathVariable Integer orderTransactionId
    ) {
        return ApiResponse.ok(this.orderTransactionService.deleteTransactionById(orderTransactionId));
    }


}
