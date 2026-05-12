package org.project;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.project.account.AccountServiceGrpc;
import org.project.account.AuthorizationRequest;
import org.project.account.AuthorizationResponse;
import org.project.account.AuthorizationStatus;
import org.project.kitchen.AcceptTicketRequest;
import org.project.kitchen.CancelTicketRequest;
import org.project.kitchen.CreateTicketRequest;
import org.project.kitchen.CreateTicketResponse;
import org.project.kitchen.KitchenServiceGrpc;
import org.project.kitchen.RejectTicketRequest;
import org.project.order.CreateRequestRequest;
import org.project.order.CreateRequestResponse;
import org.project.order.GetRequestRequest;
import org.project.order.GetRequestResponse;
import org.project.order.RequestServiceGrpc;
import org.project.order.RequestStatus;
import org.project.order.UpdateRequestStatusRequest;

import io.grpc.ManagedChannel;

public class Orchestrator
{
    private final RequestServiceGrpc.RequestServiceBlockingStub orderService;
    private final KitchenServiceGrpc.KitchenServiceBlockingStub kitchenService;
    private final AccountServiceGrpc.AccountServiceBlockingStub accountService;

    private final Map<String, PendingOrder> pendingOrders = new ConcurrentHashMap<>();

    private record PendingOrder(String orderId, String customerId, double amount) {}

    public Orchestrator(ManagedChannel orderChannel, ManagedChannel kitchenChannel, ManagedChannel accountChannel)
    {
        this.orderService = RequestServiceGrpc.newBlockingStub(orderChannel);
        this.kitchenService = KitchenServiceGrpc.newBlockingStub(kitchenChannel);
        this.accountService = AccountServiceGrpc.newBlockingStub(accountChannel);
    }

    public String initiateOrder(String customerId, double amount)
    {
        // Create request in order service
        CreateRequestRequest orderCreateRequest = CreateRequestRequest.newBuilder()
                .setCustomerId(customerId)
                .setServiceType("Web Order")
                .setPriority("Medium")
                .setLocation("Online")
                .setDescription("Food order created through the web UI")
                .setEstimatedCost(amount)
                .build();

        CreateRequestResponse orderCreateResponse = orderService.createRequest(orderCreateRequest);

        if (!orderCreateResponse.getSuccess())
        {
            return null;
        }

        String orderId = orderCreateResponse.getRequestId();

        // Create Kitchen Ticket
        CreateTicketRequest ticketCreateRequest = CreateTicketRequest.newBuilder()
                .setTicketId(orderId)
                .setCustomerId(customerId)
                .build();

        CreateTicketResponse ticketCreateResponse = kitchenService.createTicket(ticketCreateRequest);

        if (!ticketCreateResponse.getSuccess())
        {
            // Compensate: cancel the order since ticket creation failed
            UpdateRequestStatusRequest updateOrderRequest = UpdateRequestStatusRequest.newBuilder()
                    .setRequestId(orderId)
                    .setStatus(RequestStatus.CANCELED)
                    .build();
            orderService.updateRequestStatus(updateOrderRequest);
            return null;
        }

        // Store pending order data until user confirms or cancels
        pendingOrders.put(orderId, new PendingOrder(orderId, customerId, amount));

        return orderId;
    }

    public boolean confirmOrder(String orderId)
    {
        PendingOrder pending = pendingOrders.remove(orderId);

        if (pending == null)
        {
            System.out.println("No pending order found for orderId: " + orderId);
            return false;
        }

        // Process Payment
        AuthorizationRequest authRequest = AuthorizationRequest.newBuilder()
                .setOrderId(pending.orderId())
                .setCustomerId(pending.customerId())
                .setAmount(pending.amount())
                .build();

        AuthorizationResponse authResponse = accountService.authorizePayment(authRequest);

        if (!authResponse.getSuccess())
        {
            // RPC itself failed, compensate both
            UpdateRequestStatusRequest updateOrderRequest = UpdateRequestStatusRequest.newBuilder()
                    .setRequestId(orderId)
                    .setStatus(RequestStatus.REJECTED)
                    .build();
            orderService.updateRequestStatus(updateOrderRequest);

            RejectTicketRequest rejectTicketRequest = RejectTicketRequest.newBuilder()
                    .setTicketId(orderId)
                    .build();
            kitchenService.rejectTicket(rejectTicketRequest);

            return false;
        }

        if (authResponse.getStatus() == AuthorizationStatus.ACCEPTED)
        {
            // Update order to APPROVED
            UpdateRequestStatusRequest updateOrderRequest = UpdateRequestStatusRequest.newBuilder()
                    .setRequestId(orderId)
                    .setStatus(RequestStatus.APPROVED)
                    .build();
            orderService.updateRequestStatus(updateOrderRequest);

            // Accept kitchen ticket
            AcceptTicketRequest acceptTicketRequest = AcceptTicketRequest.newBuilder()
                    .setTicketId(orderId)
                    .build();
            kitchenService.acceptTicket(acceptTicketRequest);

            return true;
        }
        else
        {
            // Payment rejected, compensate both
            UpdateRequestStatusRequest updateOrderRequest = UpdateRequestStatusRequest.newBuilder()
                    .setRequestId(orderId)
                    .setStatus(RequestStatus.REJECTED)
                    .build();
            orderService.updateRequestStatus(updateOrderRequest);

            RejectTicketRequest rejectTicketRequest = RejectTicketRequest.newBuilder()
                    .setTicketId(orderId)
                    .build();
            kitchenService.rejectTicket(rejectTicketRequest);

            return false;
        }
    }

    public void cancelOrder(String orderId)
    {
        PendingOrder pending = pendingOrders.remove(orderId);

        if (pending == null)
        {
            System.out.println("No pending order found for orderId: " + orderId);
            return;
        }

        // Cancel order
        UpdateRequestStatusRequest updateOrderRequest = UpdateRequestStatusRequest.newBuilder()
                .setRequestId(orderId)
                .setStatus(RequestStatus.CANCELED)
                .build();
        orderService.updateRequestStatus(updateOrderRequest);

        // Cancel kitchen ticket
        CancelTicketRequest cancelTicketRequest = CancelTicketRequest.newBuilder()
                .setTicketId(orderId)
                .build();
        kitchenService.cancelTicket(cancelTicketRequest);
    }

    public Order getOrder(String orderId)
    {
        GetRequestRequest request = GetRequestRequest.newBuilder()
                .setRequestId(orderId)
                .build();

        GetRequestResponse response = orderService.getRequest(request);

        if (response.getSuccess()) {
            return new Order(response.getRequestId(), response.getCustomerId(), response.getEstimatedCost(), response.getStatus());
        }
        return null;
    }
}