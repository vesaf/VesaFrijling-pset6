package com.example.vesaf.vesafrijling_pset6;

import java.io.Serializable;

/**
 * Created by vesaf on 3/17/2017.
 */

public class Expense implements Serializable{
    private String title;
    private double amount;
    private String id;

    public Expense() {}

    public Expense(String expenseTitle, double expenseAmount, String expenseId) {
        title = expenseTitle;
        amount = expenseAmount;
        id = expenseId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String newTitle) {
        title = newTitle;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double newAmount) {
        amount = newAmount;
    }

    public String getId() {
        return id;
    }

    public void setId(String newId) {
        id = newId;
    }

}
