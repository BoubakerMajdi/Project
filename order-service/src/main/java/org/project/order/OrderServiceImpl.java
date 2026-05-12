package org.project.order;

import com.github.f4b6a3.ulid.UlidCreator;
import io.grpc.stub.StreamObserver;

public class OrderServiceImpl extends RequestServiceGrpc.RequestServiceImplBase
{
    private final OrderDatabase db;

    public OrderServiceImpl(OrderDatabase db)
    {
        this.db = db;
    }

    @Override
    public void createRequest(CreateRequestRequest request, StreamObserver<CreateRequestResponse> stream)
    {
        try {
            String orderId       = UlidCreator.getUlid().toString();
            String customerId    = request.getCustomerId();
            String serviceType   = request.getServiceType();
            String priority      = request.getPriority();
            String location      = request.getLocation();
            String description   = request.getDescription();
            double estimatedCost = request.getEstimatedCost();
            RequestStatus initStatus = RequestStatus.PENDING_APPROVAL;

            Order newOrder = new Order(orderId, customerId, serviceType, priority, location, description, estimatedCost, initStatus);
            db.saveOrder(newOrder);

            System.out.println("Created request: " + orderId + ", customer: " + customerId + ", service: " + serviceType);

            stream.onNext(CreateRequestResponse.newBuilder()
                    .setRequestId(orderId)
                    .setCustomerId(customerId)
                    .setServiceType(serviceType)
                    .setPriority(priority)
                    .setLocation(location)
                    .setDescription(description)
                    .setEstimatedCost(estimatedCost)
                    .setStatus(initStatus)
                    .setSuccess(true)
                    .build());
            stream.onCompleted();

        } catch (Exception e) {
            System.out.println("Error creating request: " + e.getMessage());
            stream.onNext(CreateRequestResponse.newBuilder()
                    .setSuccess(false)
                    .build());
            stream.onCompleted();
        }
    }

    @Override
    public void updateRequestStatus(UpdateRequestStatusRequest request, StreamObserver<UpdateRequestStatusResponse> stream)
    {
        try {
            String requestId = request.getRequestId();
            RequestStatus newStatus = request.getStatus();

            boolean updated = db.updateStatus(requestId, newStatus);

            if (updated) {
                System.out.println("Updated request: " + requestId + " to status: " + newStatus);
                stream.onNext(UpdateRequestStatusResponse.newBuilder()
                        .setRequestId(requestId)
                        .setNewStatus(newStatus)
                        .setSuccess(true)
                        .build());
            } else {
                System.out.println("Request not found: " + requestId);
                stream.onNext(UpdateRequestStatusResponse.newBuilder()
                        .setSuccess(false)
                        .build());
            }
            stream.onCompleted();

        } catch (Exception e) {
            System.out.println("Error updating request status: " + e.getMessage());
            stream.onNext(UpdateRequestStatusResponse.newBuilder()
                    .setSuccess(false)
                    .build());
            stream.onCompleted();
        }
    }

    @Override
    public void getRequest(GetRequestRequest request, StreamObserver<GetRequestResponse> stream)
    {
        try {
            String requestId = request.getRequestId();
            Order order = db.getOrder(requestId);

            if (order != null) {
                System.out.println("Retrieved request: " + requestId + ", status: " + order.getStatus());
                stream.onNext(GetRequestResponse.newBuilder()
                        .setRequestId(order.getOrderId())
                        .setCustomerId(order.getCustomerId())
                        .setServiceType(order.getServiceType())
                        .setPriority(order.getPriority())
                        .setLocation(order.getLocation())
                        .setDescription(order.getDescription())
                        .setEstimatedCost(order.getEstimatedCost())
                        .setStatus(order.getStatus())
                        .setSuccess(true)
                        .build());
            } else {
                System.out.println("Request not found: " + requestId);
                stream.onNext(GetRequestResponse.newBuilder()
                        .setSuccess(false)
                        .build());
            }
            stream.onCompleted();

        } catch (Exception e) {
            System.out.println("Error retrieving request: " + e.getMessage());
            stream.onNext(GetRequestResponse.newBuilder()
                    .setSuccess(false)
                    .build());
            stream.onCompleted();
        }
    }
}