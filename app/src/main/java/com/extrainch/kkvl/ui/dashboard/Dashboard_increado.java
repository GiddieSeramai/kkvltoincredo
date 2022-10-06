package com.extrainch.kkvl.ui.dashboard;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.extrainch.kkvl.LoginActivity;
import com.extrainch.kkvl.R;
import com.extrainch.kkvl.databinding.ActivityDashboardIncreadoBinding;
import com.extrainch.kkvl.databinding.NavHeaderIncredoBinding;
import com.extrainch.kkvl.other.ContactUsActivity;
import com.extrainch.kkvl.other.NotificationActivity;
import com.extrainch.kkvl.ui.dashboard.drawer.Blog;
import com.extrainch.kkvl.ui.dashboard.drawer.LocateOurBranch;
import com.extrainch.kkvl.ui.dashboard.drawer.TermsAndConditions;
import com.extrainch.kkvl.utils.MyPreferences;

public class Dashboard_increado extends AppCompatActivity {
    ActivityDashboardIncreadoBinding binding;
    private MyPreferences pref;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDashboardIncreadoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        pref = new MyPreferences(getApplicationContext());
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        ImageView ivLoanCalc = findViewById(R.id.ic_loanCalc);
        ImageView ivContact = findViewById(R.id.ic_Contact);
        ImageView ivHome = findViewById(R.id.ic_Home);
        ImageView exitApplication = findViewById(R.id.exitApplication);
        ImageView notifications = findViewById(R.id.notification);
        ImageView navigationDrawer = findViewById(R.id.navigationDrawer);

        navigationDrawer.setOnClickListener(v -> binding.drawerLayout.openDrawer(GravityCompat.START));
        View header = binding.navView.getHeaderView(0);
        @NonNull NavHeaderIncredoBinding headerViewBinding = NavHeaderIncredoBinding.bind(header);
        if (pref.getProfileBase() != null) {
            byte[] bytes = Base64.decode(pref.getProfileBase(), Base64.DEFAULT);
            // Initialize bitmap
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            // set bitmap on imageView
            headerViewBinding.profileImage.setImageBitmap(bitmap);
            headerViewBinding.customerName.setText(pref.getAccountName());
        }

        notifications.setOnClickListener(v -> startActivity(new Intent(this, NotificationActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)));

        exitApplication.setOnClickListener(v -> exitApplicationMethod());

        ivLoanCalc.setOnClickListener(v -> {
            navController.navigateUp();
            ivLoanCalc.setImageResource(R.drawable.ic_incredo_calc);
            ivHome.setImageResource(R.drawable.ic_home_icom_inactive);
            ivContact.setImageResource(R.drawable.ic_incredo_contact_white);
            navController.navigate(R.id.action_HomeFragment_to_Loan_CalcFragment);
        });

        ivContact.setOnClickListener(v -> {
            navController.navigateUp();
            ivContact.setImageResource(R.drawable.ic_incredo_contact);
            ivHome.setImageResource(R.drawable.ic_home_icom_inactive);
            ivLoanCalc.setImageResource(R.drawable.ic_incredo_calc_white);
            navController.navigate(R.id.action_HomeFragment_to_ContactFragment);
        });

        ivHome.setOnClickListener(v -> {
            navController.navigateUp();
            ivLoanCalc.setImageResource(R.drawable.ic_incredo_calc_white);
            ivContact.setImageResource(R.drawable.ic_incredo_contact_white);
            ivHome.setImageResource(R.drawable.ic_home_icom_active);
            navController.navigate(R.id.action_HomeFragment);
        });
    }

    public void locateBranch(MenuItem item) {
        binding.drawerLayout.closeDrawer(GravityCompat.START);
        startActivity(new Intent(this, LocateOurBranch.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
    }

    public void termsAndConditions(MenuItem item) {
        binding.drawerLayout.closeDrawer(GravityCompat.START);
        startActivity(new Intent(this, TermsAndConditions.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    public void blog(MenuItem item) {
        binding.drawerLayout.closeDrawer(GravityCompat.START);
        startActivity(new Intent(this, Blog.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    public void contactUs(MenuItem item) {
        binding.drawerLayout.closeDrawer(GravityCompat.START);
        startActivity(new Intent(this, ContactUsActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    public void logOut(MenuItem item) {
        binding.drawerLayout.closeDrawer(GravityCompat.START);
        exitApplicationMethod();
    }

    //Exit Application.
    private void exitApplicationMethod() {
        final Dialog dialog = new Dialog(Dashboard_increado.this);
        dialog.setContentView(R.layout.dialog_logout);
        dialog.setCancelable(false);
        Button okbtn = dialog.findViewById(R.id.dialogBtnOK);
        Button cancelBtn = dialog.findViewById(R.id.dialogBtnCancel);
        TextView txtBody = dialog.findViewById(R.id.tv_response_id);
        String textBody = "Dear " + pref.getAccountName() + " are you sure you want to logout?";
        txtBody.setText(textBody);
        okbtn.setOnClickListener(this::onClick);

        cancelBtn.setOnClickListener(v -> dialog.dismiss());
        dialog.getWindow().getAttributes().windowAnimations = R.anim.dialog_in;
        dialog.show();
    }

    private void onClick(View v) {
        startActivity(new Intent(getApplicationContext(), LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
    }
}