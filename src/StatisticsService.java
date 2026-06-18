package service;

import dao.TransactionDao;
import model.Transaction;
import model.TransactionType;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class StatisticsService {

    private final TransactionDao transactionDao;

    public StatisticsService(TransactionDao transactionDao) {
        this.transactionDao = transactionDao;
    }

    public Map<String, Integer> getExpenseByCategory(LocalDate date) {
        String month = date.toString().substring(0, 7);

        return transactionDao.findByMonth(month).stream()
                .filter(t -> t.getType() == TransactionType.EXPENSE)
                .collect(Collectors.groupingBy(
                        Transaction::getCategory,
                        LinkedHashMap::new,
                        Collectors.summingInt(Transaction::getAmount)
                ));
    }
}
