package com.fundicion.lara.commons.emuns;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Status {
    ACTIVE("A"),
    INACTIVE("I");

    private final String value;
}
