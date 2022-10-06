package com.extrainch.kkvl.ui.onboarding;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

import com.extrainch.kkvl.databinding.ActivityTermsAndConditionsOnBoardingBinding;
import com.extrainch.kkvl.utils.Constants;

public class TermsAndConditionsOnB extends AppCompatActivity {

    ActivityTermsAndConditionsOnBoardingBinding binding;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTermsAndConditionsOnBoardingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        WebSettings webSettings = binding.idWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        binding.idWebView.setWebViewClient(new WebCallback());
        binding.idWebView.loadUrl(Constants.TERMS_AND_CONDITIONS);

        binding.backPressed.setOnClickListener(v ->
                startActivity(new Intent(this, SplashScreen.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK)));

    }

    private static class WebCallback extends WebViewClient {
        @Override
        public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
            return false;
        }
    }
}