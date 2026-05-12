package org.project;

import org.project.order.RequestStatus;

public class Order
{
    private final String orderId;
    private final String customerId;
    private final double amount;
    private final RequestStatus status;

    public Order(String orderId, String customerId, double amount, RequestStatus status)
    {
        this.orderId = orderId;
        this.customerId = customerId;
        this.amount = amount;
        this.status = status;
    }

    public String getOrderId()
    {
        return orderId;
    }

    public String getCustomerId()
    {
        return customerId;
    }

    public double getAmount()
    {
        return amount;
    }

    public RequestStatus getStatus()
    {
        return status;
    }
}