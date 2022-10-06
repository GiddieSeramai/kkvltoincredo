package com.extrainch.kkvl.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.extrainch.kkvl.R;
import com.extrainch.kkvl.models.Statement;

import java.util.List;

public class AccountStatementAdapter extends RecyclerView.Adapter<AccountStatementAdapter.MyViewHolder> {
    private List<Statement> items;
    private int SELF = 100;

    public AccountStatementAdapter(List<Statement> items) {
        this.items = items;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = null;
        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.statement_layout_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Statement item = items.get(position);

        holder.trxDate.setText(item.getTrxDate());
        holder.trxDescription.setText(item.getTrxDescription());
        holder.txtAmount.setText(item.getAmount());
        holder.trxRunningBalance.setText(item.getTrxRunningBalance());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView trxDate;
        public TextView trxDebit;
        public TextView txtAmount;
        public TextView trxDescription;
        public TextView trxRunningBalance;
        public TextView trxType;
        private Typeface tf;
        private Context context;

        public MyViewHolder(View view) {
            super(view);

//           tf = Typeface.createFromAsset(context.getAssets(), "fonts/timeless.ttf");
            txtAmount = view.findViewById(R.id.txt_amount);
            trxDescription = view.findViewById(R.id.trx_description);
            trxRunningBalance = view.findViewById(R.id.trx_running_balance);
            trxDate = view.findViewById(R.id.trx_date);
            trxType = view.findViewById(R.id.trx_trxType);
        }
    }
}