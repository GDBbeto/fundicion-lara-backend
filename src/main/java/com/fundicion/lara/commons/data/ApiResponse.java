package com.fundicion.lara.commons.data;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class ApiResponse<T> implements Serializable {

    @Schema(description = "Estatus de la respuesta", allowableValues = {"Success", "Error"})
    private String status;

    @Schema(description = "Mensaje genérico de respuesta")
    private String message;

    @Schema(description = "Objeto de paginación")
    private Pagination pagination;

    @Schema(description = "Respuesta del servicio")
    private T data;

    public static <T> ApiResponse<T> ok(T result) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setData(result);
        response.setStatus("success");
        response.setMessage("ok");
        return response;
    }

    public static <T> ApiResponse<List<T>> ok(Page<T> pageResult, Pagination pagination) {
        ApiResponse<List<T>> response = new ApiResponse<>();
        pagination.setTotalElements(pageResult.getTotalElements());
        response.setData(pageResult.getContent());
        response.setStatus("success");
        response.setMessage("ok");
        response.setPagination(pagination);
        return response;
    }

    public static <T> ApiResponse<List<T>> ok(List<T> pageResult, Pagination pagination) {
        ApiResponse<List<T>> response = new ApiResponse<>();
        response.setData(pageResult);
        response.setStatus("success");
        response.setMessage("ok");
        response.setPagination(pagination);
        return response;
    }

    public static <T> ApiResponse<T> ok() {
        ApiResponse<T> response = new ApiResponse<>();
        response.setStatus("success");
        response.setMessage("ok");
        return response;
    }

}
