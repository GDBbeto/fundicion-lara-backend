package com.fundicion.lara.dto.request;

import com.fundicion.lara.commons.data.Pagination;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestParams {
    private String order;
    private String orderBy;
    private LocalDate startDate;
    private LocalDate endDate;
    private String type;
    private Pagination pagination;
}
