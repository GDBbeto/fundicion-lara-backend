package com.fundicion.lara.commons.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Pagination {
    private Integer page;

    private Integer pageSize;

    private Long totalElements;

    private Integer totalPages;

    public void setTotalElements(Long totalElements) {
        this.totalElements = totalElements;
        this.totalPages = (int) Math.ceil((double) totalElements / pageSize);
    }

    @JsonIgnore
    public Integer getNumberPage() {
        return this.page > 0 ? (this.page - 1) : 0;
    }
}
