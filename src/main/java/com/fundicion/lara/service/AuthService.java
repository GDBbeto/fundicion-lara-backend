package com.fundicion.lara.service;

import com.fundicion.lara.commons.emuns.Role;
import com.fundicion.lara.dto.UserDto;
import com.fundicion.lara.dto.request.UserRequest;
import com.fundicion.lara.entity.UserEntity;
import com.fundicion.lara.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.val;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@AllArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserDto register(UserRequest userRequest) {
        if (userRepository.existsByEmail(userRequest.getEmail())) {
            throw new RuntimeException("El correo ya está registrado.");
        }

        val encodedPassword = passwordEncoder.encode(userRequest.getPassword());

        val userEntity = UserEntity.builder()
                .name(userRequest.getName())
                .lastName(userRequest.getLastName())
                .motherLastName(userRequest.getMotherLastName())
                .email(userRequest.getEmail())
                .password(encodedPassword)
                .role(Role.PENDING)
                .build();

        val saved = userRepository.save(userEntity);
        return modelMapper.map(saved, UserDto.class);
    }
}
