package org.project.kitchen;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class KitchenDatabase
{
    private final Connection connection;

    public KitchenDatabase() throws SQLException
    {
        String url  = System.getenv().getOrDefault("DB_URL",  "jdbc:postgresql://kitchen-db:5432/kitchendb");
        String user = System.getenv().getOrDefault("DB_USER", "postgres");
        String pass = System.getenv().getOrDefault("DB_PASS", "postgres");

        this.connection = DriverManager.getConnection(url, user, pass);
        initTable();
    }

    private void initTable() throws SQLException
    {
        String sql = """
                CREATE TABLE IF NOT EXISTS tickets (
                    ticket_id   VARCHAR(26)  NOT NULL PRIMARY KEY,
                    customer_id VARCHAR(255) NOT NULL,
                    status      VARCHAR(50)  NOT NULL
                )
                """;
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        }
    }

    public void saveTicket(Ticket ticket) throws SQLException
    {
        String sql = "INSERT INTO tickets (ticket_id, customer_id, status) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, ticket.getTicketId());
            stmt.setString(2, ticket.getCustomerId());
            stmt.setString(3, ticket.getStatus().name());
            stmt.executeUpdate();
        }
    }

    // Returns true if the ticket was found and updated, false if it didn't exist
    public boolean updateStatus(String ticketId, TicketStatus status) throws SQLException
    {
        String sql = "UPDATE tickets SET status = ? WHERE ticket_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, status.name());
            stmt.setString(2, ticketId);
            return stmt.executeUpdate() > 0;
        }
    }
}