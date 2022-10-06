package com.extrainch.kkvl;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.extrainch.kkvl.ui.dashboard.Dashboard_increado;
import com.extrainch.kkvl.ui.otherItems.ReferAFriend;
import com.extrainch.kkvl.utils.Constants;
import com.extrainch.kkvl.utils.MyPreferences;
import com.github.ybq.android.spinkit.SpinKitView;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.NetworkInterface;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    final Context context = this;
    Context mAppContext;
    // Button buttonOne, buttonTwo, buttonThree, buttonFour, buttonFive, buttonSix, buttonSeven, buttonEight, buttonNine, buttonCancel, buttonZero;
    TextView btnOne, btnTwo, btnThree, btnFour, btnFive, btnSix, btnSeven, btnEight, btnNine, btnZero, txtClear, txtLogin;
    String password;
    MyPreferences pref;
    String TAG = "LoginActivity";
    String TAG_DETAILS = "LoginActivityDetails";
    TextView edtPassword;
    TextView txtAttempts, txtRemarks, txtForgotPassword;
    LinearLayout lnView;
    Button loginBtn;
    int count = 0;
    int remains = 3;
    int balance;
    String bal;
    String loginStatus;
    String unique_id;
    Button txtCancel, txtOk;
    SpinKitView sp_login;
    private ProgressDialog progress;

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

    public static String getDeviceId(Context context) {

        String deviceId;

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        } else {
            final TelephonyManager mTelephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (mTelephony.getDeviceId() != null) {
                deviceId = mTelephony.getDeviceId();
            } else {
                deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            }
        }
        return deviceId;
    }

    @SuppressLint("HardwareIds")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        pref = new MyPreferences(LoginActivity.this);
        progress = new ProgressDialog(LoginActivity.this);


        sp_login = findViewById(R.id.sp_login);
        lnView = (LinearLayout) findViewById(R.id.ln_view);
        txtAttempts = (TextView) findViewById(R.id.txtAttempts);
        txtRemarks = (TextView) findViewById(R.id.txtRemarks);
        unique_id = android.provider.Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);


        txtOk = (Button) findViewById(R.id.txtOk);
        txtCancel = (Button) findViewById(R.id.imgCancel);
        btnOne = (Button) findViewById(R.id.button_one);
        btnTwo = (Button) findViewById(R.id.button_two);
        btnThree = (Button) findViewById(R.id.button_three);
        btnFour = (Button) findViewById(R.id.button_four);
        btnFive = (Button) findViewById(R.id.button_five);
        btnSix = (Button) findViewById(R.id.button_six);
        btnSeven = (Button) findViewById(R.id.button_seven);
        btnEight = (Button) findViewById(R.id.button_eight);
        btnNine = (Button) findViewById(R.id.button_nine);
        btnZero = (Button) findViewById(R.id.button_zero);

        edtPassword = (TextView) findViewById(R.id.edtPassword);
        txtForgotPassword = (TextView) findViewById(R.id.txtForgotPassword);


        supremeClientDetails(Constants.BASE_URL + "MobileClient/ClientDetail");

        btnOne.setOnClickListener(v -> {
            edtPassword.setText(edtPassword.getText() + "1");
            // CheckMaxLength();
        });

        btnTwo.setOnClickListener(v -> {
            edtPassword.setText(edtPassword.getText() + "2");
            //  CheckMaxLength();
        });

        btnThree.setOnClickListener(v -> {
            edtPassword.setText(edtPassword.getText() + "3");
            //CheckMaxLength();
        });

        btnFour.setOnClickListener(v -> {
            edtPassword.setText(edtPassword.getText() + "4");
            //   CheckMaxLength();
        });

        btnFive.setOnClickListener(v -> {
            edtPassword.setText(edtPassword.getText() + "5");
            //   CheckMaxLength();
        });

        btnSix.setOnClickListener(v -> {
            edtPassword.setText(edtPassword.getText() + "6");
            //  CheckMaxLength();
        });

        btnSeven.setOnClickListener(v -> {
            edtPassword.setText(edtPassword.getText() + "7");
            //   CheckMaxLength();
        });

        btnEight.setOnClickListener(v -> {
            edtPassword.setText(edtPassword.getText() + "8");
            //  CheckMaxLength();
        });

        btnNine.setOnClickListener(v -> {
            edtPassword.setText(edtPassword.getText() + "9");
            //   CheckMaxLength();
        });

        btnZero.setOnClickListener(v -> {
            edtPassword.setText(edtPassword.getText() + "0");
            //  CheckMaxLength();
        });

        txtCancel.setOnClickListener(v -> {
            String str = edtPassword.getText().toString();
            if (str.length() > 1) {
                str = str.substring(0, str.length() - 1);
                edtPassword.setText(str);
            } else if (str.length() <= 1) {
                edtPassword.setText("");
            }
        });
        txtOk.setOnClickListener(v -> {
            sp_login.setVisibility(View.VISIBLE);
            password = edtPassword.getText().toString();
            supremeClient(Constants.BASE_URL + "Security/ClientLogin");
        });

        Button button = findViewById(R.id.buttonSignup);
        button.setOnClickListener(v -> startActivity(new Intent(this, ReferAFriend.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)));
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
            jsonObject.put("Imei", getDeviceIMEI());
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
                        // new updateLoginstatus().execute(Constants.LOGIN_URL + "UpdateLoginStatus");
                    }
                }
            } catch (Exception e) {
                Toast.makeText(LoginActivity.this, Log.getStackTraceString(e) + "", Toast.LENGTH_LONG).show();
                Log.i(TAG, Log.getStackTraceString(e));
            }
        }, error -> {
            Log.e("error", error.toString());
            final Dialog dialog = new Dialog(context);
            dialog.setContentView(R.layout.dialog_alert);
            dialog.setCancelable(false);
            Button okbtn = (Button) dialog.findViewById(R.id.dialogButtonOK);
            TextView remarks = (TextView) dialog.findViewById(R.id.tv_response_id);
            remarks.setText("Your internet is off. Kindly check your connection and try again");

            // if button is clicked, close the custom dialog
            okbtn.setOnClickListener(v -> dialog.dismiss());
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

    private void buildLogoutDialog(int animationSource, String type) {
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_logout);
        dialog.setCancelable(false);
        Button okbtn = (Button) dialog.findViewById(R.id.dialogBtnOK);
        Button cancelBtn = (Button) dialog.findViewById(R.id.dialogBtnCancel);
        final TextView txtResponse = dialog.findViewById(R.id.tv_response_id);
        txtResponse.setText("Are you sure you want to exit the app?");
        // if button is clicked, close the custom dialog
        okbtn.setOnClickListener(v -> {
            pref.setAuthToken("");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                finishAffinity();
            }
        });
        cancelBtn.setOnClickListener(v -> dialog.dismiss());
        dialog.getWindow().getAttributes().windowAnimations = animationSource;
        dialog.show();
    }

    public void supremeClient(String url) {
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("NationalID", pref.getNationalID());
            jsonObject.put("ClientID", pref.getClientID());
            jsonObject.put("PhoneNumber", pref.getPhoneNumber());
            jsonObject.put("DeviceID", unique_id);
            jsonObject.put("DeviceName", Build.MANUFACTURER);
            jsonObject.put("Imei", getDeviceId(getApplicationContext()));
            jsonObject.put("MacAddress", getMacAddr());
            jsonObject.put("Password", edtPassword.getText().toString());

            Log.d("VERIFYCODE", "CC" + jsonObject);
        } catch (final JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(com.android.volley.Request.Method.POST, url, jsonObject, response -> {
            sp_login.setVisibility(View.GONE);
            try {
                Log.d("LLL", response.toString());
                JSONObject object = new JSONObject(response.toString());
                if (object.getString("code").equals("200")) {
                    {
                        //  Toast.makeText(getApplicationContext(), QuestionOne, Toast.LENGTH_LONG).show();
                        pref.setAuthToken(object.getString("data"));
                        loginStatus = "S";

                        // Toast.makeText(getApplicationContext(),pref.getAuthToken(),Toast.LENGTH_LONG).show();
//                        pref.setClientID(object.getString("UserID"));
                        startActivity(new Intent(getApplicationContext(), Dashboard_increado.class));
                        finish();
                        // new updateLoginstatus().execute(Constants.LOGIN_URL + "UpdateLoginStatus");
                    }
                } else {
                    sp_login.setVisibility(View.GONE);
                    final Dialog dialog = new Dialog(context);
                    dialog.setContentView(R.layout.dialog_alert);
                    dialog.setCancelable(false);
                    Button okbtn = (Button) dialog.findViewById(R.id.dialogButtonOK);

                    TextView txtBody = dialog.findViewById(R.id.tv_response_id);
                    txtBody.setText("Login Failed. Kindly check your PIN and try again");
                    okbtn.setOnClickListener(v -> {
                        dialog.dismiss();
                        edtPassword.setText("");
                    });

                    dialog.getWindow().getAttributes().windowAnimations = R.anim.shake_harder;
                    dialog.show();

                    // final Animation animShake = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake);
                }

            } catch (Exception e) {
                Toast.makeText(LoginActivity.this, Log.getStackTraceString(e) + "", Toast.LENGTH_LONG).show();
                Log.i(TAG, Log.getStackTraceString(e));
            }
        }, error -> {
            Log.e("error", error.toString());
            sp_login.setVisibility(View.GONE);
            final Dialog dialog = new Dialog(context);
            dialog.setContentView(R.layout.dialog_alert);
            dialog.setCancelable(false);
            Button okbtn = (Button) dialog.findViewById(R.id.dialogButtonOK);
            TextView remarks = (TextView) dialog.findViewById(R.id.tv_response_id);
            remarks.setText("Your internet is off. Kindly check your connection and try again");


            // if button is clicked, close the custom dialog
            okbtn.setOnClickListener(v -> dialog.dismiss());
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
            deviceUniqueIdentifier = getDeviceId(getApplicationContext());
        }
        if (null == deviceUniqueIdentifier || 0 == deviceUniqueIdentifier.length()) {
            deviceUniqueIdentifier = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        }
        return deviceUniqueIdentifier;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        android.os.Process.killProcess(android.os.Process.myPid());
    }
}