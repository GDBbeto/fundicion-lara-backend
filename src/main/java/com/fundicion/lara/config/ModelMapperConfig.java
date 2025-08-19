package com.fundicion.lara.config;

import com.fundicion.lara.dto.OrderTransactionDto;
import com.fundicion.lara.entity.OrderTransactionEntity;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        PropertyMap<OrderTransactionEntity, OrderTransactionDto> orderTransactionMapToDto = new PropertyMap<>() {
            protected void configure() {
                map().setProductId(source.getProduct().getProductId());
                map().setProductName(source.getProduct().getName());
            }
        };

        PropertyMap<OrderTransactionDto, OrderTransactionEntity> orderTransactionMapToEntity = new PropertyMap<>() {
            protected void configure() {
                skip().setProduct(null);
            }
        };

        modelMapper.addMappings(orderTransactionMapToDto);
        modelMapper.addMappings(orderTransactionMapToEntity);

        return modelMapper;
    }
}
