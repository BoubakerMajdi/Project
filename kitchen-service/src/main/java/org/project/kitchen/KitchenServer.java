package org.project.kitchen;

import java.sql.SQLException;

import io.grpc.ServerBuilder;

public class KitchenServer
{
    public static void main(String[] args) throws Exception
    {
        KitchenDatabase db = waitForDatabase();

        int port = 50052;
        io.grpc.Server server = ServerBuilder.forPort(port)
                .addService(new KitchenServiceImpl(db))
                .build()
                .start();

        System.out.println("Kitchen Server started on port " + port);
        server.awaitTermination();
    }

    private static KitchenDatabase waitForDatabase() throws InterruptedException
    {
        int retries = 10;
        while (retries-- > 0) {
            try {
                return new KitchenDatabase();
            } catch (SQLException e) {
                System.out.println("Database not ready, retrying in 3s... (" + retries + " attempts left)");
                Thread.sleep(3000);
            }
        }
        throw new RuntimeException("Could not connect to database after retries");
    }
}