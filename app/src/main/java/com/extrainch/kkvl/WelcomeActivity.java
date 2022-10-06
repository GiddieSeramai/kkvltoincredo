package com.extrainch.kkvl;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.extrainch.kkvl.databinding.ActivityWelcomeBinding;
import com.extrainch.kkvl.utils.Constants;
import com.extrainch.kkvl.utils.MyPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.NetworkInterface;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WelcomeActivity extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_READ_PHONE_STATE = 0;
    static CountDownTimer counter;
    private static CountDownTimer INSTANCE;
    final Context context = this;
    ActivityWelcomeBinding binding;
    String TAG = "SplashActivity";
    String internetConnected;
    MyPreferences pref;
    String unique_id;

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
            ex.printStackTrace();
        }

        return "02:00:00:00:00:00";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWelcomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        pref = new MyPreferences(WelcomeActivity.this);

        unique_id = android.provider.Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);

        pref.setClientID("");
        boolean isInternetconnected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {

        } else {
            Handler handler = new Handler();
            handler.postDelayed(() -> {
                pref.setNotification("Dear customer, there seems to be an issue with your connection. Kindly connect to a network and retry");
                //notificationDialog(R.style.DialogAnimation_1, "Left - Right Animation!");
                //       Toast.makeText(getApplicationContext(), "Here", Toast.LENGTH_LONG).show();
            }, 3000); // 3000 milliseconds delay

        }

        binding.txtGetStarted.setOnClickListener(v -> {
            // if(pref.getClientID().length()>3) {
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_PHONE_STATE)
                    != PackageManager.PERMISSION_GRANTED) {
                // READ_PHONE_STATE permission has not been granted.
                buildclientdetails(R.style.DialogAnimation_1, "Left - Right Animation!");
            } else {
                supremeClientDetails(Constants.BASE_URL + "MobileClient/ClientDetail");
            }
        });
    }

    public void supremeClientDetails(String url) {
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        JSONObject jsonObject = new JSONObject();
        binding.spLoading.setVisibility(View.VISIBLE);
        try {
            jsonObject.put("NationalID", pref.getNationalID());
            jsonObject.put("ClientID", pref.getClientID());
            jsonObject.put("PhoneNumber", pref.getPhoneNumber());
            jsonObject.put("DeviceID", unique_id);
            jsonObject.put("DeviceName", Build.MANUFACTURER);
            jsonObject.put("Imei", unique_id);
            jsonObject.put("MacAddress", getMacAddr());

            Log.d("VERIFYCODE", "CC" + jsonObject);
        } catch (final JSONException e) {
            e.printStackTrace();

        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(com.android.volley.Request.Method.POST, url, jsonObject, response -> {
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
                        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                        finish();
                        // new updateLoginstatus().execute(Constants.LOGIN_URL + "UpdateLoginStatus");
                    }
                } else {
                    startActivity(new Intent(getApplicationContext(), RegistrationActivity.class));
                    finish();
                }
            } catch (Exception e) {
                Toast.makeText(WelcomeActivity.this, Log.getStackTraceString(e) + "", Toast.LENGTH_LONG).show();
                Log.i(TAG, Log.getStackTraceString(e));
            }
        }, error -> {
            Log.d("error", error.toString());
            Log.e("error", error.toString());
            binding.spLoading.setVisibility(View.GONE);
            final Dialog dialog = new Dialog(context);
            dialog.setContentView(R.layout.dialog_alert);
            dialog.setCancelable(false);
            Button okbtn = dialog.findViewById(R.id.dialogButtonOK);
            TextView remarks = dialog.findViewById(R.id.tv_response_id);
            remarks.setText("Your internet is off. Kindly check your connection and try again");


            // if button is clicked, close the custom dialog
            okbtn.setOnClickListener(v -> {
                dialog.dismiss();
                ActivityCompat.requestPermissions(WelcomeActivity.this,
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
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(60000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(jsonObjectRequest);
    }

    public String getDeviceIMEI() {
        String deviceUniqueIdentifier = null;
        TelephonyManager tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        if (null != tm) {
            deviceUniqueIdentifier = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        }
        if (null == deviceUniqueIdentifier || 0 == deviceUniqueIdentifier.length()) {
            deviceUniqueIdentifier = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        }
        return deviceUniqueIdentifier;
    }


    private void requestReadPhoneStatePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_PHONE_STATE)) {
        } else {
            // READ_PHONE_STATE permission has not been granted yet. Request it directly.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_READ_PHONE_STATE) {
            // Received permission result for READ_PHONE_STATE permission.est.");
            // Check if the only required permission has been granted
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            }
        } else {
            buildclientdetails(R.style.DialogAnimation_1, "Left - Right Animation!");
        }
    }

    private void buildclientdetails(int animationSource, String type) {
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_alert);
        dialog.setCancelable(false);
        Button okbtn = dialog.findViewById(R.id.dialogButtonOK);
        TextView remarks = dialog.findViewById(R.id.tv_response_id);
        remarks.setText("Dear customer, to enhance your experience, kindly allow phone permissions");

        // if button is clicked, close the custom dialog
        okbtn.setOnClickListener(v -> {
            dialog.dismiss();
            requestReadPhoneStatePermission();
        });

        dialog.getWindow().getAttributes().windowAnimations = animationSource;
        dialog.show();
    }
}

