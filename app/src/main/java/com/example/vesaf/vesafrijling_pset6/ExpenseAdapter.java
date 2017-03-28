/**
 * Vesa Frijling - 10782885
 * Problemset 6 - Expenses
 * 25-03-2017
 *
 * Process list of expenses and put them into listView with an on long click listener.
 * Also able to get amount of items in listView.
 */

package com.example.vesaf.vesafrijling_pset6;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

class ExpenseAdapter extends ArrayAdapter<Expense> {
    private ArrayList<Expense> expenses;
    private Context context;
    private OverviewActivity overviewActivity;

    /*
     *Constructor
     */
    ExpenseAdapter(Context context, ArrayList<Expense> data) {
        super(context,0, data);
        this.expenses = data;
        this.overviewActivity = (OverviewActivity) context;
        this.context = context;
    }

    /*
     * Fill ListView based on expenses given in argument.
     * Also sets on long click listener for eacht list item.
     */
    @Override
    public @NonNull View getView(int pos, View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if (view == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.list_item, parent, false);
        }

        final Expense expense = expenses.get(pos);
        String title = expense.getTitle();
        String amount = String.format("%.2f", expense.getAmount());

        TextView listTv = (TextView) view.findViewById(R.id.textView4);
        listTv.setText(title + ": €" + amount);

        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                overviewActivity.deleteExpense(expense.getId());

                return true;
            }
        });

        return view;
    }

    /*
     * Get amount of list items.
     */
    @Override
    public int getCount() {
        return super.getCount();
    }

}
