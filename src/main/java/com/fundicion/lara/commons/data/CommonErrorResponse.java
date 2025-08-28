package com.fundicion.lara.commons.data;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class CommonErrorResponse implements Serializable {
    private String userMessage;
    private String message;
}
