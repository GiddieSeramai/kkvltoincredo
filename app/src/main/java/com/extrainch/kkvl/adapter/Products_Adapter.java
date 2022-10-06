package com.extrainch.kkvl.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.extrainch.kkvl.R;

import java.util.ArrayList;

public class Products_Adapter extends ArrayAdapter<String> {
    private final Activity context;
    private final ArrayList<String> ac_Product;
    private final ArrayList<String> account_number;
    private final ArrayList<String> account_Bal;
    private final ArrayList<String> term_from;
    private final ArrayList<String> term_to;

    public Products_Adapter(Activity context, ArrayList<String> ac_Product, ArrayList<String> account_number, ArrayList<String> account_Bal, ArrayList<String> term_from, ArrayList<String> term_to) {
        super(context, R.layout.products_details_adapter, account_number);
        // TODO Auto-generated constructor stub
        this.context = context;
        this.ac_Product = ac_Product;
        this.account_number = account_number;
        this.account_Bal = account_Bal;
        this.term_from = term_from;
        this.term_to = term_to;
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.products_details_adapter, null, true);
        TextView tv_account_type = rowView.findViewById(R.id.tv_account_type);
        TextView tv_account_number = rowView.findViewById(R.id.tv_account_number);
        TextView tv_account_balance = rowView.findViewById(R.id.tv_bal);
        TextView t_term_from = rowView.findViewById(R.id.term_from);
        TextView tv_term_to = rowView.findViewById(R.id.term_to);

        TextView tv_status = rowView.findViewById(R.id.tv_status);
        tv_account_type.setText(ac_Product.get(position));
        tv_account_number.setText(account_number.get(position));
        tv_account_balance.setText(account_Bal.get(position));
        t_term_from.setText(term_from.get(position));
        tv_term_to.setText(term_to.get(position));

        return rowView;
    }
}
