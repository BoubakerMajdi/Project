package org.project.account;

import io.grpc.stub.StreamObserver;

public class AccountServiceImpl extends AccountServiceGrpc.AccountServiceImplBase
{
    private final AccountDatabase db;

    public AccountServiceImpl(AccountDatabase db)
    {
        this.db = db;
    }

    @Override
    public void authorizePayment(AuthorizationRequest request, StreamObserver<AuthorizationResponse> stream)
    {
        try {
            String orderId    = request.getOrderId();
            String customerId = request.getCustomerId();
            double amount     = request.getAmount();

            Account acc = db.getAccount(customerId);

            if (acc != null)
            {
                if (acc.getBalance() >= amount)
                {
                    db.updateBalance(customerId, acc.getBalance() - amount);
                    System.out.println("Payment accepted for order: " + orderId);
                    stream.onNext(AuthorizationResponse.newBuilder()
                            .setOrderId(orderId)
                            .setStatus(AuthorizationStatus.ACCEPTED)
                            .setSuccess(true)
                            .build());
                }
                else
                {
                    System.out.println("Payment rejected for order: " + orderId + ", insufficient balance");
                    stream.onNext(AuthorizationResponse.newBuilder()
                            .setOrderId(orderId)
                            .setStatus(AuthorizationStatus.REJECTED)
                            .setSuccess(true)
                            .build());
                }
            }
            else
            {
                System.out.println("Payment rejected for order: " + orderId + ", account not found");
                stream.onNext(AuthorizationResponse.newBuilder()
                        .setOrderId(orderId)
                        .setStatus(AuthorizationStatus.REJECTED)
                        .setSuccess(true)
                        .build());
            }
            stream.onCompleted();

        } catch (Exception e) {
            System.out.println("Error processing payment: " + e.getMessage());
            stream.onNext(AuthorizationResponse.newBuilder()
                    .setSuccess(false)
                    .build());
            stream.onCompleted();
        }
    }
}