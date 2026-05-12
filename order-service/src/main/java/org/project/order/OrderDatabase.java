package org.project.order;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class OrderDatabase
{
    private final Connection connection;

    public OrderDatabase() throws SQLException
    {
        String url  = System.getenv().getOrDefault("DB_URL",  "jdbc:postgresql://order-db:5432/orderdb");
        String user = System.getenv().getOrDefault("DB_USER", "postgres");
        String pass = System.getenv().getOrDefault("DB_PASS", "postgres");

        this.connection = DriverManager.getConnection(url, user, pass);
        initTable();
    }

    private void initTable() throws SQLException
    {
        String sql = """
                CREATE TABLE IF NOT EXISTS requests (
                    request_id    VARCHAR(26)  NOT NULL PRIMARY KEY,
                    customer_id   VARCHAR(255) NOT NULL,
                    service_type  VARCHAR(255) NOT NULL,
                    priority      VARCHAR(50)  NOT NULL,
                    location      VARCHAR(255) NOT NULL,
                    description   TEXT         NOT NULL,
                    estimated_cost DOUBLE PRECISION NOT NULL,
                    status        VARCHAR(50)  NOT NULL
                )
                """;
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        }
    }

    public void saveOrder(Order order) throws SQLException
    {
        String sql = "INSERT INTO requests (request_id, customer_id, service_type, priority, location, description, estimated_cost, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, order.getOrderId());
            stmt.setString(2, order.getCustomerId());
            stmt.setString(3, order.getServiceType());
            stmt.setString(4, order.getPriority());
            stmt.setString(5, order.getLocation());
            stmt.setString(6, order.getDescription());
            stmt.setDouble(7, order.getEstimatedCost());
            stmt.setString(8, order.getStatus().name());
            stmt.executeUpdate();
        }
    }

    // Returns true if the order was found and updated, false if it didn't exist
    public boolean updateStatus(String orderId, RequestStatus status) throws SQLException
    {
        String sql = "UPDATE requests SET status = ? WHERE request_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, status.name());
            stmt.setString(2, orderId);
            return stmt.executeUpdate() > 0;
        }
    }

    // Returns the order if found, null if it doesn't exist
    public Order getOrder(String orderId) throws SQLException
    {
        String sql = "SELECT request_id, customer_id, service_type, priority, location, description, estimated_cost, status FROM requests WHERE request_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, orderId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String customerId = rs.getString("customer_id");
                    String serviceType = rs.getString("service_type");
                    String priority = rs.getString("priority");
                    String location = rs.getString("location");
                    String description = rs.getString("description");
                    double estimatedCost = rs.getDouble("estimated_cost");
                    RequestStatus status = RequestStatus.valueOf(rs.getString("status"));
                    return new Order(orderId, customerId, serviceType, priority, location, description, estimatedCost, status);
                }
            }
        }
        return null;
    }
}