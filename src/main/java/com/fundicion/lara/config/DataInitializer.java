package com.fundicion.lara.config;

import com.fundicion.lara.commons.emuns.TransactionType;
import com.fundicion.lara.entity.TransactionEntity;
import com.fundicion.lara.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
public class DataInitializer  implements CommandLineRunner {

    @Autowired
    private TransactionRepository transactionRepository;

    @Override
    public void run(String... args) throws Exception {
        // Crear registros de ejemplo
        List<TransactionEntity> transactionEntities = new ArrayList<>();

        createTransactions(TransactionType.PURCHASE, transactionEntities, 4);
        createTransactions(TransactionType.SALE, transactionEntities, 5);
        createTransactions(TransactionType.EXPENSE, transactionEntities, 10);
        // Guardar las transacciones en la base de datos
        transactionRepository.saveAll(transactionEntities);
    }

    private void createTransactions(TransactionType type, List<TransactionEntity> transactionEntities, int count ) {
        for (int i = 1; i <= count; i++) {
            TransactionEntity transaction = TransactionEntity.builder()
                    .amount(new BigDecimal("100.00")) // Monto variable
                    .description(type == TransactionType.PURCHASE ? "Compra de materiales " + i :
                            type == TransactionType.SALE ? "Venta de productos " + i :
                                    "Gastos operativos " + i)
                    .invoiceNumber("INV-2023-" + (100 + i)) // Número de factura
                    .type(type)
                    .operationDate(LocalDate.now())
                    .status("A")
                    .build();
            // Guardar la transacción en la base de datos
            transactionEntities.add(transaction);
        }
    }

}
