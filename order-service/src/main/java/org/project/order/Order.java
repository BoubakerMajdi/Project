package org.project.order;

public class Order
{
    private final String orderId;
    private final String customerId;
    private final String serviceType;
    private final String priority;
    private final String location;
    private final String description;
    private final double estimatedCost;
    private RequestStatus status;

    public Order(String orderId, String customerId, String serviceType, String priority, String location, String description, double estimatedCost, RequestStatus status)
    {
        this.orderId = orderId;
        this.customerId = customerId;
        this.serviceType = serviceType;
        this.priority = priority;
        this.location = location;
        this.description = description;
        this.estimatedCost = estimatedCost;
        this.status = status;
    }

    public String getOrderId()
    {
        return this.orderId;
    }

    public String getCustomerId()
    {
        return this.customerId;
    }

    public String getServiceType()
    {
        return this.serviceType;
    }

    public String getPriority()
    {
        return this.priority;
    }

    public String getLocation()
    {
        return this.location;
    }

    public String getDescription()
    {
        return this.description;
    }

    public double getEstimatedCost()
    {
        return this.estimatedCost;
    }

    public RequestStatus getStatus()
    {
        return this.status;
    }

    public void setStatus(RequestStatus newStatus)
    {
        this.status = newStatus;
    }
}