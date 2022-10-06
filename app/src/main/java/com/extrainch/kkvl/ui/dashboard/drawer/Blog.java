package com.extrainch.kkvl.ui.dashboard.drawer;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

import com.extrainch.kkvl.databinding.ActivityBlogBinding;
import com.extrainch.kkvl.ui.dashboard.Dashboard_increado;
import com.extrainch.kkvl.utils.Constants;

public class Blog extends AppCompatActivity {

    ActivityBlogBinding binding;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBlogBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        WebSettings webSettings = binding.idWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        binding.idWebView.setWebViewClient(new WebCallback());
        binding.idWebView.loadUrl(Constants.BLOG);

        binding.backPressed.setOnClickListener(v ->
                startActivity(new Intent(this, Dashboard_increado.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK)));


    }

    private static class WebCallback extends WebViewClient {
        @Override
        public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
            return false;
        }
    }
}