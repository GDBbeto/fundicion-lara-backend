package com.fundicion.lara.events;

import com.fundicion.lara.repository.OrderTransactionRepository;
import com.fundicion.lara.repository.TransactionRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

import static com.fundicion.lara.commons.constants.Constants.BATCH_CRON_EXPRESSION;
import static com.fundicion.lara.commons.constants.Constants.TWO_YEARS_AGO;

@Slf4j
@AllArgsConstructor
@Component
public class DailyTask {
    private TransactionRepository transactionRepository;
    private OrderTransactionRepository orderTransactionRepository;


    // @Scheduled(cron = "0 */1 * * * ?")
    @Scheduled(cron = BATCH_CRON_EXPRESSION)
    public void cleanOldTransactions() {
        LocalDate twoYearsAgo = LocalDate.now().minusYears(TWO_YEARS_AGO);
        var oldTransactions = this.transactionRepository.findAllByOperationDateBefore(twoYearsAgo);
        var oldOrderTransactions = this.orderTransactionRepository.findAllByOperationDateBefore(twoYearsAgo);
        cleanTransactions(oldTransactions, transactionRepository, "transacciones");
        cleanTransactions(oldOrderTransactions, orderTransactionRepository, "órdenes de transacciones");
    }

    private <T> void cleanTransactions(List<T> transactions, JpaRepository<T, ?> repository, String transactionType) {
        log.info("===================Limpiando {} antiguas ===================", transactionType);
        if (!transactions.isEmpty()) {
            repository.deleteAll(transactions);
            log.info("Se han depurado {} {} antiguas.", transactions.size(), transactionType);
        } else {
            log.info("No hay {} antiguas para depurar.", transactionType);
        }
    }

}
