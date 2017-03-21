package com.example.vesaf.vesafrijling_pset6;

import java.io.Serializable;

/**
 * Created by vesaf on 3/17/2017.
 */

public class Budget implements Serializable{
    private int amount;
    private String userId;

    public Budget() {}

    public Budget(int budgetAmount, String budgetUserId) {
        amount = budgetAmount;
        userId = budgetUserId;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int newAmount) {
        amount = newAmount;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String newUserId) {
        userId = newUserId;
    }

}
