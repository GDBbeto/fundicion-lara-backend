package com.fundicion.lara.dto;

import com.fundicion.lara.commons.emuns.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto implements Serializable {
    private Long userId;
    private String name;
    private String lastName;
    private String motherLastName;
    private String email;
    private Role role;
}