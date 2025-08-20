package com.fundicion.lara.service;

import com.fundicion.lara.commons.emuns.DeliveryStatus;
import com.fundicion.lara.dto.TransactionDto;
import com.fundicion.lara.dto.request.RequestParams;
import com.fundicion.lara.entity.TransactionEntity;
import com.fundicion.lara.exception.NotFoundException;
import com.fundicion.lara.repository.TransactionRepository;
import com.fundicion.lara.utils.SpecificationUtil;
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
@Service
@AllArgsConstructor
public class TransactionService {
    private TransactionRepository transactionRepository;
    private ModelMapper modelMapper;

    public List<TransactionDto> findAllTransactions(RequestParams requestParams) {
        var pagination = requestParams.getPagination();
        var sort = Sort.by(Sort.Direction.fromString(requestParams.getOrder()), requestParams.getOrderBy());

        var specification = SpecificationUtil.getSpecificationByParams(requestParams, TransactionEntity.class);
        var pageable = PageRequest.of(pagination.getNumberPage(), pagination.getPageSize(), sort);

        val response = this.transactionRepository.findAll(specification, pageable);
        if (response.isEmpty()) {
            throw new NotFoundException("Parece que no tenemos ninguna transacción en este momento.");
        }
        pagination.setTotalElements(response.getTotalElements());
        return response.getContent().stream()
                .map(entity -> this.modelMapper.map(entity, TransactionDto.class))
                .collect(Collectors.toList());
    }

    public TransactionDto findTransactionById(Long productId) {
        return modelMapper.map(findEntityById(productId), TransactionDto.class);
    }

    public TransactionDto saveTransaction(TransactionDto transactionDto) {
        var transactionEntity = modelMapper.map(transactionDto, TransactionEntity.class);
        transactionEntity = transactionRepository.save(transactionEntity);
        return this.modelMapper.map(transactionEntity, TransactionDto.class);
    }

    public TransactionDto updateTransactionById(TransactionDto transactionDto, Long transactionId) {
        var transactionEntity = this.findEntityById(transactionId);
        transactionEntity.setType(transactionDto.getType());
        transactionEntity.setAmount(transactionDto.getAmount());
        transactionEntity.setDescription(transactionDto.getDescription());
        transactionEntity.setInvoiceNumber(transactionDto.getInvoiceNumber());
        transactionEntity.setOperationDate(transactionDto.getOperationDate());
        return this.modelMapper.map(transactionRepository.save(transactionEntity), TransactionDto.class);
    }

    public String deleteTransactionById(Long id) {
        val paymentTransaction = this.findEntityById(id);
        this.transactionRepository.delete(paymentTransaction);
        return "OK";
    }


    private TransactionEntity findEntityById(Long id) {
        val transactionEntity = this.transactionRepository.findById(id);
        val message = String.format("No se pudo encontrar la transacción con el ID: %s", id);
        if (transactionEntity.isEmpty()) {
            log.debug(message);
            throw new NotFoundException(message);
        }
        return transactionEntity.get();
    }

}
