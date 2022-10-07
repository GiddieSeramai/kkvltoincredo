package com.extrainch.kkvl.ui.onboarding;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.extrainch.kkvl.LoginActivity;
import com.extrainch.kkvl.R;
import com.extrainch.kkvl.RegistrationActivity;
import com.extrainch.kkvl.databinding.ActivityOnboardingBinding;
import com.extrainch.kkvl.utils.Constants;
import com.extrainch.kkvl.utils.MyPreferences;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OnBoardingActivity extends AppCompatActivity {
    ActivityOnboardingBinding binding;
    private int dotsCount;
    private ImageView[] dots;
    private OnBoard_Adapter mAdapter;
    MyPreferences pref;
    private String unique_id;

    int previous_pos = 0;

    ArrayList<OnBoardItem> onBoardItems = new ArrayList<>();
    private static final int MY_PERMISSIONS_REQUEST_READ_PHONE_STATE = 0;

    public static String getMacAddr() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(String.format("%02X:", b));
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception ex) {
        }

        return "02:00:00:00:00:00";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOnboardingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        pref = new MyPreferences(OnBoardingActivity.this);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        unique_id = android.provider.Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);

        if (pref.getON_BOARDING().equals(true)) {
//             Start Main Activity
            Intent intent = new Intent(this, SplashScreen.class);
            startActivity(intent);

            // Close Onboarding
            finish();
            return;
        }

        loadData();

        mAdapter = new OnBoard_Adapter(this, onBoardItems);
        binding.pagerIntroduction.setAdapter(mAdapter);
        binding.pagerIntroduction.setCurrentItem(0);
        binding.pagerIntroduction.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                // Change the current position intimation
                for (int i = 0; i < dotsCount; i++) {
                    dots[i].setImageDrawable(ContextCompat.getDrawable(OnBoardingActivity.this, R.drawable.non_selected_item_dot));
                }

                int posi = position + 1;
                if (posi == 1) {
                    binding.linearLayoutIncredoText.setVisibility(View.VISIBLE);
                } else {
                    binding.linearLayoutIncredoText.setVisibility(View.GONE);
                }

                dots[position].setImageDrawable(ContextCompat.getDrawable(OnBoardingActivity.this, R.drawable.selected_item_dot));
                int pos = position + 1;
                if (pos == dotsCount && previous_pos == (dotsCount - 1))
                    show_animation();
                else if (pos == (dotsCount - 1) && previous_pos == dotsCount)
                    hide_animation();
                previous_pos = pos;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        binding.menuIconOpenBottomSlideCheet.setOnClickListener(v -> {
            showBottomSheetView();
        });

        binding.getStarted.setOnClickListener(v -> {
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_PHONE_STATE)
                    != PackageManager.PERMISSION_GRANTED) {
                // READ_PHONE_STATE permission has not been granted.
                buildclientdetails(R.style.DialogAnimation_1, "Left - Right Animation!");
            } else {
                supremeClientDetails(Constants.BASE_URL + "MobileClient/ClientDetail");
            }


