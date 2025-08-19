package com.fundicion.lara.commons.emuns;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fundicion.lara.exception.BadRequestException;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DeliveryStatus {
    PENDING("PENDING"),         // Represents "PENDIENTE"
    IN_TRANSIT("IN_TRANSIT"),   // Represents "EN TRANCITO"
    DELIVERED("DELIVERED"),     // Represents "ENTREGADO"
    CANCELLED("CANCELLED"),     // Represents "CANCELADO"
    ON_HOLD("ON_HOLD");         // Represents "EN ESPERA"
    private final String status;
}
