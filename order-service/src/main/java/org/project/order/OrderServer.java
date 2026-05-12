package org.project.order;

import io.grpc.ServerBuilder;
import java.sql.SQLException;

public class OrderServer
{
    public static void main(String[] args) throws Exception
    {
        OrderDatabase db = waitForDatabase();

        int port = 50051;
        io.grpc.Server server = ServerBuilder.forPort(port)
                .addService(new OrderServiceImpl(db))
                .build()
                .start();

        System.out.println("Order Server started on port " + port);
        server.awaitTermination();
    }

    private static OrderDatabase waitForDatabase() throws InterruptedException
    {
        int retries = 10;
        while (retries-- > 0) {
            try {
                return new OrderDatabase();
            } catch (SQLException e) {
                System.out.println("Database not ready, retrying in 3s... (" + retries + " attempts left)");
                Thread.sleep(3000);
            }
        }
        throw new RuntimeException("Could not connect to database after retries");
    }
}