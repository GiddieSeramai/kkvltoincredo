package com.extrainch.kkvl.mpesa;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.extrainch.kkvl.R;
import com.extrainch.kkvl.databinding.ActivityMpesaWithdrawalBinding;
import com.extrainch.kkvl.ui.dashboard.Dashboard_increado;
import com.extrainch.kkvl.utils.MyPreferences;

import java.util.ArrayList;

public class MpesaWithdrawal extends AppCompatActivity {
    ActivityMpesaWithdrawalBinding binding;
    MyPreferences pref;
    EditText et_amount;
    Spinner sp_from_account;
    ImageView im_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMpesaWithdrawalBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        pref = new MyPreferences(MpesaWithdrawal.this);

        pref = new MyPreferences(MpesaWithdrawal.this);
        et_amount = findViewById(R.id.et_amount);

        et_amount.setText(pref.getLoanAmount());
        sp_from_account = findViewById(R.id.sp_from_account_regular);
        ArrayList odsaccounts_arraylist = new ArrayList();
        odsaccounts_arraylist.add(pref.getOdsaccountId());

        ArrayAdapter<String> adapter = new ArrayAdapter<>(MpesaWithdrawal.this, android.R.layout.simple_spinner_item, odsaccounts_arraylist);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_from_account.setAdapter(adapter);

        binding.backPressed.setOnClickListener(v -> startActivity(new Intent(this, Dashboard_increado.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)));
    }


}