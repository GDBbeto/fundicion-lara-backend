package com.fundicion.lara.service;

import com.fundicion.lara.commons.emuns.DeliveryStatus;
import com.fundicion.lara.commons.emuns.PaymentStatus;
import com.fundicion.lara.dto.OrderTransactionDto;
import com.fundicion.lara.dto.request.RequestParams;
import com.fundicion.lara.entity.OrderTransactionEntity;
import com.fundicion.lara.entity.ProductEntity;
import com.fundicion.lara.exception.ConflictException;
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
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class OrderTransactionService {
    private OrderTransactionRepository transactionRepository;
    private ProductService productService;
    private ProductRepository productRepository;
    private ModelMapper modelMapper;

    public List<OrderTransactionDto> findAllPaymentTransactions(RequestParams requestParams) {
        var pagination = requestParams.getPagination();
        var sort = Sort.by(Sort.Direction.fromString(requestParams.getOrder()), requestParams.getOrderBy());

        var specification = SpecificationUtil.getSpecificationByParams(requestParams, OrderTransactionEntity.class);
        var pageable = PageRequest.of(pagination.getNumberPage(), pagination.getPageSize(), sort);

        val response = this.transactionRepository.findAll(specification, pageable);
        if (response.isEmpty()) {
            throw new NotFoundException("No se encontraron registros que coincidan.");
        }

        pagination.setTotalElements(response.getTotalElements());

        return response.getContent().stream()
                .map(this::mapEntityToDto)
                .toList();
    }


    public OrderTransactionDto findPaymentTransactionById(Integer orderTransactionId) {
        return this.mapEntityToDto(this.findPaymentTransactionEntityEntityById(orderTransactionId));
    }

    public OrderTransactionDto savePaymentTransaction(OrderTransactionDto orderTransactionDto) {
        var orderTransactionEntity = this.mapDtoToEntity(orderTransactionDto);

        val product = orderTransactionEntity.getProduct();

        if (orderTransactionEntity.getItemCount() > product.getStock()) {
            throw new ConflictException("Las unidades excede el stock disponible del producto.");
        }

        product.setStock(product.getStock() - orderTransactionEntity.getItemCount());
        this.productRepository.save(product);

        BigDecimal total = product.getSellingPrice().multiply(BigDecimal.valueOf(orderTransactionDto.getItemCount()));
        this.processPayment(orderTransactionEntity, total);
        orderTransactionEntity.setSellingPrice(product.getSellingPrice());
        orderTransactionEntity.setPurchasePrice(product.getPurchasePrice());
        if (orderTransactionDto.getDeliveryStatus() == null) {
            orderTransactionEntity.setDeliveryStatus(DeliveryStatus.PENDING);
        }

        orderTransactionEntity = this.transactionRepository.save(orderTransactionEntity);
        return this.mapEntityToDto(orderTransactionEntity);
    }

    public OrderTransactionDto updatePaymentTransaction(OrderTransactionDto orderTransactionDto, Integer orderTransactionId) {
        var orderTransactionEntity = this.findPaymentTransactionEntityEntityById(orderTransactionId);
        var orderTransactionEntityCopy = modelMapper.map(orderTransactionEntity, OrderTransactionEntity.class);

        if (isStatusCancelled(orderTransactionDto, orderTransactionEntityCopy)) {
            orderTransactionEntity.setPaymentStatus(orderTransactionDto.getPaymentStatus());
            this.transactionRepository.save(orderTransactionEntity);
            return this.mapEntityToDto(orderTransactionEntity);
        }

        validateProduct(orderTransactionDto, orderTransactionEntityCopy);

        boolean productChanged = !orderTransactionDto.getProductId().equals(orderTransactionEntity.getProduct().getProductId());

        if(productChanged){
            orderTransactionEntity.setSellingPrice(orderTransactionEntity.getProduct().getSellingPrice());
            orderTransactionEntity.setPurchasePrice(orderTransactionEntity.getProduct().getPurchasePrice());
        }

        // var product = productService.findProductEntityById(orderTransactionDto.getProductId());
        orderTransactionEntity.setProduct(orderTransactionEntity.getProduct());
        orderTransactionEntity.setItemCount(orderTransactionDto.getItemCount());
        orderTransactionEntity.setExtraAmount(orderTransactionDto.getExtraAmount());
        orderTransactionEntity.setDescription(orderTransactionDto.getDescription());
        orderTransactionEntity.setMethodPayment(orderTransactionDto.getMethodPayment());
        orderTransactionEntity.setInvoiceNumber(String.valueOf(orderTransactionDto.getInvoiceNumber()));
        orderTransactionEntity.setClient(String.valueOf(orderTransactionDto.getClient()));
        orderTransactionEntity.setAmountPaid(orderTransactionDto.getAmountPaid());

        BigDecimal total = orderTransactionEntity.getSellingPrice().multiply(BigDecimal.valueOf(orderTransactionEntity.getItemCount()));
        this.processPayment(orderTransactionEntity, total);

        orderTransactionEntity.setDeliveryStatus(orderTransactionDto.getDeliveryStatus());
        orderTransactionEntity.setOperationDate(orderTransactionDto.getOperationDate());

        this.transactionRepository.save(orderTransactionEntity);

        return this.mapEntityToDto(orderTransactionEntity);
    }


    public String deleteTransactionById(Integer id) {
        val paymentTransaction = this.findPaymentTransactionEntityEntityById(id);
        val statusCompleted = DeliveryStatus.CANCELLED.getStatus().equals(paymentTransaction.getDeliveryStatus().getStatus()) ||
                DeliveryStatus.DELIVERED.getStatus().equals(paymentTransaction.getDeliveryStatus().getStatus());

        if (!statusCompleted) {
            val currentProduct = paymentTransaction.getProduct();
            updateStock(currentProduct, paymentTransaction.getItemCount(), true);
        }
        this.transactionRepository.delete(paymentTransaction);
        return "OK";
    }

    private boolean isStatusCancelled(OrderTransactionDto orderTransactionDto, OrderTransactionEntity orderTransactionEntity) {
        if (DeliveryStatus.CANCELLED.getStatus().equals(orderTransactionDto.getDeliveryStatus().getStatus())) {
            val currentProduct = orderTransactionEntity.getProduct();
            updateStock(currentProduct, orderTransactionEntity.getItemCount(), true);
            return true;
        }
        return false;
    }


    private void validateProduct(OrderTransactionDto orderTransactionDto, OrderTransactionEntity orderTransactionEntity) {
        boolean productChanged = !orderTransactionDto.getProductId().equals(orderTransactionEntity.getProduct().getProductId());
        boolean itemCountChanged = !orderTransactionEntity.getItemCount().equals(orderTransactionDto.getItemCount());

        if (!productChanged && !itemCountChanged) {
            log.debug("No hay cambios");
            return;
        }

        val currentProduct = orderTransactionEntity.getProduct();
        val newProduct = this.productService.findProductEntityById(orderTransactionDto.getProductId());

        if (productChanged) {
            handleProductChange(orderTransactionDto, orderTransactionEntity, currentProduct, newProduct);
        } else {
            handleItemCountChange(orderTransactionDto, orderTransactionEntity, currentProduct);
        }
    }

    private void handleProductChange(OrderTransactionDto orderTransactionDto, OrderTransactionEntity orderTransactionEntity, ProductEntity currentProduct, ProductEntity newProduct) {
        log.debug("Hay cambios en producto");

        if (orderTransactionDto.getItemCount() > newProduct.getStock()) {
            log.debug("El itemCount excede el stock disponible del nuevo producto.");
            throw new ConflictException("Las unidades excede el stock disponible del nuevo producto.");
        }

        int previousItemCount = orderTransactionEntity.getItemCount();
        updateStock(currentProduct, previousItemCount, true);

        updateStock(newProduct, orderTransactionDto.getItemCount(), false);
    }

    private void handleItemCountChange(OrderTransactionDto orderTransactionDto, OrderTransactionEntity orderTransactionEntity, ProductEntity currentProduct) {
        log.debug("Solo cambio en Items");
        int currentItemCount = orderTransactionEntity.getItemCount();
        int newItemCount = orderTransactionDto.getItemCount();

        if (newItemCount < currentItemCount) {
            log.debug("Si itemCount es menor al actual");
            updateStock(currentProduct, currentItemCount - newItemCount, true);
        } else {
            log.debug("Si itemCount es mayor al actual");
            int difference = newItemCount - currentItemCount;
            if (difference > currentProduct.getStock()) {
                log.debug("El nuevo itemCount excede el stock disponible del producto.");
                throw new ConflictException("El nuevo valor de las unidades excede el stock disponible del producto.");
            }
            updateStock(currentProduct, difference, false);
        }
    }


    private void updateStock(ProductEntity product, int amount, boolean isAddition) {
        if (isAddition) {
            product.setStock(product.getStock() + amount);
        } else {
            product.setStock(product.getStock() - amount);
        }
        this.productRepository.save(product);
    }


    private OrderTransactionEntity findPaymentTransactionEntityEntityById(Integer id) {
        val productEntity = this.transactionRepository.findById(id);
        val message = String.format("No se pudo encontrar la transacci\u00F3n con el ID: %s", id);
        if (productEntity.isEmpty()) {
            log.debug(message);
            throw new NotFoundException(message);
        }
        return productEntity.get();
    }

    private void processPayment(OrderTransactionEntity orderTransactionEntity, BigDecimal total){
        val amountTotal = orderTransactionEntity.getExtraAmount().add(orderTransactionEntity.getAmountPaid());

        if (amountTotal.compareTo(total) >= 0) {
            orderTransactionEntity.setPaymentStatus(PaymentStatus.PAID);
        } else if (amountTotal.equals(BigDecimal.ZERO)) {
            orderTransactionEntity.setPaymentStatus(PaymentStatus.PENDING);
        } else {
            orderTransactionEntity.setPaymentStatus(PaymentStatus.INCOMPLETE);
        }
    }

    private OrderTransactionDto mapEntityToDto(OrderTransactionEntity orderTransactionEntity) {
        return this.modelMapper.map(orderTransactionEntity, OrderTransactionDto.class);
    }

    public OrderTransactionEntity mapDtoToEntity(OrderTransactionDto orderTransactionDto) {
        OrderTransactionEntity entity = modelMapper.map(orderTransactionDto, OrderTransactionEntity.class);
        ProductEntity product = productService.findProductEntityById(orderTransactionDto.getProductId());
        entity.setProduct(product);
        return entity;
    }


}
