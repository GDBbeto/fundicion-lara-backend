package com.fundicion.lara.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.math.BigDecimal;

@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto implements Serializable {
    private Integer productId;
    private String name;
    private String client;
    private String description;
    private String unidad;
    private Integer stock;
    private BigDecimal purchasePrice;
    private BigDecimal sellingPrice;
    private String avatar;
}
