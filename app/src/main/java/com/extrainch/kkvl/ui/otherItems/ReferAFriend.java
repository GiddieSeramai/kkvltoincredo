package com.extrainch.kkvl.ui.otherItems;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.extrainch.kkvl.LoginActivity;
import com.extrainch.kkvl.databinding.ActivityReferAfriendBinding;

public class ReferAFriend extends AppCompatActivity {
    ActivityReferAfriendBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityReferAfriendBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.imBack.setOnClickListener(v -> startActivity(new Intent(this, LoginActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, LoginActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
    }
}