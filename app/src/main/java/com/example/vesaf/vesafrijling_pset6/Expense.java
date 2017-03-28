/**
 * Vesa Frijling - 10782885
 * Problemset 6 - Expenses
 * 25-03-2017
 *
 * Defines an expense object. The object contains: a title, an amount and an id. Also contains a
 * getter method for each variable.
 */

package com.example.vesaf.vesafrijling_pset6;

import java.io.Serializable;

class Expense implements Serializable{
    private String title;
    private double amount;
    private String id;

    /*
     * Constructor required by Firebase.
     */
    public Expense() {}

    /*
     * Constructor.
     */
    Expense(String expenseTitle, double expenseAmount, String expenseId) {
        title = expenseTitle;
        amount = expenseAmount;
        id = expenseId;
    }

    public String getTitle() {
        return title;
    }

    double getAmount() {
        return amount;
    }

    public String getId() {
        return id;
    }

}
