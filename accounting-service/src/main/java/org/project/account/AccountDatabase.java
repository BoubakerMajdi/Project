package org.project.account;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AccountDatabase
{
    private final Connection connection;

    public AccountDatabase() throws SQLException
    {
        String url  = System.getenv().getOrDefault("DB_URL",  "jdbc:postgresql://account-db:5432/accountdb");
        String user = System.getenv().getOrDefault("DB_USER", "postgres");
        String pass = System.getenv().getOrDefault("DB_PASS", "postgres");

        this.connection = DriverManager.getConnection(url, user, pass);
    }

    public Account getAccount(String customerId) throws SQLException
    {
        String sql = "SELECT * FROM accounts WHERE customer_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, customerId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Account(
                        rs.getString("customer_id"),
                        rs.getDouble("balance")
                );
            }
        }
        return null;
    }

    public void updateBalance(String customerId, double newBalance) throws SQLException
    {
        String sql = "UPDATE accounts SET balance = ? WHERE customer_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDouble(1, newBalance);
            stmt.setString(2, customerId);
            stmt.executeUpdate();
        }
    }
}