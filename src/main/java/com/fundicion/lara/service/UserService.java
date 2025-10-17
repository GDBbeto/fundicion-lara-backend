package com.fundicion.lara.service;

import com.fundicion.lara.commons.emuns.Role;
import com.fundicion.lara.dto.UserDto;
import com.fundicion.lara.dto.request.RequestParams;
import com.fundicion.lara.dto.request.UserRequest;
import com.fundicion.lara.entity.UserEntity;
import com.fundicion.lara.exception.NotFoundException;
import com.fundicion.lara.repository.UserRepository;
import com.fundicion.lara.utils.SpecificationUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final BCryptPasswordEncoder passwordEncoder;

    public List<UserDto> findAllUsers(RequestParams requestParams) {
        var pagination = requestParams.getPagination();
        var sort = Sort.by(Sort.Direction.fromString(requestParams.getOrder()), requestParams.getOrderBy());

        var specification = SpecificationUtil.getSpecificationByParams(requestParams, UserEntity.class);
        var pageable = PageRequest.of(pagination.getNumberPage(), pagination.getPageSize(), sort);

        val response = this.userRepository.findAll(specification, pageable);
        if (response.isEmpty()) {
            log.info("No users found {}", requestParams);
            throw new NotFoundException("No se encontraron registros que coincidan.");
        }

        pagination.setTotalElements(response.getTotalElements());

        return response.getContent().stream()
                .map(user -> modelMapper.map(user, UserDto.class))
                .toList();
    }

    public UserDto findUserById(Long userId) {
        return modelMapper.map(findEntityById(userId), UserDto.class);
    }

    public UserDto findUserByEmail(String email) {
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado con email: " + email));
        return modelMapper.map(user, UserDto.class);
    }

    public UserDto updateUser(Long userId, UserRequest userDto) {
        var userEntity = findEntityById(userId);
        userEntity.setName(userDto.getName());
        userEntity.setLastName(userDto.getLastName());
        userEntity.setMotherLastName(userDto.getMotherLastName());
        userEntity.setRole(userDto.getRole());
        return modelMapper.map(userRepository.save(userEntity), UserDto.class);
    }

    @Transactional
    public void deleteUser(Long userId) {
        var userEntity = findEntityById(userId);
        userRepository.delete(userEntity);
    }

    private UserEntity findEntityById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    var msg = "Usuario no encontrado con ID: " + userId;
                    log.debug(msg);
                    return new NotFoundException(msg);
                });
    }
}
