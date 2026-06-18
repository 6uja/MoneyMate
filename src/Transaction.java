package model;

public class Transaction {
    private int id;
    private String date;
    private TransactionType type;
    private String category;
    private int amount;
    private String memo;

    public Transaction(int id, String date, TransactionType type, String category, int amount, String memo) {
        this.id = id;
        this.date = date;
        this.type = type;
        this.category = category;
        this.amount = amount;
        this.memo = memo;
    }

    public int getId() {
        return id;
    }

    public String getDate() {
        return date;
    }

    public TransactionType getType() {
        return type;
    }

    public String getCategory() {
        return category;
    }

    public int getAmount() {
        return amount;
    }

    public String getMemo() {
        return memo;
    }
}
