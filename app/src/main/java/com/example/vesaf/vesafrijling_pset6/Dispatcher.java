/*
* Vesa Frijling - 10782885
* Problemset 6 - Expenses
* 24-03-2017
*
* In this activity the user is sent to the activity where he left off.
 */

package com.example.vesaf.vesafrijling_pset6;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class Dispatcher extends Activity {

    /*
     * Redirect user to last used activity.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Class<?> activityClass;

        try {
            SharedPreferences prefs = getSharedPreferences("X", MODE_PRIVATE);
            activityClass = Class.forName(
                    prefs.getString("lastActivity", OverviewActivity.class.getName()));
        } catch(ClassNotFoundException ex) {
            activityClass = OverviewActivity.class;
        }

        startActivity(new Intent(this, activityClass));
        finish();
    }
}
