package com.fundicion.lara.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class InvoicePatternConfig {
    private  String folioPattern;
    private  String totalPattern;
    private  int groupTotal;
}
