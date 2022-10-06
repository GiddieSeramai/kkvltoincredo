package com.extrainch.kkvl.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.extrainch.kkvl.R;
import com.extrainch.kkvl.models.Calculator;

import java.util.List;

public class CalculatorAdapter extends RecyclerView.Adapter<CalculatorAdapter.MyViewHolder> {
    private List<Calculator> calculatorList;

    public CalculatorAdapter(List<Calculator> calculatorList) {
        this.calculatorList = calculatorList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.calculator_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Calculator loan = calculatorList.get(position);
        holder.prnBalance.setText(loan.getPrnbalance());
        holder.prnAmount.setText(loan.getPrnAmount());
        holder.intAmount.setText(loan.getIntAmount());
        holder.instAmount.setText(loan.getInstAmount());
        //   holder.im_image.setBackgroundResource(R.mipmap.ic_hourg);
    }

    @Override
    public int getItemCount() {
        return calculatorList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView prnBalance, prnAmount, intAmount, instAmount;

        public MyViewHolder(View view) {
            super(view);
            prnBalance = view.findViewById(R.id.prn_balance);
            prnAmount = view.findViewById(R.id.prn_amount);
            intAmount = view.findViewById(R.id.int_amount);
            instAmount = view.findViewById(R.id.inst_amount);
        }
    }
}