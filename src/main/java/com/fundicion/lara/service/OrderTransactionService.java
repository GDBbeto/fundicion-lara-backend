package com.fundicion.lara.service;

import com.fundicion.lara.commons.emuns.DeliveryStatus;
import com.fundicion.lara.commons.emuns.PaymentStatus;
import com.fundicion.lara.commons.emuns.Status;
import com.fundicion.lara.dto.OrderTransactionDto;
import com.fundicion.lara.dto.request.OrderTransactionRequest;
import com.fundicion.lara.dto.request.RequestParams;
import com.fundicion.lara.entity.OrderTransactionEntity;
import com.fundicion.lara.entity.ProductEntity;
import com.fundicion.lara.exception.NotFoundException;
import com.fundicion.lara.repository.OrderTransactionRepository;
import com.fundicion.lara.utils.SpecificationUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class OrderTransactionService {
    private OrderTransactionRepository transactionRepository;
    private ProductService productService;
    private ModelMapper modelMapper;
    private TransactionService transactionService;

    public List<OrderTransactionDto> findAllPaymentTransactions(RequestParams requestParams) {
        var pagination = requestParams.getPagination();
        var sort = Sort.by(Sort.Direction.fromString(requestParams.getOrder()), requestParams.getOrderBy());

        var specification = SpecificationUtil.getSpecificationByParams(requestParams, OrderTransactionEntity.class);
        var pageable = PageRequest.of(pagination.getNumberPage(), pagination.getPageSize(), sort);

        val response = this.transactionRepository.findAll(specification, pageable);
        if (response.isEmpty()) {
            log.info("No transactions found {}", requestParams);
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

    @Transactional
    public OrderTransactionDto savePaymentTransaction(OrderTransactionRequest orderTransactionDto) {
        var orderTransactionEntity = this.mapRequestDtoToEntity(orderTransactionDto);

        val product = orderTransactionEntity.getProduct();

        orderTransactionEntity.setSellingPrice(product.getSellingPrice());
        orderTransactionEntity.setPurchasePrice(product.getPurchasePrice());
        if (orderTransactionDto.getDeliveryStatus() == null) {
            orderTransactionEntity.setDeliveryStatus(DeliveryStatus.PENDING);
        }

        orderTransactionEntity = this.transactionRepository.save(orderTransactionEntity);

        if (orderTransactionDto.getRegisterInSales()) {
            this.transactionService.saveTransactionByOrderTransaction(orderTransactionEntity);
        }
        return this.mapEntityToDto(orderTransactionEntity);
    }

    @Transactional
    public OrderTransactionDto updatePaymentTransaction(OrderTransactionRequest orderTransactionDto, Integer orderTransactionId) {
        var orderTransactionEntity = this.findPaymentTransactionEntityEntityById(orderTransactionId);

        if (isStatusCancelled(orderTransactionDto)) {
            orderTransactionEntity.setDeliveryStatus(orderTransactionDto.getDeliveryStatus());
            this.transactionService.updateTransactionByOrderTransaction(orderTransactionEntity, Status.INACTIVE.getValue());
            this.transactionRepository.save(orderTransactionEntity);
            return this.mapEntityToDto(orderTransactionEntity);
        }
        if (isStatusCompleted(orderTransactionDto)) {
            boolean productChanged = !orderTransactionDto.getProductId().equals(orderTransactionEntity.getProduct().getProductId());

            if (productChanged) {
                orderTransactionEntity.setSellingPrice(orderTransactionEntity.getProduct().getSellingPrice());
                orderTransactionEntity.setPurchasePrice(orderTransactionEntity.getProduct().getPurchasePrice());
            }
        }

        orderTransactionEntity.setProduct(orderTransactionEntity.getProduct());
        orderTransactionEntity.setItemCount(orderTransactionDto.getItemCount());
        orderTransactionEntity.setExtraAmount(orderTransactionDto.getExtraAmount());
        orderTransactionEntity.setDescription(orderTransactionDto.getDescription());
        orderTransactionEntity.setMethodPayment(orderTransactionDto.getMethodPayment());
        orderTransactionEntity.setInvoiceNumber(String.valueOf(orderTransactionDto.getInvoiceNumber()));
        orderTransactionEntity.setClient(String.valueOf(orderTransactionDto.getClient()));
        orderTransactionEntity.setAmountPaid(orderTransactionDto.getAmountPaid());
        orderTransactionEntity.setRegisterInSales(orderTransactionDto.getRegisterInSales());

        // BigDecimal total = orderTransactionEntity.getSellingPrice().multiply(BigDecimal.valueOf(orderTransactionEntity.getItemCount()));
        // this.processPayment(orderTransactionEntity, total);

        orderTransactionEntity.setDeliveryStatus(orderTransactionDto.getDeliveryStatus());
        orderTransactionEntity.setOperationDate(orderTransactionDto.getOperationDate());

        if (orderTransactionDto.getRegisterInSales()) {
            this.transactionService.updateTransactionByOrderTransaction(orderTransactionEntity, Status.ACTIVE.getValue());
        }

        this.transactionRepository.save(orderTransactionEntity);

        return this.mapEntityToDto(orderTransactionEntity);
    }


    @Transactional
    public String deleteTransactionById(Integer id) {
        val paymentTransaction = this.findPaymentTransactionEntityEntityById(id);

        this.transactionService.deleteTransactionByByOrderTransactionId(id);

        this.transactionRepository.delete(paymentTransaction);
        return "OK";
    }

    private boolean isStatusCancelled(OrderTransactionRequest orderTransactionDto) {
        return DeliveryStatus.CANCELLED.getStatus().equals(orderTransactionDto.getDeliveryStatus().getStatus());
    }

    private boolean isStatusCompleted(OrderTransactionRequest paymentTransaction) {
        return PaymentStatus.PAID.getStatus().equals(paymentTransaction.getPaymentStatus().getStatus()) &&
                DeliveryStatus.DELIVERED.getStatus().equals(paymentTransaction.getDeliveryStatus().getStatus());
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

    private void processPayment(OrderTransactionEntity orderTransactionEntity, BigDecimal total) {
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

    public OrderTransactionEntity mapRequestDtoToEntity(OrderTransactionRequest orderTransactionDto) {
        OrderTransactionEntity entity = modelMapper.map(orderTransactionDto, OrderTransactionEntity.class);
        ProductEntity product = productService.findProductEntityById(orderTransactionDto.getProductId());
        entity.setProduct(product);
        return entity;
    }


}
