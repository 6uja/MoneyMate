package dao;

import db.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class BudgetDao {

    public void saveOrUpdateBudget(String category, String month, int amount) {
        String sql = """
                INSERT INTO budgets(category, month, budget_amount)
                VALUES (?, ?, ?)
                ON CONFLICT(category, month)
                DO UPDATE SET budget_amount = excluded.budget_amount
                """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, category);
            pstmt.setString(2, month);
            pstmt.setInt(3, amount);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("예산 저장 실패", e);
        }
    }

    public Optional<Integer> findBudgetAmount(String category, String month) {
        String sql = """
                SELECT budget_amount FROM budgets
                WHERE category = ? AND month = ?
                """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, category);
            pstmt.setString(2, month);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(rs.getInt("budget_amount"));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("예산 조회 실패", e);
        }

        return Optional.empty();
    }
}
