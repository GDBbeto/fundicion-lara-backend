package com.fundicion.lara.commons.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PaginationParams {
    private int page;
    private int pageSize;
    private String order;
    private String orderBy;
}
