package com.fundicion.lara.controller;

import com.fundicion.lara.commons.data.ApiResponse;
import com.fundicion.lara.commons.data.Pagination;
import com.fundicion.lara.dto.TransactionDto;
import com.fundicion.lara.dto.TransactionSummaryDto;
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
            summary = "Retrieve a paginated list of transactions based on specified filters",
            description = "This endpoint retrieves a list of transactions filtered by various parameters such as date range, transaction type, and sorting options. " +
                    "You can specify the page number and page size for pagination. The transactions can be filtered by their operation date, " +
                    "and you can choose to sort the results in ascending or descending order based on a specified field. " +
                    "The transaction types can include SALE, PURCHASE, or EXPENSE. " +
                    "Ensure that the start date is earlier than the end date for valid results."
    )
    public ApiResponse<List<TransactionDto>> findAllTransactions(
            @Parameter(name = "page", description = "The page number to retrieve, starting from 1.")
            @RequestParam(defaultValue = "1") int page,
            @Parameter(name = "pageSize", description = "The number of transactions to return per page.")
            @RequestParam(defaultValue = "10") int pageSize,
            @Parameter(name = "order", description = "Sorting order: 'asc' for ascending or 'desc' for descending.")
            @RequestParam(defaultValue = "asc", required = false) String order,
            @Parameter(name = "orderBy", description = "The field by which to sort the transactions.")
            @RequestParam(defaultValue = "orderTransactionId", required = false) String orderBy,
            @Parameter(name = "startDate", description = "The start date for filtering transactions (inclusive). Format: YYYY-MM-DD")
            @RequestParam(value = "startDate")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(name = "endDate", description = "The end date for filtering transactions (inclusive). Format: YYYY-MM-DD")
            @RequestParam(value = "endDate")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @Parameter(name = "type", description = "The type of transactions to filter by: SALE, PURCHASE, or EXPENSE.")
            @RequestParam(required = false) String type
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
                .type(type)
                .pagination(pagination)
                .build();

        return ApiResponse.ok(this.transactionService.findAllTransactions(requestParams), pagination);
    }

    @GetMapping("summary")
    @Operation(
            operationId = "findTransactionSummary",
            summary = "Retrieve a summary of transactions within a specified date range",
            description = "This endpoint provides a summary of transactions that occurred between the specified start and end dates. " +
                    "The summary includes total sales, total purchases, and the difference between them, allowing for a quick overview of financial performance. " +
                    "Ensure that the start date is earlier than the end date to receive valid results. " +
                    "Both dates should be provided in the ISO format (YYYY-MM-DD)."
    )
    public ApiResponse<TransactionSummaryDto> findTransactionSummary(
            @Parameter(name = "startDate", description = "The start date for filtering transactions (inclusive). Format: YYYY-MM-DD")
            @RequestParam(value = "startDate")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(name = "endDate", description = "The end date for filtering transactions (inclusive). Format: YYYY-MM-DD")
            @RequestParam(value = "endDate")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        RequestParams requestParams = RequestParams.builder()
                .startDate(startDate)
                .endDate(endDate)
                .build();

        return ApiResponse.ok(this.transactionService.findTransactionSummary(requestParams));
    }

    @GetMapping("/{transactionId}")
    @Operation(
            operationId = "findTransactionById",
            summary = "Retrieve transaction by its unique identifier",
            description = "This endpoint retrieves the details of a specific transaction identified by its unique transaction ID. " +
                    "The transaction ID must be a valid identifier for an existing transaction in the system. " +
                    "The response will include all relevant information about the transaction, such as amount, date, type, and status. " +
                    "If the transaction ID does not exist, an appropriate error response will be returned."
    )
    public ApiResponse<TransactionDto> findTransactionById(
            @Parameter(name = "transactionId", description = "The unique identifier of the transaction")
            @PathVariable Long transactionId
    ) {
        return ApiResponse.ok(this.transactionService.findTransactionById(transactionId));
    }

    @PostMapping
    @Operation(
            operationId = "saveTransaction",
            summary = "Create a new financial transaction",
            description = "Endpoint to record a new financial transaction in the system. " +
                    "The request should include all required transaction details including amount, type (SALE/PURCHASE/EXPENSE), " +
                    "description, and operation date. The system will validate the transaction data before saving and return " +
                    "the complete saved transaction with generated identifiers (transactionId). " +
                    "Required fields: amount, type, operationDate. Status defaults to 'A' (Active) if not provided."
    )
    public ApiResponse<TransactionDto> saveTransaction(
            @RequestBody TransactionDto transactionDto
    ) {
        return ApiResponse.ok(this.transactionService.saveTransaction(transactionDto));
    }


    @PutMapping("/{transactionId}")
    @Operation(
            operationId = "updateTransaction",
            summary = "Update an existing financial transaction",
            description = "This endpoint allows for the modification of an existing financial transaction identified by its unique transaction ID. " +
                    "The request must include a TransactionDto object containing the updated details, such as amount, type (SALE/PURCHASE/EXPENSE), " +
                    "description, and operation date. The system will validate the provided data before applying the changes. " +
                    "If the transaction ID does not correspond to an existing transaction, an error response will be returned. " +
                    "Only the fields included in the request will be updated, while other fields will remain unchanged."
    )
    public ApiResponse<TransactionDto> updateTransaction(
            @RequestBody TransactionDto transactionDto,
            @Parameter(name = "transactionId", description = "The unique identifier of the transaction")
            @PathVariable Long transactionId
    ) {
        return ApiResponse.ok(this.transactionService.updateTransactionById(transactionDto, transactionId));
    }

    @DeleteMapping("/{transactionId}")
    @Operation(
            operationId = "deleteTransactionById",
            summary = "Delete a financial transaction by its unique identifier",
            description = "This endpoint allows for the deletion of a financial transaction identified by its unique transaction ID. " +
                    "Upon successful deletion, a confirmation message will be returned. " +
                    "If the specified transaction ID does not exist, an error response will be provided. " +
                    "This operation is irreversible, and the transaction data will be permanently removed from the system."
    )
    public ApiResponse<String> deleteTransactionById(
            @Parameter(name = "transactionId", description = "The unique identifier of the transaction")
            @PathVariable Long transactionId
    ) {
        return ApiResponse.ok(this.transactionService.deleteTransactionById(transactionId));
    }

}
