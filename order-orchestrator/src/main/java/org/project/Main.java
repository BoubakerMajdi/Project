package org.project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

@SpringBootApplication
public class Main
{
    public static void main(String[] args)
    {
        SpringApplication.run(Main.class, args);
    }

    @Bean
    public Orchestrator orchestrator()
    {
        int orderPort = Integer.parseInt(System.getenv().getOrDefault("ORDER_SERVICE_PORT", "50051"));
        int kitchenPort = Integer.parseInt(System.getenv().getOrDefault("KITCHEN_SERVICE_PORT", "50052"));
        int accountPort = Integer.parseInt(System.getenv().getOrDefault("ACCOUNTING_SERVICE_PORT", "50053"));

        ManagedChannel orderChannel = ManagedChannelBuilder.forAddress("order-service", orderPort).usePlaintext().build();
        ManagedChannel kitchenChannel = ManagedChannelBuilder.forAddress("kitchen-service", kitchenPort).usePlaintext().build();
        ManagedChannel accountChannel = ManagedChannelBuilder.forAddress("account-service", accountPort).usePlaintext().build();

        return new Orchestrator(orderChannel, kitchenChannel, accountChannel);
    }
}