package org.project.kitchen;

public class Ticket
{
    private final String ticketId;
    private final String customerId;
    private TicketStatus status;

    public Ticket (String ticketId, String CustomerId, TicketStatus stat)
    {
        this.ticketId = ticketId;
        this.customerId = CustomerId;
        this.status = stat;
    }

    public String getTicketId ()
    {
        return this.ticketId;
    }

    public String getCustomerId ()
    {
        return this.customerId;
    }

    public TicketStatus getStatus ()
    {
        return this.status;
    }

    public void setStatus (TicketStatus newStat)
    {
        this.status = newStat;
    }
}
