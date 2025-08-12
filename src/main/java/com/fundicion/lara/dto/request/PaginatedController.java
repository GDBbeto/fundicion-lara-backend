package com.fundicion.lara.dto.request;

import com.fundicion.lara.commons.data.PaginationParams;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.web.bind.annotation.RequestParam;

public interface PaginatedController {
    default PaginationParams getDefaultParams(
            @Parameter(name = "page", description = "Número de página")
            @RequestParam(defaultValue = "1") int page,
            @Parameter(name = "pageSize", description = "Tamaño de página")
            @RequestParam(defaultValue = "10") int pageSize,
            @Parameter(name = "asc", description = "asc | desc")
            @RequestParam(defaultValue = "asc", required = false) String order,
            @Parameter(name = "productId", description = "campo para el ordenamiento")
            @RequestParam(defaultValue = "productId", required = false) String orderBy) {
        return new PaginationParams(page, pageSize, order, orderBy);
    }
}
