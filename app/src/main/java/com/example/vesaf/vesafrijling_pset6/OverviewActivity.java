/*
* Vesa Frijling - 10782885
* Problemset 6 - Expenses
* 24-03-2017
*
* In this activity users can view their expenses, add expenses, delete expenses, see their budget,
* see their total expenses and see how much money they have left (i.e. budget - expenses).
* It is the main activity.
 */

package com.example.vesaf.vesafrijling_pset6;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class OverviewActivity extends AppCompatActivity {

    // variable definitions
    Double budget;
    ArrayList<Expense> expenses;
    private DatabaseReference database;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private String userId;
    private int nextId;
    private double totalExpenses;

    // Set expenses and difference in text views
    private TextView expensesTv;
    private TextView differenceTv;
    private TextView budgetTv;

    /*
    * Setup activity by loading data from Firebase.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);

        // Make sure keyboard never over text view
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        setTitle("Expenses");

        expensesTv = (TextView) findViewById(R.id.expensesTextView);
        differenceTv = (TextView) findViewById(R.id.differenceTextView);
        budgetTv = (TextView) findViewById(R.id.budgetTextView);

        // Get database
        database = FirebaseDatabase.getInstance().getReference();

        // Setup user
        mAuth = FirebaseAuth.getInstance();

        // Check if logged in
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    Intent intent = new Intent(OverviewActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        };
        getData();
    }

    /*
* Retrieve data from Firebase as well as make sure a budget has been set (if not make user set
* a new budget).
* I am aware of the length, however splitting it to two listeners introduced synchronity issues.
 */
    public void getData() {
        // Get user ID
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            userId = user.getUid();
        }

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // If no budget set go to budgetActivity
                if (!dataSnapshot.child("budgets").hasChild(userId)) {
                    Intent intent = new Intent(OverviewActivity.this, BudgetActivity.class);
                    startActivity(intent);
                    finish();
                }
                // Retrieve all data
                else {
                    budget = dataSnapshot.child("budgets").child(userId).getValue(Double.class);
                    String budgetText = "Budget: €" + String.format("%.2f", budget);
                    budgetTv.setText(budgetText);

                    // Get next id
                    if (dataSnapshot.child("ids").hasChild(userId)) {
                        nextId = dataSnapshot.child("ids").child(userId).getValue(int.class);
                    } else {
                        nextId = 0;
                    }

                    // Get expenses
                    expenses = new ArrayList<>();
                    Iterable<DataSnapshot> snapshot
                            = dataSnapshot.child("expenses").child(userId).getChildren();

                    for (DataSnapshot expense : snapshot) {
                        expenses.add(expense.getValue(Expense.class));
                    }

                    // Get total expenses and difference
                    totalExpenses = 0;
                    for (Expense expense : expenses) {
                        totalExpenses += expense.getAmount();
                    }

                    updateLables();

                    setAdapter();
                }
            }

            // When data retrieval failed send toast
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Note: didn't coose a toast as this fires as well when the user logs out, so he'd
                // get a toast everytime they log out
                Log.d("ERROR", "Could not retrieve data");
            }
        };
        database.addValueEventListener(postListener);
    }

    /*
    * Used to add an expense to the in app list of expenses and the Firebase database.
    * Called when 'Add' button clicked, gets data from editTexts and saves it on Firebase
    * and in internal list.
     */
    public void addExpense (View view) {
        // Define input editTexts and whether empty
        EditText amountEt = (EditText) findViewById(R.id.amountEditText);
        EditText titleEt = (EditText) findViewById(R.id.titleEditText);

        if (!amountEt.getText().toString().equals("") && !titleEt.getText().toString().equals("")) {
            // Setup new expense
            Double amount  = Double.valueOf(amountEt.getText().toString());
            String title = titleEt.getText().toString();
            titleEt.setText("");
            amountEt.setText("");
            Expense expense = new Expense(title, amount, String.valueOf(nextId));

            // Add new expense
            String expenseChild = "Expense" + String.valueOf(nextId);
            database.child("expenses").child(userId).child(expenseChild).setValue(expense);

            // Update total expenses and difference
            totalExpenses += amount;

            // Update nexId
            nextId += 1;
            database.child("ids").child(userId).setValue(nextId);
        }
    }

    /*
    * Deletes an expense from the internal expenses list and from Firebase.
    * Called when long-pressing on an expense.
     */
    public void deleteExpense(String id) {
        // Delete expense from database
        database.child("expenses").child(userId).child("Expense" + id).removeValue();

        // Delete expense from list
        for (int i = 0; i < expenses.size(); i++) {
            if (expenses.get(i).getId().equals(id)) {
                // Update total expenses and difference
                totalExpenses -= expenses.get(i).getAmount();

                expenses.remove(i);
                break;
            }
        }
    }

    /*
    * Reload text in 'expenses' and 'money left' textViews.
    * Called when data changed.
     */
    public void updateLables() {
        String expensesString = "Expenses: €"
                + String.format("%.2f", totalExpenses);
        String differenceString = "Money Left: €"
                + String.format("%.2f", budget - totalExpenses);
        expensesTv.setText(expensesString);
        differenceTv.setText(differenceString);
    }

    /*
    * Setup ExpenseAdapter to read expenses into listView.
     */
    public void setAdapter() {
        ExpenseAdapter adapter = new ExpenseAdapter(this, expenses);
        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(adapter);
    }

    /*
    * Takes users to BudgetActivity.
    * Called when 'Change Budget' chosen in menu.
     */
    public void changeBudget() {
        Intent intent = new Intent(OverviewActivity.this, BudgetActivity.class);
        startActivity(intent);
    }

    /*
    * Logs out user and takes them back to LoginActivity.
    * Called when 'Logout' chosen in menu.
     */
    public void logout() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(OverviewActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    /*
    * Sets up menu.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    /*
    * Handles item selection from menu.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.change_budget:
                changeBudget();
                return true;
            case R.id.logout:
                logout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /*
    * Setup authorization state listener on activity start.
     */
    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    /*
    * Stop authorization state listener on activity stop.
     */
    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }


    /*
     * Store last used activity
     */
    @Override
    protected void onPause() {
        super.onPause();

        SharedPreferences prefs = getSharedPreferences("X", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("lastActivity", getClass().getName());
        editor.apply();
    }
}
