package com.fundicion.lara.commons.emuns;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PaymentStatus {
    PAID("PAID"),         // Represents "PAGADO"
    PENDING("PENDING"),   // Represents "PENDIENTE"
    INCOMPLETE("INCOMPLETE"); // Represents "INCOMPLETO"
    private final String status;
}
