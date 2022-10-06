package com.extrainch.kkvl.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.extrainch.kkvl.R;
import com.extrainch.kkvl.models.LoanStatement;

import java.util.List;

public class LoanStatementAdapter extends RecyclerView.Adapter<LoanStatementAdapter.MyViewHolder> {
    private List<LoanStatement> items;
    private int SELF = 100;

    public LoanStatementAdapter(List<LoanStatement> items) {
        this.items = items;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = null;
        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.loan_statement_layout_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        LoanStatement item = items.get(position);

        holder.trxDate.setText(item.getTrxDate());
        holder.trxAmount.setText(item.getTrxAmount());
        holder.trxDescription.setText(item.getTrxDescription());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView trxDate;
        public TextView trxAmount;
        public TextView trxDescription;

        public MyViewHolder(View view) {
            super(view);

//           tf = Typeface.createFromAsset(context.getAssets(), "fonts/timeless.ttf");
            trxDate = view.findViewById(R.id.trx_date);
            trxAmount = view.findViewById(R.id.trx_amount);
            trxDescription = view.findViewById(R.id.trx_description);
        }
    }
}