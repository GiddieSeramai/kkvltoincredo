package com.extrainch.kkvl.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.extrainch.kkvl.R;

import java.util.ArrayList;

public class LoanAdapter extends ArrayAdapter<String> {
    private final Activity context;
    private final ArrayList<String> ac_Product;
    private final ArrayList<String> account_number;
    private final ArrayList<String> account_Bal;

    public LoanAdapter(Activity context, ArrayList<String> ac_Product, ArrayList<String> account_number, ArrayList<String> account_Bal) {
        super(context, R.layout.account_details_adapter, account_number);
        // TODO Auto-generated constructor stub
        this.context = context;
        this.ac_Product = ac_Product;
        this.account_number = account_number;
        this.account_Bal = account_Bal;

    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.account_details_adapter, null, true);
        TextView tv_account_type = rowView.findViewById(R.id.tv_account_type);
        TextView tv_account_number = rowView.findViewById(R.id.tv_account_number);
        TextView tv_balance = rowView.findViewById(R.id.tv_balance);
        TextView tv_status = rowView.findViewById(R.id.tv_status);
        tv_account_type.setText(ac_Product.get(position));
        tv_account_number.setText(account_number.get(position));

        if (account_Bal.get(position).equalsIgnoreCase("Savings Bank")) {
            tv_status.setText(account_Bal.get(position));
        } else {
            tv_status.setText(account_Bal.get(position));
        }
        return rowView;
    }
}
