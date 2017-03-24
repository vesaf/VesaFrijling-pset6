package com.example.vesaf.vesafrijling_pset6;

import android.content.Intent;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        database = FirebaseDatabase.getInstance().getReference();

        // get user ID if logged in else go to login
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            userId = user.getUid();
        }
        else {
            Intent intent = new Intent(BudgetActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        setTitle("Budget");
    }


    public void saveBudget(View view) {
        EditText budgetEt = (EditText) findViewById(R.id.budgetEditText);
        if (!budgetEt.getText().toString().equals("")) {
            double amount = Double.valueOf(budgetEt.getText().toString());

            database.child("budgets").child(userId).setValue(amount);


            Intent intent = new Intent(this, OverviewActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
