package com.fundicion.lara.service;

import com.fundicion.lara.dto.ProductDto;
import com.fundicion.lara.entity.ProductEntity;
import com.fundicion.lara.exception.NotFoundException;
import com.fundicion.lara.repository.ProductRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service("productService")
@AllArgsConstructor
public class ProductService {
    private ProductRepository productRepository;
    private ModelMapper modelMapper;

    public List<ProductDto> findAllProducts() {
        val products = this.productRepository.findAll();
        if (products.isEmpty()) {
            throw new NotFoundException("Parece que no tenemos ningún producto en este momento.");
        }
        return products.stream()
                .map(entity -> this.modelMapper.map(entity, ProductDto.class))
                .collect(Collectors.toList());
    }

    public ProductDto findProductById(Integer productId) {
        return modelMapper.map(findProductEntityById(productId), ProductDto.class);
    }

    public ProductDto saveProduct(ProductDto productDto) {
        var productEntity = this.modelMapper.map(productDto, ProductEntity.class);
        productEntity = this.productRepository.save(productEntity);
        return this.modelMapper.map(productEntity, ProductDto.class);
    }

    public ProductDto updateProduct(ProductDto productDto, Integer productId) {
        var product = findProductEntityById(productId);
        product.setName(productDto.getName());
        product.setDescription(productDto.getDescription());
        product.setUnidad(productDto.getUnidad());
        product.setStock(productDto.getStock());
        product.setPurchasePrice(productDto.getPurchasePrice());
        product.setSellingPrice(productDto.getSellingPrice());
        product.setAvatar(productDto.getAvatar());
        return this.modelMapper.map(this.productRepository.save(product), ProductDto.class);
    }

    public String deleteProduct(Integer productId) {
        val product = findProductEntityById(productId);
        this.productRepository.delete(product);
        return "OK";
    }

    private ProductEntity findProductEntityById(Integer id) {
        val productEntity = this.productRepository.findById(id);
        val message = String.format("No se pudo encontrar el producto con el ID: %s", id);
        if (productEntity.isEmpty()) {
            log.debug(message);
            throw new NotFoundException(message);
        }
        return productEntity.get();
    }
}

