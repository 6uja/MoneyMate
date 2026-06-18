package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:moneymate.db";

    private DatabaseManager() {
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    public static void initializeDatabase() {
        String transactionSql = """
                CREATE TABLE IF NOT EXISTS transactions (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    date TEXT NOT NULL,
                    type TEXT NOT NULL,
                    category TEXT NOT NULL,
                    amount INTEGER NOT NULL,
                    memo TEXT
                );
                """;

        String budgetSql = """
                CREATE TABLE IF NOT EXISTS budgets (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    category TEXT NOT NULL,
                    month TEXT NOT NULL,
                    budget_amount INTEGER NOT NULL,
                    UNIQUE(category, month)
                );
                """;

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute(transactionSql);
            stmt.execute(budgetSql);

        } catch (SQLException e) {
            throw new RuntimeException("데이터베이스 초기화 실패", e);
        }
    }
}
