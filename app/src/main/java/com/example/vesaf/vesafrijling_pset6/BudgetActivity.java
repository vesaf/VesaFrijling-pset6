/*
* Vesa Frijling - 10782885
* Problemset 6 - Expenses
* 24-03-2017
*
* In this activity the user is prompted to enter a new budget amount.
 */


package com.example.vesaf.vesafrijling_pset6;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class BudgetActivity extends AppCompatActivity {

    private DatabaseReference database;
    private String userId;

    /*
    * Sets up activity by setting title, getting userId and getting database.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get database
        database = FirebaseDatabase.getInstance().getReference();

        // Get user ID if logged in else go to login
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            userId = user.getUid();
        }
        else {
            Intent loginIntent = new Intent(BudgetActivity.this, LoginActivity.class);
            startActivity(loginIntent);
            finish();
        }

        setTitle("Budget");
    }

    /*
     * Save budget to database using data from editText and then send user to OverviewActivity.
     * Called when 'Save' button clicked.
     */
    public void saveBudget(View view) {
        EditText budgetEt = (EditText) findViewById(R.id.budgetEditText);

        // Make sure editText isn't empty
        if (!budgetEt.getText().toString().equals("")) {
            double amount = Double.valueOf(budgetEt.getText().toString());

            // Save new budget
            database.child("budgets").child(userId).setValue(amount);

            // Go to OverviewActivity
            Intent intent = new Intent(this, OverviewActivity.class);
            startActivity(intent);
            finish();
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

    /*
     * Make sure user always returns to OverviewActivity,
      * even if it wasn't opened in this cycle before. Otherwise if sent here from dispatch pressing
      * back will result in closing the app instead of moving to OverviewActivity as one would expect.
     */
    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        startActivity(new Intent(BudgetActivity.this, OverviewActivity.class));

    }
}
