package org.project.kitchen;

import io.grpc.stub.StreamObserver;

public class KitchenServiceImpl extends KitchenServiceGrpc.KitchenServiceImplBase
{
    private final KitchenDatabase db;

    public KitchenServiceImpl(KitchenDatabase db)
    {
        this.db = db;
    }

    @Override
    public void createTicket(CreateTicketRequest request, StreamObserver<CreateTicketResponse> stream)
    {
        try {
            String ticketId   = request.getTicketId();
            String customerId = request.getCustomerId();

            Ticket newTicket = new Ticket(ticketId, customerId, TicketStatus.PENDING_APPROVAL);
            db.saveTicket(newTicket);

            System.out.println("Created ticket: " + ticketId + ", customer: " + customerId);

            stream.onNext(CreateTicketResponse.newBuilder()
                    .setTicketId(ticketId)
                    .setCustomerId(customerId)
                    .setSuccess(true)
                    .build());
            stream.onCompleted();

        } catch (Exception e) {
            System.err.println("Error creating ticket: " + e.getMessage());
            stream.onNext(CreateTicketResponse.newBuilder()
                    .setSuccess(false)
                    .build());
            stream.onCompleted();
        }
    }

    @Override
    public void acceptTicket(AcceptTicketRequest request, StreamObserver<AcceptTicketResponse> stream)
    {
        try {
            String ticketId = request.getTicketId();
            boolean updated = db.updateStatus(ticketId, TicketStatus.ACCEPTED);

            if (updated) {
                System.out.println("Accepted ticket: " + ticketId);
            } else {
                System.out.println("Ticket not found: " + ticketId);
            }

            stream.onNext(AcceptTicketResponse.newBuilder()
                    .setAcknowledgement(updated)
                    .build());
            stream.onCompleted();

        } catch (Exception e) {
            System.err.println("Error accepting ticket: " + e.getMessage());
            stream.onNext(AcceptTicketResponse.newBuilder()
                    .setAcknowledgement(false)
                    .build());
            stream.onCompleted();
        }
    }

    @Override
    public void rejectTicket(RejectTicketRequest request, StreamObserver<RejectTicketResponse> stream)
    {
        try {
            String ticketId = request.getTicketId();
            boolean updated = db.updateStatus(ticketId, TicketStatus.REJECTED);

            if (updated) {
                System.out.println("Rejected ticket: " + ticketId);
            } else {
                System.out.println("Ticket not found: " + ticketId);
            }

            stream.onNext(RejectTicketResponse.newBuilder()
                    .setAcknowledgement(updated)
                    .build());
            stream.onCompleted();

        } catch (Exception e) {
            System.err.println("Error rejecting ticket: " + e.getMessage());
            stream.onNext(RejectTicketResponse.newBuilder()
                    .setAcknowledgement(false)
                    .build());
            stream.onCompleted();
        }
    }

    @Override
    public void cancelTicket(CancelTicketRequest request, StreamObserver<CancelTicketResponse> stream)
    {
        try {
            String ticketId = request.getTicketId();
            boolean updated = db.updateStatus(ticketId, TicketStatus.CANCELED);

            if (updated) {
                System.out.println("Cancelled ticket: " + ticketId);
            } else {
                System.out.println("Ticket not found: " + ticketId);
            }

            stream.onNext(CancelTicketResponse.newBuilder()
                    .setAcknowledgement(updated)
                    .build());
            stream.onCompleted();

        } catch (Exception e) {
            System.err.println("Error cancelling ticket: " + e.getMessage());
            stream.onNext(CancelTicketResponse.newBuilder()
                    .setAcknowledgement(false)
                    .build());
            stream.onCompleted();
        }
    }
}