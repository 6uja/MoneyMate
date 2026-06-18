package service;

import dao.BudgetDao;
import dao.TransactionDao;
import model.Transaction;
import model.TransactionType;
import util.MoneyFormatter;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class FinanceService {

    private final TransactionDao transactionDao;
    private final BudgetDao budgetDao;

    public FinanceService(TransactionDao transactionDao, BudgetDao budgetDao) {
        this.transactionDao = transactionDao;
        this.budgetDao = budgetDao;
    }

    public int getMonthlyIncome(LocalDate date) {
        return getMonthlyTotal(date, TransactionType.INCOME);
    }

    public int getMonthlyExpense(LocalDate date) {
        return getMonthlyTotal(date, TransactionType.EXPENSE);
    }

    private int getMonthlyTotal(LocalDate date, TransactionType type) {
        String month = date.toString().substring(0, 7);
        List<Transaction> transactions = transactionDao.findByMonth(month);

        return transactions.stream()
                .filter(t -> t.getType() == type)
                .mapToInt(Transaction::getAmount)
                .sum();
    }

    public String getBudgetWarning(String category, LocalDate date) {
        String month = date.toString().substring(0, 7);
        Optional<Integer> optionalBudget = budgetDao.findBudgetAmount(category, month);

        if (optionalBudget.isEmpty()) {
            return "예산 정보: 이번 달 " + category + " 예산이 설정되지 않았습니다.";
        }

        int budget = optionalBudget.get();
        int used = transactionDao.findByMonth(month).stream()
                .filter(t -> t.getType() == TransactionType.EXPENSE)
                .filter(t -> t.getCategory().equals(category))
                .mapToInt(Transaction::getAmount)
                .sum();

        double rate = budget == 0 ? 0 : (used * 100.0 / budget);

        if (rate >= 100) {
            return "예산 초과 경고: " + category + " 예산 " + MoneyFormatter.format(budget)
                    + "을 초과했습니다. 현재 사용: " + MoneyFormatter.format(used);
        }

        if (rate >= 80) {
            return "예산 주의: " + category + " 예산의 " + String.format("%.1f", rate)
                    + "%를 사용했습니다.";
        }

        return "예산 상태: " + category + " " + MoneyFormatter.format(used)
                + " / " + MoneyFormatter.format(budget)
                + " 사용 중입니다.";
    }
}
