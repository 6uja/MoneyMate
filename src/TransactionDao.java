package dao;

import db.DatabaseManager;
import model.Transaction;
import model.TransactionType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransactionDao {

    public void insert(Transaction transaction) {
        String sql = """
                INSERT INTO transactions(date, type, category, amount, memo)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, transaction.getDate());
            pstmt.setString(2, transaction.getType().name());
            pstmt.setString(3, transaction.getCategory());
            pstmt.setInt(4, transaction.getAmount());
            pstmt.setString(5, transaction.getMemo());
            pstmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("거래 내역 저장 실패", e);
        }
    }

    public List<Transaction> findAll() {
        String sql = "SELECT * FROM transactions ORDER BY date DESC, id DESC";
        List<Transaction> transactions = new ArrayList<>();

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                transactions.add(mapToTransaction(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("거래 내역 조회 실패", e);
        }

        return transactions;
    }

    public List<Transaction> findByMonth(String month) {
        String sql = """
                SELECT * FROM transactions
                WHERE date LIKE ?
                ORDER BY date DESC, id DESC
                """;

        List<Transaction> transactions = new ArrayList<>();

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, month + "%");

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    transactions.add(mapToTransaction(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("월별 거래 내역 조회 실패", e);
        }

        return transactions;
    }

    public void deleteById(int id) {
        String sql = "DELETE FROM transactions WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("거래 내역 삭제 실패", e);
        }
    }

    private Transaction mapToTransaction(ResultSet rs) throws SQLException {
        return new Transaction(
                rs.getInt("id"),
                rs.getString("date"),
                TransactionType.valueOf(rs.getString("type")),
                rs.getString("category"),
                rs.getInt("amount"),
                rs.getString("memo")
        );
    }
}
