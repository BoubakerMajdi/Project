package org.project;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = {"http://localhost:8081", "http://localhost:8080", "*"},
             methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS},
             allowedHeaders = "*",
             allowCredentials = "false")
@RestController
@RequestMapping("/orders")
public class OrderController
{
    private final Orchestrator orchestrator;

    public OrderController(Orchestrator orchestrator)
    {
        this.orchestrator = orchestrator;
    }

    @PostMapping
    public String initiateOrder(@RequestParam String customerId, @RequestParam double amount)
    {
        String orderId = orchestrator.initiateOrder(customerId, amount);
        if (orderId == null)
        {
            return "Failed to create order";
        }
        return orderId;
    }

    @PostMapping("/{orderId}/confirm")
    public String confirmOrder(@PathVariable String orderId)
    {
        boolean result = orchestrator.confirmOrder(orderId);
        return result ? "Order approved" : "Order rejected";
    }

    @PostMapping("/{orderId}/cancel")
    public String cancelOrder(@PathVariable String orderId)
    {
        orchestrator.cancelOrder(orderId);
        return "Order cancelled";
    }

    @GetMapping(params = {"customerId", "amount"})
    public String initiateOrderViaGet(@RequestParam String customerId, @RequestParam double amount)
    {
        String orderId = orchestrator.initiateOrder(customerId, amount);
        if (orderId == null)
        {
            return "Failed to create order";
        }
        return orderId;
    }

    @GetMapping
    public String getOrder(@RequestParam String orderId)
    {
        Order order = orchestrator.getOrder(orderId);
        if (order == null)
        {
            return "{\"success\": false, \"message\": \"Order not found\"}";
        }
        return String.format("{\"success\": true, \"order\": {\"orderId\": \"%s\", \"status\": \"%s\"}}",
                           order.getOrderId(), order.getStatus());
    }
}