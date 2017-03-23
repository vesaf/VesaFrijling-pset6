package com.example.vesaf.vesafrijling_pset6;

import android.content.Intent;
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

import org.w3c.dom.Text;

import java.util.ArrayList;

public class OverviewActivity extends AppCompatActivity {

    //TODO: afschermen voor niet ingelogden
    //TODO: TV NAME
    //TODO: twee decimalen
    //TODO: horizontal layout
    //TODO: start waar gebleven
    //TODO: variable scoping


    Double budget;
    ArrayList<Expense> expenses;
    private DatabaseReference database;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser user;
    private String userId;
    private int nextId;
    private ExpenseAdapter adapter;
    private double totalExpenses;

    private TextView expensesTv;
    private TextView differenceTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);

        // make sure keyboard never over text view
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        setTitle("Expenses");

        // get database
        database = FirebaseDatabase.getInstance().getReference();

        // setup user
        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d("", "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d("", "onAuthStateChanged:signed_out");
                }
            }
        };

        // get user ID
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            userId = user.getUid();
        }
        // if exists create new budget else setup screen
        final TextView budgetTv = (TextView) findViewById(R.id.budgetTextView);

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (!dataSnapshot.child("budgets").hasChild(userId)) {
                    Intent intent = new Intent(OverviewActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
                else {
                    // get budget
                    budget = dataSnapshot.child("budgets").child(userId).getValue(Double.class);
                    String budgetText = "Budget: €" + String.format("%.2f", budget);
                    budgetTv.setText(budgetText);

                    // get next id
                    if (dataSnapshot.child("ids").hasChild(userId)) {
                        nextId = dataSnapshot.child("ids").child(userId).getValue(int.class);
                    } else {
                        nextId = 0;
                    }


                    // get expenses
                    expenses = new ArrayList<>();
                    Iterable<DataSnapshot> snapshot
                            = dataSnapshot.child("expenses").child(userId).getChildren();

                    for (DataSnapshot expense : snapshot) {
                        expenses.add(expense.getValue(Expense.class));
                    }

                    // get total expenses and difference
                    totalExpenses = 0;
                    for (Expense expense : expenses) {
                        totalExpenses += expense.getAmount();
                    }

                    // set expenses and difference in text views
                    expensesTv = (TextView) findViewById(R.id.expensesTextView);
                    differenceTv = (TextView) findViewById(R.id.differenceTextView);
                    String expensesString = "Expenses: €"
                            + String.format("%.2f", totalExpenses);
                    String differenceString = "Money Left: €"
                            + String.format("%.2f", budget - totalExpenses);
                    expensesTv.setText(expensesString);
                    differenceTv.setText(differenceString);

                    setAdapter();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("", "Failed to read value");
            }
        };
        database.addValueEventListener(postListener);
    }

    public void addExpense (View view) {
        // define input editTexts and whether empty
        EditText amountEt = (EditText) findViewById(R.id.amountEditText);
        EditText titleEt = (EditText) findViewById(R.id.titleEditText);

        if (!amountEt.getText().toString().equals("") && !titleEt.getText().toString().equals("")) {
            // setup new expense
            Double amount  = Double.valueOf(amountEt.getText().toString());
            String title = titleEt.getText().toString();
            titleEt.setText("");
            amountEt.setText("");
            Expense expense = new Expense(title, amount, String.valueOf(nextId));

            // add new expense
            String expenseChild = "Expense" + String.valueOf(nextId);
            database.child("expenses").child(userId).child(expenseChild).setValue(expense);

            // update total expenses and difference
            totalExpenses += amount;
            String expensesString = "Expenses: €"
                    + String.format("%.2f", totalExpenses);
            String differenceString = "Money Left: €"
                    + String.format("%.2f", budget - totalExpenses);
            expensesTv.setText(expensesString);
            differenceTv.setText(differenceString);

            // update nexId
            nextId += 1;
            database.child("ids").child(userId).setValue(nextId);
        }
    }

    public void deleteExpense(String id) {
        // delete expense from database
        database.child("expenses").child(userId).child("Expense" + id).removeValue();
        // delete expense from list
        for (int i = 0; i < expenses.size(); i++) {
            if (expenses.get(i).getId().equals(id)) {

                // update total expenses and difference
                totalExpenses -= expenses.get(i).getAmount();
                String expensesString = "Expenses: €"
                        + String.format("%.2f", totalExpenses);
                String differenceString = "Money Left: €"
                        + String.format("%.2f", budget - totalExpenses);
                expensesTv.setText(expensesString);
                differenceTv.setText(differenceString);

                expenses.remove(i);
                break;
            }
        }

        setAdapter();
    }

    public void setAdapter() {
        // read expenses into list view
        adapter = new ExpenseAdapter(this, expenses);
        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(adapter);
    }

    public void changeBudget() {
        // change budget by going back to MainActivity
        Intent intent = new Intent(OverviewActivity.this, MainActivity.class);
        startActivity(intent);
    }

    public void logout() {
        // logout user and go to LoginActivity
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(OverviewActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // setup menu
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
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

    @Override
    public void onStart() {
        // setup authorization state listener
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        // stop authorization state listener
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
