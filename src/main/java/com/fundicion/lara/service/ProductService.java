package com.fundicion.lara.service;

import com.fundicion.lara.commons.emuns.Status;
import com.fundicion.lara.dto.ProductDto;
import com.fundicion.lara.dto.request.RequestParams;
import com.fundicion.lara.entity.ProductEntity;
import com.fundicion.lara.exception.NotFoundException;
import com.fundicion.lara.repository.OrderTransactionRepository;
import com.fundicion.lara.repository.ProductRepository;
import com.fundicion.lara.utils.SpecificationUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service("productService")
@AllArgsConstructor
public class ProductService {
    private ProductRepository productRepository;
    private ModelMapper modelMapper;
    private OrderTransactionRepository orderTransactionRepository;

    public List<ProductDto> findAllProducts(RequestParams requestParams) {
        List<ProductEntity> productEntities = getProductEntities(requestParams);

        if (productEntities.isEmpty()) {
            throw new NotFoundException("Parece que no tenemos ningún producto en este momento.");
        }

        return productEntities.stream()
                .map(entity -> this.modelMapper.map(entity, ProductDto.class))
                .collect(Collectors.toList());
    }

    private List<ProductEntity> getProductEntities(RequestParams requestParams) {
        var pagination = requestParams.getPagination();
        Specification<ProductEntity> specification = SpecificationUtil.getSpecificationByParams(requestParams, ProductEntity.class);

        if (ObjectUtils.isEmpty(pagination.getPageSize()) && ObjectUtils.isEmpty(pagination.getPage())) {
            return this.productRepository.findAll(specification);
        }
        var sort = Sort.by(Sort.Direction.fromString(requestParams.getOrder()), requestParams.getOrderBy());
        var pageable = PageRequest.of(pagination.getNumberPage(), pagination.getPageSize(), sort);
        var response = this.productRepository.findAll(specification, pageable);

        pagination.setTotalElements(response.getTotalElements());
        return response.getContent();
    }

    public ProductDto findProductById(Integer productId) {
        return modelMapper.map(findProductEntityById(productId), ProductDto.class);
    }

    public ProductDto saveProduct(ProductDto productDto) {
        var productEntity = this.modelMapper.map(productDto, ProductEntity.class);
        productEntity.setStatus(Status.ACTIVE.getValue());
        productEntity = this.productRepository.save(productEntity);
        return this.modelMapper.map(productEntity, ProductDto.class);
    }

    public ProductDto updateProduct(ProductDto productDto, Integer productId) {
        var product = findProductEntityById(productId);
        product.setName(productDto.getName());
        product.setClient(productDto.getClient());
        product.setDescription(productDto.getDescription());
        product.setUnidad(productDto.getUnidad());
        product.setStock(productDto.getStock());
        product.setPurchasePrice(productDto.getPurchasePrice());
        product.setSellingPrice(productDto.getSellingPrice());
        product.setAvatar(productDto.getAvatar());
        return this.modelMapper.map(this.productRepository.save(product), ProductDto.class);
    }

    public String deleteProductById(Integer productId) {
        var product = findProductEntityById(productId);
        var countByProduct = this.orderTransactionRepository.countByProduct(product);
        log.info("Products encontrados: {}", countByProduct);
        if (countByProduct > 0) {
            product.setStatus(Status.INACTIVE.getValue());
            this.productRepository.save(product);
        } else {
            this.productRepository.delete(product);
        }

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

    public List<String> getUniqueClients() {
        return productRepository.findDistinctClientsUpperCase();
    }
}

