package com.fundicion.lara.service;

import com.fundicion.lara.dto.ProductDto;
import com.fundicion.lara.dto.request.RequestParams;
import com.fundicion.lara.entity.ProductEntity;
import com.fundicion.lara.exception.BadRequestException;
import com.fundicion.lara.exception.ConflictException;
import com.fundicion.lara.exception.NotFoundException;
import com.fundicion.lara.repository.ProductRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service("productService")
@AllArgsConstructor
public class ProductService {
    private ProductRepository productRepository;
    private ModelMapper modelMapper;

    public List<ProductDto> findAllProducts(RequestParams requestParams) {
        var pagination = requestParams.getPagination();
        var sort = Sort.by(Sort.Direction.fromString(requestParams.getOrder()), requestParams.getOrderBy());
        var pageable = PageRequest.of(pagination.getNumberPage(), pagination.getPageSize(), sort);

        val response = this.productRepository.findAll(pageable);
        if (response.isEmpty()) {
            throw new NotFoundException("Parece que no tenemos ningún producto en este momento.");
        }
        pagination.setTotalElements(response.getTotalElements());

        return response.getContent().stream()
                .map(entity -> this.modelMapper.map(entity, ProductDto.class))
                .collect(Collectors.toList());
    }

    public ProductDto findProductById(Integer productId) {
        return modelMapper.map(findProductEntityById(productId), ProductDto.class);
    }

    public ProductDto saveProduct(ProductDto productDto) {
        var productByName = this.productRepository.findProductEntitiesByName(productDto.getName());
        if (productByName.isPresent()) {
            throw new ConflictException("El producto ya existe");
        }
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

    public String deleteProductById(Integer productId) {
        val product = findProductEntityById(productId);
        this.productRepository.delete(product);
        return "OK";
    }

    public ProductEntity findProductEntityById(Integer id) {
        val productEntity = this.productRepository.findById(id);
        val message = String.format("No se pudo encontrar el producto con el ID: %s", id);
        if (productEntity.isEmpty()) {
            log.debug(message);
            throw new NotFoundException(message);
        }
        return productEntity.get();
    }
}

