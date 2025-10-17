package com.fundicion.lara.commons.emuns;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Role {
    ADMIN("ADMIN"),
    OPERATOR("OPERATOR"),
    PENDING("PENDING"),
    VIEWER("VIEWER");

    private final String value;
}
