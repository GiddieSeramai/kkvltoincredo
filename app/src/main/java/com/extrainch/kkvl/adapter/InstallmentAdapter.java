package com.extrainch.kkvl.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.extrainch.kkvl.R;
import com.extrainch.kkvl.models.Installment;

import java.util.List;

public class InstallmentAdapter extends RecyclerView.Adapter<InstallmentAdapter.MyViewHolder> {
    private List<Installment> calculatorList;

    public InstallmentAdapter(List<Installment> calculatorList) {
        this.calculatorList = calculatorList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.installment_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Installment loan = calculatorList.get(position);
        holder.instNo.setText(loan.getInstNo());
        holder.duedate.setText(loan.getDueDate());
        holder.due_amount.setText(loan.getDueAmount());
        holder.balance_amount.setText(loan.getPaymentStatus());
    }

    @Override
    public int getItemCount() {
        return calculatorList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView instNo, duedate, due_amount, balance_amount;

        public MyViewHolder(View view) {
            super(view);
            instNo = view.findViewById(R.id.instNo);
            duedate = view.findViewById(R.id.duedate);
            due_amount = view.findViewById(R.id.due_amount);
            balance_amount = view.findViewById(R.id.balance_amount);
        }
    }
}