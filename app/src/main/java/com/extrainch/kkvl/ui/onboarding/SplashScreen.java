package com.extrainch.kkvl.ui.onboarding;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.extrainch.kkvl.LoginActivity;
import com.extrainch.kkvl.R;
import com.extrainch.kkvl.RegistrationActivity;
import com.extrainch.kkvl.databinding.ActivityTestSlideImagesBinding;
import com.extrainch.kkvl.utils.Constants;
import com.extrainch.kkvl.utils.MyPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.NetworkInterface;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SplashScreen extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_READ_PHONE_STATE = 0;
    private ActivityTestSlideImagesBinding binding;
    private MyPreferences pref;
    private String unique_id;

    int index;
    long delay = 200;
    Handler handler = new Handler();

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

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Glide.with(this).asGif().load(R.drawable.loadunscreen).error(R.drawable.error_img).into(binding.loader);

        unique_id = android.provider.Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        pref = new MyPreferences(this);

        if (pref.getON_BOARDING().equals(true)) {
            supremeClientDetails(Constants.BASE_URL + "MobileClient/ClientDetail");
            getLocationPermission();
        }
    }

    private void getLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }
    }

    public void supremeClientDetails(String url) {
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        JSONObject jsonObject = new JSONObject();
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
        }, error -> {
            Log.d("error", error.toString());
            Log.e("error", error.toString());
            final Dialog dialog = new Dialog(SplashScreen.this);
            dialog.setContentView(R.layout.dialog_alert);
            dialog.setCancelable(false);
            Button okbtn = (Button) dialog.findViewById(R.id.dialogButtonOK);
            TextView remarks = (TextView) dialog.findViewById(R.id.tv_response_id);
            remarks.setText("Your internet is off. Kindly check your connection and try again");


            // if button is clicked, close the custom dialog
            okbtn.setOnClickListener(v -> {

//                binding.getStarted.setVisibility(View.VISIBLE);
                dialog.dismiss();
                ActivityCompat.requestPermissions(SplashScreen.this,
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

    private void requestReadPhoneStatePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_PHONE_STATE)) {


        } else {
            // READ_PHONE_STATE permission has not been granted yet. Request it directly.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE},
                    MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);
        }
    }
}