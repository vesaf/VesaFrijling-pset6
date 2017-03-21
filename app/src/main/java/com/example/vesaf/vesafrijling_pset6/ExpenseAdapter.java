package com.example.vesaf.vesafrijling_pset6;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by vesaf on 3/13/2017.
 */

public class ExpenseAdapter extends ArrayAdapter {
    private ArrayList<Expense> expenses;
    private Context context;
    private OverviewActivity overviewActivity;

    // Constructor
    public ExpenseAdapter(Context context, ArrayList<Expense> data) {
        super(context,0, data);
        this.expenses = data;
        this.overviewActivity = (OverviewActivity) context;
        this.context = context;
    }

    // get the view and return it
    @Override
    public View getView(int pos, View convertView, ViewGroup parent) {
        View view = convertView;

        if (view == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.list_item, parent, false);
        }

        final Expense expense = expenses.get(pos);
        String title = expense.getTitle();
        String amount = String.valueOf(expense.getAmount());

        TextView listTv = (TextView) view.findViewById(R.id.textView4); // TV NAME!!!!!!!!!!!
        listTv.setText(title + ": " + amount);

        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                overviewActivity.deleteExpense(expense.getId());

                return true;
            }
        });

        //KEEP FOR LATER FUNCTIONALITY!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
//        view.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                overviewActivity.openList(expense.getId());
//            }
//        });

        return view;
    }

    // get the count
    @Override
    public int getCount() {
        return super.getCount();
    }

}
