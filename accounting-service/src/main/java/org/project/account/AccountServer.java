package org.project.account;

import io.grpc.ServerBuilder;
import java.sql.SQLException;

public class AccountServer
{
    public static void main(String[] args) throws Exception
    {
        AccountDatabase db = waitForDatabase();

        int port = 50053;
        io.grpc.Server server = ServerBuilder.forPort(port)
                .addService(new AccountServiceImpl(db))
                .build()
                .start();

        System.out.println("Account Server started on port " + port);
        server.awaitTermination();
    }

    private static AccountDatabase waitForDatabase() throws InterruptedException
    {
        int retries = 10;
        while (retries-- > 0) {
            try {
                return new AccountDatabase();
            } catch (SQLException e) {
                System.out.println("Database not ready, retrying in 3s... (" + retries + " attempts left)");
                Thread.sleep(3000);
            }
        }
        throw new RuntimeException("Could not connect to database after retries");
    }
}