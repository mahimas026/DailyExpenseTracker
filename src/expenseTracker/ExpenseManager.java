package expenseTracker;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class ExpenseManager {

    private int userId;
    public ExpenseManager(String email) {
        String sql = "SELECT user_id FROM users WHERE email = ?";
        try (Connection con = DBConnectivity.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                userId = rs.getInt("user_id");
            } else {
                throw new RuntimeException("User not found!");
            }
        } catch (SQLException e) {
            throw new RuntimeException(
                    "Database connection failed", e);
        }
    }
    public boolean addExpense(Expense expense) {
        String sql = "INSERT INTO expenses "
                + "(user_id, expense_date, category, amount, description) "
                + "VALUES (?, ?, ?, ?, ?)";
        try (Connection con = DBConnectivity.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setDate(2, Date.valueOf(expense.getDate()));
            ps.setString(3, expense.getCategory());
            ps.setDouble(4, expense.getAmount());
            ps.setString(5, expense.getDescription());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public List<Expense> getExpenses() {
        List<Expense> expenses = new ArrayList<>();
        String sql = "SELECT * FROM expenses WHERE user_id = ?";
        try (Connection con = DBConnectivity.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Expense expense = new Expense(
                        rs.getInt("expense_id"),
                        rs.getDate("expense_date").toString(),
                        rs.getString("category"),
                        rs.getDouble("amount"),
                        rs.getString("description")
                );
                expenses.add(expense);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return expenses;
    }
    public boolean updateExpense(Expense expense) {
        String sql = "UPDATE expenses SET expense_date = ?, "
                + "category = ?, amount = ?, description = ? "
                + "WHERE expense_id = ? AND user_id = ?";
        try (Connection con = DBConnectivity.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(expense.getDate()));
            ps.setString(2, expense.getCategory());
            ps.setDouble(3, expense.getAmount());
            ps.setString(4, expense.getDescription());
            ps.setInt(5, expense.getId());
            ps.setInt(6, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean deleteExpense(int id) {
        String sql = "DELETE FROM expenses "
                + "WHERE expense_id = ? AND user_id = ?";
        try (Connection con = DBConnectivity.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public List<Expense> searchByCategory(String category) {
        List<Expense> expenses = new ArrayList<>();
        String sql = "SELECT * FROM expenses "
                + "WHERE user_id = ? AND category = ?";
        try (Connection con = DBConnectivity.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, category);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                expenses.add(new Expense(
                        rs.getInt("expense_id"),
                        rs.getDate("expense_date").toString(),
                        rs.getString("category"),
                        rs.getDouble("amount"),
                        rs.getString("description")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return expenses;
    }
    public List<Expense> searchByDate(String date) {
        List<Expense> expenses = new ArrayList<>();
        String sql = "SELECT * FROM expenses "
                + "WHERE user_id = ? AND expense_date = ?";
        try (Connection con = DBConnectivity.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setDate(2, Date.valueOf(date));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                expenses.add(new Expense(
                        rs.getInt("expense_id"),
                        rs.getDate("expense_date").toString(),
                        rs.getString("category"),
                        rs.getDouble("amount"),
                        rs.getString("description")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return expenses;
    }
    public double getTotalExpenses() {
        String sql = "SELECT COALESCE(SUM(amount), 0) "
                + "FROM expenses WHERE user_id = ?";
        try (Connection con = DBConnectivity.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}