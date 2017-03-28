/*
* Vesa Frijling - 10782885
* Problemset 6 - Expenses
* 24-03-2017
*
* In this activity the user is prompted to log in or create a new account.
 */

package com.example.vesaf.vesafrijling_pset6;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    /*
    * Sets up activity and checks if a user is already logged in, if so the user is taken straight
    * to the OverviewActivity.
    */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setTitle("Login");

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        // Check user status
        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // If user is signed in send to Dispatcher
                    Intent intent = new Intent(LoginActivity.this, Dispatcher.class);
                    startActivity(intent);
                    finish();
                }
            }
        };
    }

    /*
    * Create a new user with data entered in appropriate editTexts.
     */
    public void createUser(View view) {
        // Setup signup edit texts and retrieve data
        EditText newEmailEt = (EditText) findViewById(R.id.newEmailEditText);
        EditText newPasswordEt = (EditText) findViewById(R.id.newPasswordEditText);

        final String email = newEmailEt.getText().toString();
        String password = newPasswordEt.getText().toString();

        // Make sure password not empty and longer than 5 characters
        if (email.equals("") || password.equals("")) {
            Toast.makeText(LoginActivity.this, "Authentication failed.",
                    Toast.LENGTH_SHORT).show();
        }
        else if (password.length() < 6) {
            Toast.makeText(LoginActivity.this, "Password must be at least six characters",
                    Toast.LENGTH_SHORT).show();
        }
        else {
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            // If sign in fails, display a message to the user. If sign in succeeds
                            // the auth state listener will be notified.
                            if (!task.isSuccessful()) {
                                Toast.makeText(LoginActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(LoginActivity.this, email + " signed up!",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    /*
    * Logs in user using data from appropriate editTexts.
    * Then sends the user to the OverviewActivity.
     */
    public void loginUser(View view) {
        // Setup login edit texts and retrieve data
        EditText emailEt = (EditText) findViewById(R.id.emailEditText);
        EditText passwordEt = (EditText) findViewById(R.id.passwordEditText);

        final String email = emailEt.getText().toString();
        String password = passwordEt.getText().toString();

        // Make sure no empty fields
        if (email.equals("") || password.equals("")) {
            Toast.makeText(LoginActivity.this, "Authentication failed.",
                    Toast.LENGTH_SHORT).show();
        }
        else {
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    // If sign in fails, display a message to the user. If sign in succeeds
                    // the auth state listener will be notified and user will be sent to
                    // Dispatcher.
                    if (!task.isSuccessful()) {
                        Toast.makeText(LoginActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(LoginActivity.this, email + " logged in!",
                                Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginActivity.this, Dispatcher.class);
                        startActivity(intent);
                        finish();
                    }
                }
            });
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
}
