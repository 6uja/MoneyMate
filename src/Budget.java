package model;

public class Budget {
    private int id;
    private String category;
    private String month;
    private int budgetAmount;

    public Budget(int id, String category, String month, int budgetAmount) {
        this.id = id;
        this.category = category;
        this.month = month;
        this.budgetAmount = budgetAmount;
    }

    public int getId() {
        return id;
    }

    public String getCategory() {
        return category;
    }

    public String getMonth() {
        return month;
    }

    public int getBudgetAmount() {
        return budgetAmount;
    }
}
