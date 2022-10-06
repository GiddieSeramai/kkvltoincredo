package com.extrainch.kkvl.ui.onboarding;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.extrainch.kkvl.LoginActivity;
import com.extrainch.kkvl.R;
import com.extrainch.kkvl.RegistrationActivity;
import com.extrainch.kkvl.databinding.ActivityTestSlideImagesBinding;
import com.extrainch.kkvl.utils.Constants;
import com.extrainch.kkvl.utils.MyPreferences;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.NetworkInterface;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class SplashScreen extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_READ_PHONE_STATE = 0;
    private ActivityTestSlideImagesBinding binding;
    private MyViewPagerAdapter myViewPagerAdapter;
    private TextView[] dots;
    private int[] layouts;
    ViewPager.OnPageChangeListener viewPagerPageChangeListener =
            new ViewPager.OnPageChangeListener() {

                @Override
                public void onPageSelected(int position) {
                    addBottomDots(position);

                    // changing the next button text 'NEXT' / 'GOT IT'
                    if (position == layouts.length - 1) {
                        // last page. make button text to GOT IT
                        binding.sliderNext.setVisibility(View.GONE);
                        binding.getStarted.setVisibility(View.VISIBLE);
                        binding.linearLayoutIncredoText.setVisibility(View.VISIBLE);

                    } else {
                        // still pages are left
                        binding.sliderNext.setVisibility(View.VISIBLE);
                        binding.getStarted.setVisibility(View.GONE);
                        binding.linearLayoutIncredoText.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onPageScrolled(int arg0, float arg1, int arg2) {
                }

                @Override
                public void onPageScrollStateChanged(int arg0) {
                }
            };
    private MyPreferences pref;
    private String unique_id;

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
        binding = ActivityTestSlideImagesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        unique_id = android.provider.Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        pref = new MyPreferences(this);

        //Check If Application is Launched for the first time or not.
        if (pref.getAppLaunchState() != null) {
            pref.setAppLaunchState("second_time");
        } else {
            pref.setAppLaunchState("first_time");
        }

        if (pref.getAppLaunchState().equals("second_time")) {
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_PHONE_STATE)
                    != PackageManager.PERMISSION_GRANTED) {
                // READ_PHONE_STATE permission has not been granted.

                buildclientdetails(R.style.DialogAnimation_1, "Left - Right Animation!");
            } else {

                supremeClientDetails(Constants.BASE_URL + "MobileClient/ClientDetail");
            }
        }

        layouts = new int[]{
                R.layout.slider_one,
                R.layout.slider_two,
                R.layout.slider_three,
                R.layout.slider_four
        };

        // adding bottom dots
        addBottomDots(0);

        myViewPagerAdapter = new MyViewPagerAdapter();
        binding.viewPager.setAdapter(myViewPagerAdapter);
        binding.viewPager.addOnPageChangeListener(viewPagerPageChangeListener);

        binding.sliderNext.setOnClickListener(v -> {
            // checking for last page
            // if last page home screen will be launched
            int current = getItem();
            if (current < layouts.length) {
                // move to next screen
                binding.viewPager.setCurrentItem(current);
            }
        });

        autoSwipeViewPager();


        binding.menuIconOpenBottomSlideCheet.setOnClickListener(v -> {
            showBottomSheetView();

        });


        binding.getStarted.setOnClickListener(view -> {

            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_PHONE_STATE)
                    != PackageManager.PERMISSION_GRANTED) {
                // READ_PHONE_STATE permission has not been granted.

                buildclientdetails(R.style.DialogAnimation_1, "Left - Right Animation!");
            } else {

                supremeClientDetails(Constants.BASE_URL + "MobileClient/ClientDetail");
            }
        });

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

    private int getItem() {
        return binding.viewPager.getCurrentItem() + 1;
    }

    public void autoSwipeViewPager() {
        final Handler handler = new Handler();
        final Runnable Update = () -> {
            int currentPage = binding.viewPager.getCurrentItem();

            if (currentPage == layouts.length - 1) {
                currentPage = -1;
            }

            binding.viewPager.setCurrentItem(currentPage + 1, true);
        };

        Timer swipeTimer = new Timer();
        swipeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(Update);
            }
        }, 5000, 9000);
    }

    private void addBottomDots(int currentPage) {
        dots = new TextView[layouts.length];

        int[] colorsActive = getResources().getIntArray(R.array.array_dot_active);
        int[] colorsInactive = getResources().getIntArray(R.array.array_dot_inactive);

        binding.layoutDots.removeAllViews();
        for (int i = 0; i < dots.length; i++) {

            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(colorsInactive[currentPage]);
            binding.layoutDots.addView(dots[i]);
        }

        if (dots.length > 0)
            dots[currentPage].setTextColor(colorsActive[currentPage]);
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
                        {

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
                            startActivity(new Intent(getApplicationContext(), LoginActivity.class)
                                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                            finish();
                            // new updateLoginstatus().execute(Constants.LOGIN_URL + "UpdateLoginStatus");
                        }
                    } else {


                        startActivity(new Intent(getApplicationContext(), RegistrationActivity.class));
                        finish();
                    }

                } catch (Exception e) {

                    Toast.makeText(getApplicationContext(), Log.getStackTraceString(e) + "",
                            Toast.LENGTH_LONG).show();
                    Log.i(TAG, Log.getStackTraceString(e));
                }
            }


        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("error", error.toString());
                Log.e("error", error.toString());
                binding.spLoading.setVisibility(View.GONE);
                final Dialog dialog = new Dialog(SplashScreen.this);
                dialog.setContentView(R.layout.dialog_alert);
                dialog.setCancelable(false);
                Button okbtn = (Button) dialog.findViewById(R.id.dialogButtonOK);
                TextView remarks = (TextView) dialog.findViewById(R.id.tv_response_id);
                remarks.setText("Your internet is off. Kindly check your connection and try again");


                // if button is clicked, close the custom dialog
                okbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        binding.getStarted.setVisibility(View.VISIBLE);
                        dialog.dismiss();
                        ActivityCompat.requestPermissions(SplashScreen.this,
                                new String[]{Manifest.permission.READ_PHONE_STATE},
                                MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);
                    }
                });

                dialog.show();

                //     progress.dismiss();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
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

    private void buildclientdetails(int animationSource, String type) {

        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_alert);
        dialog.setCancelable(false);
        Button okbtn = (Button) dialog.findViewById(R.id.dialogButtonOK);
        TextView remarks = (TextView) dialog.findViewById(R.id.tv_response_id);
        remarks.setText("Dear customer, to enhance your experience, kindly allow phone permissions");


        // if button is clicked, close the custom dialog
        okbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                requestReadPhoneStatePermission();
            }
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

    public class MyViewPagerAdapter extends PagerAdapter {

        public MyViewPagerAdapter() {
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View view = layoutInflater.inflate(layouts[position], container, false);
            container.addView(view);

            return view;
        }

        @Override
        public int getCount() {
            return layouts.length;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object obj) {
            return view == obj;
        }


        @Override
        public void destroyItem(ViewGroup container, int position, @NonNull Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }

}