//            Intent intent = new Intent(OnBoardingActivity.this, SignUpActivity.class);
//            startActivity(intent);
        });

        setUiPageViewController();
        getLocationPermission();
    }


    private void getLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }
    }

    private void buildclientdetails(int animationSource, String type) {

        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_alert);
        dialog.setCancelable(false);
        Button okbtn = (Button) dialog.findViewById(R.id.dialogButtonOK);
        TextView remarks = (TextView) dialog.findViewById(R.id.tv_response_id);
        remarks.setText("Dear customer, to enhance your experience, kindly allow phone permissions");


        // if button is clicked, close the custom dialog
        okbtn.setOnClickListener(v -> {
            dialog.dismiss();
            requestReadPhoneStatePermission();
        });

        dialog.getWindow().getAttributes().windowAnimations = animationSource;
        dialog.show();
    }

    private void requestReadPhoneStatePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_PHONE_STATE)) {
        } else {
            // READ_PHONE_STATE permission has not been granted yet. Request it directly.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE},
                    MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);
        }
    }

    public void supremeClientDetails(String url) {
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        JSONObject jsonObject = new JSONObject();
        binding.spLoading.setVisibility(View.VISIBLE);
        binding.getStarted.setVisibility(View.GONE);
        try {
            jsonObject.put("NationalID", pref.getNationalID());
            jsonObject.put("ClientID", pref.getClientID());
            jsonObject.put("PhoneNumber", pref.getPhoneNumber());
            jsonObject.put("DeviceID", unique_id);
            jsonObject.put("DeviceName", Build.MANUFACTURER);
            jsonObject.put("Imei", unique_id);
            jsonObject.put("MacAddress", getMacAddr());

            Log.d("VERIFYCODE", "CC" + jsonObject.toString());
        } catch (final JSONException e) {
            e.printStackTrace();

        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(com.android.volley.Request.Method.POST, url, jsonObject, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                Log.d("LLL", response.toString());
                // progress.dismiss();
                try {
                    Log.d("LLL", response.toString());

                    JSONObject object = new JSONObject(response.toString());

                    binding.spLoading.setVisibility(View.GONE);
                    if (object.getString("code").equals("200")) {
                        finishTutorial();
                        JSONObject data = new JSONObject(object.getString("data"));

                        pref.setClientID(data.getString("clientID"));
                        pref.setODSAccID(data.getString("mbdAccountID"));
                        pref.setPhoneNumber(data.getString("phoneNumber"));
                        pref.setLoanAccountID(data.getString("loanAccountID"));
                        pref.setAccountName(data.getString("name"));
                        pref.setAccountReminder(data.getString("reminder"));
                        pref.setMBDAccountID(data.getString("accountID"));
                        pref.setAccID(data.getString("accountID"));
                        //   pref.setPhotoID(object.getString("Photo"));
                        pref.setPhotoID("");
                        startActivity(new Intent(getApplicationContext(), LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        finish();
                        // new updateLoginstatus().execute(Constants.LOGIN_URL + "UpdateLoginStatus");

                    } else {
                        startActivity(new Intent(getApplicationContext(), RegistrationActivity.class));
                        finish();
                    }
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), Log.getStackTraceString(e) + "", Toast.LENGTH_LONG).show();
                    Log.i(TAG, Log.getStackTraceString(e));
                }
            }


        }, error -> {
            Log.d("error", error.toString());
            Log.e("error", error.toString());
            binding.spLoading.setVisibility(View.GONE);
            final Dialog dialog = new Dialog(OnBoardingActivity.this);
            dialog.setContentView(R.layout.dialog_alert);
            dialog.setCancelable(false);
            Button okbtn = (Button) dialog.findViewById(R.id.dialogButtonOK);
            TextView remarks = (TextView) dialog.findViewById(R.id.tv_response_id);
            remarks.setText("Your internet is off. Kindly check your connection and try again");


            // if button is clicked, close the custom dialog
            okbtn.setOnClickListener(v -> {

                binding.getStarted.setVisibility(View.VISIBLE);
                dialog.dismiss();
                ActivityCompat.requestPermissions(OnBoardingActivity.this,
                        new String[]{Manifest.permission.READ_PHONE_STATE},
                        MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);
            });

            dialog.show();

            //     progress.dismiss();
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json");
                return params;
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };
        jsonObjectRequest.setRetryPolicy(new

                DefaultRetryPolicy(
                60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(jsonObjectRequest);
    }

    // Load data into the viewpager
    public void loadData() {
//        int[] header = {R.string.ob_header1, R.string.ob_header2, R.string.ob_header3};
//        int[] desc = {" ", " ", " ", ""};
        int[] imageId = {R.drawable.incredo_slider_four, R.drawable.incredo_slider_one, R.drawable.incredo_slider_two, R.drawable.incredo_slider_three};

        for (int i = 0; i < imageId.length; i++) {
            OnBoardItem item = new OnBoardItem();
            item.setImageID(imageId[i]);
//            item.setTitle(getResources().getString(header[i]));
//            item.setDescription(getResources().getString(desc[i]));
            onBoardItems.add(item);
        }
    }

    // Button bottomUp animation
    public void show_animation() {
        Animation show = AnimationUtils.loadAnimation(this, R.anim.slide_up_anim);
        binding.getStarted.startAnimation(show);
        show.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                binding.getStarted.setVisibility(View.VISIBLE);
                binding.viewPagerCountDots.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                binding.getStarted.clearAnimation();
            }
        });
    }

    // Button Topdown animation
    public void hide_animation() {
        Animation hide = AnimationUtils.loadAnimation(this, R.anim.slide_down_anim);
        binding.getStarted.startAnimation(hide);
        hide.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                binding.getStarted.clearAnimation();
                binding.getStarted.setVisibility(View.GONE);
                binding.viewPagerCountDots.setVisibility(View.VISIBLE);
                binding.linearLayoutIncredoText.setVisibility(View.GONE);
            }
        });
    }

    // setup the
    private void setUiPageViewController() {
        dotsCount = mAdapter.getCount();
        dots = new ImageView[dotsCount];

        for (int i = 0; i < dotsCount; i++) {
            dots[i] = new ImageView(this);
            dots[i].setImageDrawable(ContextCompat.getDrawable(OnBoardingActivity.this, R.drawable.non_selected_item_dot));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );

            params.setMargins(6, 0, 6, 0);
            binding.viewPagerCountDots.addView(dots[i], params);
        }
        dots[0].setImageDrawable(ContextCompat.getDrawable(OnBoardingActivity.this, R.drawable.selected_item_dot));
    }

    private void showBottomSheetView() {

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);
        View bottomSheetView = LayoutInflater.from(getApplicationContext()).inflate(
                R.layout.activity_slide_panel, (LinearLayout) findViewById(R.id.slideupContainer)
        );

        bottomSheetView.findViewById(R.id.registerWelcomeActivity).setOnClickListener(view -> {
            bottomSheetDialog.cancel();
            startActivity(new Intent(getApplicationContext(), RegistrationActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        });

        bottomSheetView.findViewById(R.id.termsAndConditions).setOnClickListener(view -> {
            bottomSheetDialog.cancel();
            startActivity(new Intent(getApplicationContext(), TermsAndConditionsOnB.class)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
        });

        bottomSheetView.findViewById(R.id.cancelWelcomeActivity).setOnClickListener(view -> {
            bottomSheetDialog.cancel();
        });


        //Show Bottom Sheet
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
        bottomSheetDialog.setCanceledOnTouchOutside(false);
    }

    public void finishTutorial() {
        // Set onboarding_complete to true
        pref.setON_BOARDING(true);

//         Launch the main Activity, called MainActivity
        Intent main = new Intent(this, LoginActivity.class);
        startActivity(main);

        // Close the OnboardingActivity
        finish();
    }
}