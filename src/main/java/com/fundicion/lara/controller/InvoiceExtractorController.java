package com.fundicion.lara.controller;

import com.fundicion.lara.commons.data.ApiResponse;
import com.fundicion.lara.dto.InvoiceDataDto;
import com.fundicion.lara.service.InvoiceExtractorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "INVOICE-EXTRACT")
@RestController
@AllArgsConstructor
@RequestMapping(value = "v1/management/invoices")
public class InvoiceExtractorController {
    private InvoiceExtractorService invoiceExtractorService;

    @PostMapping("/extract")
    @Operation(
            operationId = "getExtractInvoiceData",
            summary = "Extract key information from an invoice PDF file",
            description = "This endpoint processes a PDF invoice file and extracts essential financial information including: " +
                    "• Fiscal folio (UUID) of the invoice\n" +
                    "• Total amount of the transaction\n" +
                    "• Product/service description\n\n" +
                    "The uploaded file must be a valid PDF containing invoice data. " +
                    "The extraction uses pattern recognition to identify key fields in the invoice text."

    )
    public ApiResponse<InvoiceDataDto> getExtractInvoiceData(@RequestParam("file") MultipartFile file) {
        return ApiResponse.ok(this.invoiceExtractorService.findExtractInvoiceData(file));
    }
}
