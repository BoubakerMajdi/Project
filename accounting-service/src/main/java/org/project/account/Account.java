package org.project.account;

public class Account
{
    private final String customerId;
    private double balance;

    public Account (String id, double val)
    {
        this.customerId = id;
        this.balance = val;
    }

    public String getCustomerId ()
    {
        return this.customerId;
    }

    public double getBalance ()
    {
        return this.balance;
    }

    public void setBalance (double val)
    {
        this.balance = val;
    }
}
