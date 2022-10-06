package com.extrainch.kkvl;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.extrainch.kkvl.databinding.ActivityVerifyBinding;
import com.extrainch.kkvl.service.ReceiveSMS;
import com.extrainch.kkvl.utils.AppSignatureHelper;
import com.extrainch.kkvl.utils.Constants;
import com.extrainch.kkvl.utils.MyPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.NetworkInterface;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegistrationActivity extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_READ_PHONE_STATE = 0;
    final Context context = this;
    ActivityVerifyBinding binding;
    String national_id, phone_number, country_code;
    String TAG = "mobile_number";
    MyPreferences pref;
    ProgressDialog progress;
    String unique_id, imei;
    String serialNumber;
    String manufacturer, model;
    int version;
    String smsSignID;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVerifyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        pref = new MyPreferences(RegistrationActivity.this);
        progress = new ProgressDialog(RegistrationActivity.this);
        pref.setPhoneNumber(""); //reset comment if nolonger need it
        pref.setPhoneNumber("");

        AppSignatureHelper appSignatureHelper = new AppSignatureHelper(this);
        for (String signature : appSignatureHelper.getAppSignatures()) {
            Log.d("signature ", signature);
            smsSignID = signature;
        }

        binding.edtVerificationCode.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                binding.tvTimer.setVisibility(View.GONE);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

            }

            @Override
            public void afterTextChanged(Editable arg0) {

            }
        });
        requestsmspermission();
        //loadIMEI();
        getMacAddr();
        getDeviceName();

        unique_id = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);

        binding.edtVerificationCode.getText().toString();
        pref.setVerificationCode(binding.edtVerificationCode.getText().toString());

        binding.tvIndoRegistration.setOnClickListener(view -> {
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                buildclientdetails(R.style.DialogAnimation_1, "Left - Right Animation!");
            } else {
                if (TextUtils.isEmpty(binding.edtNationalId.getText().toString())) {
                    binding.edtNationalId.setError("National ID is required");
                } else if (TextUtils.isEmpty(binding.edtPhoneNumber.getText().toString())) {
                    binding.edtPhoneNumber.setError("Phone Number is required");
                } else if (!binding.chConditions.isChecked()) {

                    binding.chConditions.setError("Phone Number is required");
                    // true,do the task

                } else if (!pref.getPhoneNumber().isEmpty() || !pref.getPhoneNumber().equals("")) {
                    if (pref.getPhoneNumber().length() > 5) {
                        if (TextUtils.isEmpty(binding.edtPassword.getText().toString())) {
                            binding.edtPassword.setError("Password is required");
                        } else if (TextUtils.isEmpty(binding.edtConfirmPassword.getText().toString())) {
                            binding.edtConfirmPassword.setError("Confirm password is required");
                        } else if (!binding.edtConfirmPassword.getText().toString().equals(binding.edtPassword.getText().toString())) {
                            binding.edtConfirmPassword.setError("Password and Confirm Password does not match");
                        } else {
                            new ReceiveSMS().setEditText(binding.edtVerificationCode);
                            progress = new ProgressDialog(RegistrationActivity.this);
                            progress.setMessage("Please wait");

                            progress.show();
                            national_id = binding.edtNationalId.getText().toString();
                            pref.setNationalid(national_id);
                            country_code = binding.edtCountryCode.getText().toString();
                            phone_number = binding.edtPhoneNumber.getText().toString();

                            verifyCode(Constants.BASE_URL + "Security/ClientVerifyCode");
                            // postData("http://197.232.33.44:56759/SupremeApp/api/Security/ClientRegister");
                        }
                    }
                } else {
                    progress = new ProgressDialog(RegistrationActivity.this);
                    progress.setMessage("Please wait");
                    progress.show();
                    national_id = binding.edtNationalId.getText().toString();
                    phone_number = binding.edtPhoneNumber.getText().toString();

                    addRegistrations(Constants.BASE_URL + "Security/ClientRegister");
                }
            }
        });
    }

    public String getDeviceName() {
        manufacturer = Build.MANUFACTURER;
        model = Build.MODEL;
        version = Build.VERSION.SDK_INT;
        String versionRelease = Build.VERSION.RELEASE;
        if (model.toLowerCase().startsWith(manufacturer.toLowerCase())) {
            return capitalize(model);
        } else {
            return capitalize(manufacturer) + " " + model + version;
        }
    }

    private String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }

    private void requestReadPhoneStatePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_PHONE_STATE)) {

            Toast.makeText(getApplicationContext(), "HERE NOW", Toast.LENGTH_LONG).show();

        } else {
            Toast.makeText(getApplicationContext(), "HERE", Toast.LENGTH_LONG).show();
            // READ_PHONE_STATE permission has not been granted yet. Request it directly.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE},
                    MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);
        }
    }

    public void EnableDisableFields() {
        binding.lvVerification.setVisibility(View.VISIBLE);
        binding.lnFirstView.setVisibility(View.GONE);
        binding.tvTxtDetails.setText(phone_number);

        new CountDownTimer(60000, 1000) {

            public void onTick(long millisUntilFinished) {
                binding.tvTimer.setText("" + millisUntilFinished / 1000);
                //here you can have your logic to set text to edittext
            }

            public void onFinish() {
                binding.tvTimer.setText("RESEND");
            }
        }.start();
    }

    private void buildclientdetails(int animationSource, String type) {

        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_alert);
        dialog.setCancelable(false);
        Button okbtn = (Button) dialog.findViewById(R.id.dialogButtonOK);
        TextView remarks = (TextView) dialog.findViewById(R.id.tv_response_id);
        remarks.setText("Dear customer, to enhance your experience, kindly allow phone permissions");


        okbtn.setOnClickListener(v -> {
            dialog.dismiss();

            permission_fn();
        });

        dialog.getWindow().getAttributes().windowAnimations = animationSource;
        dialog.show();
    }

    public void addRegistrations(String url) {
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("NationalID", national_id);
            jsonObject.put("ClientID", pref.getClientID());
            jsonObject.put("PhoneNumber", binding.edtCountryCode.getText().toString() + phone_number);
            jsonObject.put("DeviceID", unique_id);
            jsonObject.put("DeviceName", Build.MANUFACTURER);
            jsonObject.put("Imei", getDeviceId(getApplicationContext()));
            jsonObject.put("MacAddress", getMacAddr());
            jsonObject.put("VerificationCode", binding.edtVerificationCode.getText().toString());
            jsonObject.put("Password", binding.edtPassword.getText().toString());
            jsonObject.put("FireBaseID", pref.getFirebaseId());

            Log.d("XXX", "CC" + jsonObject);
        } catch (final JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(com.android.volley.Request.Method.POST, url, jsonObject, response -> {
            progress.dismiss();
            try {
                Log.d("DDD", response.toString());
                JSONObject object = new JSONObject(response.toString());

                if (object.getString("code").equals("500")) {
                    final Dialog dialog = new Dialog(context);
                    dialog.setContentView(R.layout.dialog_alert);
                    dialog.setCancelable(false);
                    Button okbtn = (Button) dialog.findViewById(R.id.dialogButtonOK);

                    TextView txtBody = dialog.findViewById(R.id.tv_response_id);
                    txtBody.setText(object.getString("msg"));
                    okbtn.setOnClickListener(v -> dialog.dismiss());

                    dialog.getWindow().getAttributes().windowAnimations = R.anim.dialog_in;
                    dialog.show();
                } else if (object.getString("code").equals("200")) {
                    {
                        JSONObject data = new JSONObject(object.getString("data"));
                        pref.setPhoneNumber(binding.edtPhoneNumber.getText().toString());
                        new ReceiveSMS().setEditText(binding.edtVerificationCode);
                        pref.setClientID(data.getString("clientID"));

                        EnableDisableFields();
                        final Dialog dialog = new Dialog(context);
                        dialog.setContentView(R.layout.dialog_alert);
                        dialog.setCancelable(false);
                        Button okbtn = (Button) dialog.findViewById(R.id.dialogButtonOK);

                        TextView txtBody = dialog.findViewById(R.id.tv_response_id);
                        txtBody.setText(data.getString("remarks"));
                        okbtn.setOnClickListener(v -> dialog.dismiss());
                        dialog.getWindow().getAttributes().windowAnimations = R.anim.dialog_in;
                        dialog.show();
                    }
                } else {
                    final Dialog dialog = new Dialog(context);
                    dialog.setContentView(R.layout.dialog_alert);
                    dialog.setCancelable(false);
                    Button okbtn = (Button) dialog.findViewById(R.id.dialogButtonOK);

                    TextView txtBody = dialog.findViewById(R.id.tv_response_id);
                    txtBody.setText("Successfully registered. Kindly verify mobile number");
                    okbtn.setOnClickListener(v -> {
                        dialog.dismiss();
                        verifyCode(Constants.BASE_URL + "Security/ClientVerifyCode");
                    });
                    dialog.getWindow().getAttributes().windowAnimations = R.anim.dialog_in;
                    dialog.show();
                }
            } catch (Exception e) {
                Toast.makeText(RegistrationActivity.this, Log.getStackTraceString(e) + "", Toast.LENGTH_LONG).show();
                Log.i(TAG, Log.getStackTraceString(e));
            }
        }, error -> {
            Log.e("error", error.toString());
            progress.dismiss();
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

    public void verifyCode(String url) {
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("NationalID", national_id);
            jsonObject.put("ClientID", pref.getClientID());
            jsonObject.put("PhoneNumber", binding.edtCountryCode.getText().toString() + phone_number);
            jsonObject.put("DeviceID", unique_id);
            jsonObject.put("DeviceName", Build.MANUFACTURER);
            jsonObject.put("Imei", getDeviceId(getApplicationContext()));
            jsonObject.put("MacAddress", getMacAddr());
            jsonObject.put("VerificationCode", binding.edtVerificationCode.getText().toString());
            jsonObject.put("Password", binding.edtPassword.getText().toString());
            Log.d("VERIFYCODE", "CC1" + jsonObject);
        } catch (final JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(com.android.volley.Request.Method.POST, url, jsonObject, response -> {
            progress.dismiss();
            try {
                Log.d("LLL", response.toString());
                JSONObject object = new JSONObject(response.toString());
//                    pref.setClientID(object.getString("clientID"));
//
//                    Log.d("ClientID", object.getString("clientID"));

                //  pref.setAuthToken(object.getString("tokenAuth"));
                if (object.getString("code").equals("200")) {
                    //log.i("Pref Client ID",pref.getClientID());

                    pref.setClientID(object.getString("data"));
                    //new FetchClientDetails().execute(Constants.LOGIN_URL + "GetClientDetails");
                    updateClientPassword(Constants.LOGIN_URL + "UpdateClientPassword");
                } else if (object.getString("code").equals("500")) {
                    {
                        pref.setPhoneNumber(binding.edtPhoneNumber.getText().toString());
                        //  Toast.makeText(getApplicationContext(), QuestionOne, Toast.LENGTH_LONG).show();

                        EnableDisableFields();
                        final Dialog dialog = new Dialog(context);
                        dialog.setContentView(R.layout.dialog_alert);
                        dialog.setCancelable(false);
                        Button okbtn = (Button) dialog.findViewById(R.id.dialogButtonOK);

                        TextView txtBody = dialog.findViewById(R.id.tv_response_id);
                        txtBody.setText("" + object.getString("msg"));
                        okbtn.setOnClickListener(v -> dialog.dismiss());
                        dialog.getWindow().getAttributes().windowAnimations = R.anim.dialog_in;
                        dialog.show();
                    }
                } else {
                    final Dialog dialog = new Dialog(context);
                    dialog.setContentView(R.layout.dialog_alert);
                    dialog.setCancelable(false);
                    Button okbtn = (Button) dialog.findViewById(R.id.dialogButtonOK);

                    TextView txtBody = dialog.findViewById(R.id.tv_response_id);
                    txtBody.setText("" + object.getString("msg"));
                    okbtn.setOnClickListener(v -> dialog.dismiss());

                    dialog.getWindow().getAttributes().windowAnimations = R.anim.dialog_in;
                    dialog.show();
                }
            } catch (Exception e) {
                Toast.makeText(RegistrationActivity.this, Log.getStackTraceString(e) + "", Toast.LENGTH_LONG).show();
                Log.i(TAG, Log.getStackTraceString(e));
            }
        }, error -> {
            Log.e("error", error.toString());
            progress.dismiss();
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

    public void updateClientPassword(String url) {
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("NationalID", national_id);
            jsonObject.put("ClientID", pref.getClientID());
            jsonObject.put("PhoneNumber", country_code.replace("+", "") + phone_number);
            jsonObject.put("DeviceID", unique_id);
            jsonObject.put("DeviceName", Build.MANUFACTURER);
            jsonObject.put("Imei", getDeviceId(getApplicationContext()));
            jsonObject.put("MacAddress", getMacAddr());
            jsonObject.put("VerificationCode", binding.edtVerificationCode.getText().toString());
            jsonObject.put("Password", binding.edtPassword.getText().toString());

            Log.d("VERIFYCODE", "CC2" + jsonObject);
        } catch (final JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(com.android.volley.Request.Method.POST, url, jsonObject, response -> {
            progress.dismiss();
            try {
                Log.d("LLL", response.toString());
                JSONObject object = new JSONObject(response.toString());
//                    pref.setClientID(object.getString("clientID"));
//
//                    Log.d("ClientID", object.getString("clientID"));

                //  pref.setAuthToken(object.getString("tokenAuth"));
                if (object.getString("code").equals("200")) {
                    //log.i("Pref Client ID",pref.getClientID());
                    pref.setClientID(object.getString("data"));
                    //new FetchClientDetails().execute(Constants.LOGIN_URL + "GetClientDetails");
                    supremeClientDetails(Constants.BASE_URL + "MobileClient/ClientDetail");
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    finish();
                } else if (object.getString("code").equals("500")) {
                    {
                        pref.setPhoneNumber(binding.edtPhoneNumber.getText().toString());
                        //  Toast.makeText(getApplicationContext(), QuestionOne, Toast.LENGTH_LONG).show();

                        EnableDisableFields();
                        final Dialog dialog = new Dialog(context);
                        dialog.setContentView(R.layout.dialog_alert);
                        dialog.setCancelable(false);
                        Button okbtn = (Button) dialog.findViewById(R.id.dialogButtonOK);

                        TextView txtBody = dialog.findViewById(R.id.tv_response_id);
                        txtBody.setText("" + object.getString("msg"));
                        okbtn.setOnClickListener(v -> dialog.dismiss());

                        dialog.getWindow().getAttributes().windowAnimations = R.anim.dialog_in;
                        dialog.show();
                    }
                } else {

                    final Dialog dialog = new Dialog(context);
                    dialog.setContentView(R.layout.dialog_alert);
                    dialog.setCancelable(false);
                    Button okbtn = (Button) dialog.findViewById(R.id.dialogButtonOK);

                    TextView txtBody = dialog.findViewById(R.id.tv_response_id);
                    txtBody.setText("" + object.getString("msg"));
                    okbtn.setOnClickListener(v -> dialog.dismiss());
                    dialog.getWindow().getAttributes().windowAnimations = R.anim.dialog_in;
                    dialog.show();
                }
            } catch (Exception e) {
                Toast.makeText(RegistrationActivity.this, Log.getStackTraceString(e) + "", Toast.LENGTH_LONG).show();
                Log.i(TAG, Log.getStackTraceString(e));
            }
        }, error -> {
            Log.e("error", error.toString());
            progress.dismiss();
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

    private void requestsmspermission() {
        String smspermission = Manifest.permission.RECEIVE_SMS;
        int grant = ContextCompat.checkSelfPermission(this, smspermission);
        //check if read SMS permission is granted or not
        if (grant != PackageManager.PERMISSION_GRANTED) {
            String[] permission_list = new String[1];
            permission_list[0] = smspermission;
            ActivityCompat.requestPermissions(this, permission_list, 1);
        }
    }

    private void requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_PHONE_STATE)) {
            new AlertDialog.Builder(this)
                    .setTitle("Permission Needed")
                    .setMessage("Permission is needed to access files from your device...")
                    .setPositiveButton("OK", (dialog, which) -> ActivityCompat.requestPermissions(RegistrationActivity.this, new String[]{Manifest.permission.READ_PHONE_STATE}, MY_PERMISSIONS_REQUEST_READ_PHONE_STATE))
                    .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss()).create().show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);
        }
    }

    private void permission_fn() {
        if (ContextCompat.checkSelfPermission(RegistrationActivity.this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(RegistrationActivity.this, Manifest.permission.READ_PHONE_STATE)) {
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);
            }
        } else {
            requestStoragePermission();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_READ_PHONE_STATE) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Thanks for enabling the permission", Toast.LENGTH_SHORT).show();
                //do something permission is allowed here....
            } else {
                Toast.makeText(this, "Please allow the Permission", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void supremeClientDetails(String url) {
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("NationalID", pref.getNationalID());
            jsonObject.put("ClientID", pref.getClientID());
            jsonObject.put("PhoneNumber", binding.edtCountryCode.getText().toString() + pref.getPhoneNumber());
            jsonObject.put("DeviceID", unique_id);
            jsonObject.put("DeviceName", Build.MANUFACTURER);
            jsonObject.put("Imei", unique_id);
            jsonObject.put("MacAddress", getMacAddr());

            Log.d("VERIFYCODE", "CC3" + jsonObject);
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
                Toast.makeText(RegistrationActivity.this, Log.getStackTraceString(e) + "", Toast.LENGTH_LONG).show();
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
}