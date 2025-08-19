package com.fundicion.lara.commons.emuns;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MethodPayment {
    CREDIT_CARD("CREDIT_CARD"),
    DEBIT_CARD("DEBIT_CARD"),
    PAYPAL("PAYPAL"),
    BANK_TRANSFER("BANK_TRANSFER"),
    CASH("CASH");
    private final String displayName;
}
