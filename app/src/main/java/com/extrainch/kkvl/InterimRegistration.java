package com.extrainch.kkvl;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.extrainch.kkvl.service.ReceiveSMS;
import com.extrainch.kkvl.utils.Constants;
import com.extrainch.kkvl.utils.MyPreferences;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONObject;

import java.io.IOException;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

public class InterimRegistration extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_READ_PHONE_STATE = 0;
    final Context context = this;
    ImageView im_back;
    EditText nationalID, phoneNumber, clientID, password, et_verificatonCode, et_confirmPassword;
    TextView tv_password, tv_confirmPassword, tv_verificatonCode, tv_client_id, tv_national_id;
    String national_id, phone_number, client_id;
    String TAG = "mobile_number";
    ScrollView lnSecurityQuestions;
    MyPreferences pref;
    Spinner spQuestionOne, spQuestionTwo;
    EditText edtAnswerOne, edtAnswerTwo;
    ProgressDialog progress;
    TextView registerBtn, tvVerificationCode;
    String unique_id, imei;
    String serialNumber;
    String manufacturer, model;
    int version;
    ImageView imgClientID, imgNationalID;
    //TextView txtForgotPassword;

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
        setContentView(R.layout.activity_registration);


        pref = new MyPreferences(InterimRegistration.this);
        progress = new ProgressDialog(InterimRegistration.this);
        pref.setPhoneNumber(""); //reset comment if nolonger need it
        pref.setPhoneNumber("");
        im_back = (ImageView) findViewById(R.id.im_back);
        nationalID = (EditText) findViewById(R.id.edt_National_id);
        tv_verificatonCode = findViewById(R.id.tv_verificationCode);
        password = (EditText) findViewById(R.id.edt_password);
        lnSecurityQuestions = findViewById(R.id.ln_security_questions);
        et_verificatonCode = (EditText) findViewById(R.id.edt_verificationCode);
        et_confirmPassword = (EditText) findViewById(R.id.edt_confirmPassword);
        tv_password = (TextView) findViewById(R.id.tv_password);
        tv_confirmPassword = (TextView) findViewById(R.id.tv_confirmPassword);
        tv_verificatonCode = (TextView) findViewById(R.id.tv_verificationCode);
        tv_national_id = (TextView) findViewById(R.id.tv_national_id);

        phoneNumber = (EditText) findViewById(R.id.edt_PhoneNumber);


        loadIMEI();

        getMacAddr();

        getDeviceName();


        unique_id = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        im_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        registerBtn = (TextView) findViewById(R.id.tv_indo_registration);
        et_verificatonCode.getText().toString();
        pref.setVerificationCode(et_verificatonCode.getText().toString());

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_PHONE_STATE)
                        != PackageManager.PERMISSION_GRANTED) {
                    // READ_PHONE_STATE permission has not been granted.
                    requestReadPhoneStatePermission();
                } else {

                    if (TextUtils.isEmpty(nationalID.getText().toString())) {
                        nationalID.setError("National ID is required");

                    } else if (TextUtils.isEmpty(phoneNumber.getText().toString())) {
                        phoneNumber.setError("Phone Number is required");
                    } else if (!pref.getPhoneNumber().toString().isEmpty() || !pref.getPhoneNumber().toString().equals("")) {
                        if (pref.getPhoneNumber().toString().length() > 5) {
                            if (TextUtils.isEmpty(password.getText().toString())) {
                                password.setError("Password is required");
                            } else if (TextUtils.isEmpty(et_confirmPassword.getText().toString())) {
                                et_confirmPassword.setError("Confirm password is required");
                            } else if (!et_confirmPassword.getText().toString().equals(password.getText().toString())) {
                                et_confirmPassword.setError("Password and Confirm Password does not match");
                            } else {
                                new ReceiveSMS().setEditText(et_verificatonCode);
                                progress = new ProgressDialog(InterimRegistration.this);
                                progress.setMessage("Please wait");
                                progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                                progress.setIndeterminate(true);
                                progress.setProgress(0);
                                progress.show();
                                national_id = nationalID.getText().toString();
                                pref.setNationalid(national_id);
                                phone_number = phoneNumber.getText().toString();
                                new Registration().execute(Constants.REGISTRATION + "AddClientRegistration");
                            }
                        }
                    } else {
                        progress = new ProgressDialog(InterimRegistration.this);
                        progress.setMessage("Please wait");
                        progress.show();
                        national_id = nationalID.getText().toString();
                        phone_number = phoneNumber.getText().toString();

                        //branchID = branch.getText().toString();
                        new Registration().execute(Constants.REGISTRATION + "AddClientRegistration");
                    }
                }
                //new LoadBranches().execute(Constants.);
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

    public void loadIMEI() {
        // Check if the READ_PHONE_STATE permission is already available.
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            // READ_PHONE_STATE permission has not been granted.
            requestReadPhoneStatePermission();
        } else {
            // READ_PHONE_STATE permission is already been granted.
            TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            imei = "";
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                serialNumber = Build.getSerial();

            } else {
                serialNumber = telephonyManager.getDeviceId();
            }

            if (Build.VERSION.SDK_INT >= 26) {
                imei = telephonyManager.getImei();


            } else {
                imei = telephonyManager.getDeviceId();

            }
        }
    }

    public String getDeviceIMEI() {
        String deviceUniqueIdentifier = null;
        TelephonyManager tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        if (null != tm) {
            deviceUniqueIdentifier = tm.getDeviceId();
        }
        if (null == deviceUniqueIdentifier || 0 == deviceUniqueIdentifier.length()) {
            deviceUniqueIdentifier = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        }
        return deviceUniqueIdentifier;
    }

    private void requestReadPhoneStatePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_PHONE_STATE)) {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // For example if the user has previously denied the permission.
            buildclientdetails(R.style.DialogAnimation_1, "Left - Right Animation!");
        } else {
            // READ_PHONE_STATE permission has not been granted yet. Request it directly.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE},
                    MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);
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

    private void alertAlert(String msg) {
        new AlertDialog.Builder(InterimRegistration.this)
                .setTitle("Permission Request")
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do somthing here
                    }
                })
                //   .setIcon(R.drawable.onlinlinew_warning_sign)
                .show();
    }

    public void EnableDisableFields() {
        password.setVisibility(View.VISIBLE);
        et_verificatonCode.setVisibility(View.VISIBLE);
        et_confirmPassword.setVisibility(View.VISIBLE);
        tv_password.setVisibility(View.VISIBLE);
        tv_confirmPassword.setVisibility(View.VISIBLE);
        tv_verificatonCode.setVisibility(View.VISIBLE);
        nationalID.setVisibility(View.GONE);
        tv_verificatonCode.setVisibility(View.VISIBLE);
        phoneNumber.setEnabled(false);
        tv_national_id.setVisibility(View.GONE);
        lnSecurityQuestions.setVisibility(View.VISIBLE);
    }


    public void EnableDisableSecurity() {

        password.setVisibility(View.GONE);
        et_verificatonCode.setVisibility(View.GONE);
        et_confirmPassword.setVisibility(View.GONE);
        tv_password.setVisibility(View.GONE);
        tv_confirmPassword.setVisibility(View.GONE);
        tv_verificatonCode.setVisibility(View.GONE);
        nationalID.setVisibility(View.GONE);

        phoneNumber.setEnabled(false);
        tv_national_id.setVisibility(View.GONE);

        lnSecurityQuestions.setVisibility(View.VISIBLE);

    }

    private void buildclientdetails(int animationSource, String type) {

        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_alert);
        dialog.setCancelable(false);
        Button okbtn = (Button) dialog.findViewById(R.id.dialogButtonOK);
        TextView remarks = (TextView) dialog.findViewById(R.id.tv_response_id);
        remarks.setText("Dear member, to enhance your experience, kindly allow phone permissions");


        // if button is clicked, close the custom dialog
        okbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                ActivityCompat.requestPermissions(InterimRegistration.this,
                        new String[]{Manifest.permission.READ_PHONE_STATE},
                        MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);
            }
        });

        dialog.getWindow().getAttributes().windowAnimations = animationSource;
        dialog.show();
    }

    public class Registration extends AsyncTask<String, Void, String> {
        OkHttpClient client = new OkHttpClient();
        boolean connected = false;
        String result = "";


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progress.dismiss();
            if (connected) {
                Toast.makeText(getApplicationContext(), "Error: Could not reach the server ", Toast.LENGTH_LONG).show();
                // Toast.makeText(getApplicationContext(),"User Name is"+userName+password,Toast.LENGTH_LONG).show();

            } else {

                try {
                    JSONObject object = new JSONObject(s);

                    if (object.getString("Status").equals("Success")) {

                        //  pref.setAuthToken(object.getString("tokenAuth"));


                        if (object.getString("Remarks").equals("SUCCESS")) {
                            pref.setClientID(object.getString("ClientID").toString());
                            //log.i("Pref Client ID",pref.getClientID());
                            new FetchClientDetails().execute(Constants.LOGIN_URL + "GetClientDetails");

                            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                            finish();

                        } else {

                            pref.setPhoneNumber(phoneNumber.getText().toString());


                            //  Toast.makeText(getApplicationContext(), QuestionOne, Toast.LENGTH_LONG).show();

                            EnableDisableFields();
                            final Dialog dialog = new Dialog(context);
                            dialog.setContentView(R.layout.dialog_alert);
                            dialog.setCancelable(false);
                            Button okbtn = (Button) dialog.findViewById(R.id.dialogButtonOK);

                            TextView txtBody = dialog.findViewById(R.id.tv_response_id);
                            txtBody.setText("" + object.getString("Remarks"));
                            okbtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    dialog.dismiss();

                                }
                            });

                            dialog.getWindow().getAttributes().windowAnimations = R.anim.dialog_in;
                            dialog.show();

                        }


                    } else {

                        final Dialog dialog = new Dialog(context);
                        dialog.setContentView(R.layout.dialog_alert);
                        dialog.setCancelable(false);
                        Button okbtn = (Button) dialog.findViewById(R.id.dialogButtonOK);

                        TextView txtBody = dialog.findViewById(R.id.tv_response_id);
                        txtBody.setText("" + object.getString("Remarks"));
                        okbtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                dialog.dismiss();

                            }
                        });

                        dialog.getWindow().getAttributes().windowAnimations = R.anim.dialog_in;
                        dialog.show();


                    }


                } catch (Exception e) {

                    AlertDialog.Builder alert = new AlertDialog.Builder(InterimRegistration.this);
                    alert.setMessage("Unknown Error occured. Kindly contact administrator");

                    alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    alert.show();
                    Log.i(TAG, Log.getStackTraceString(e));
                }

            }

        }

        @Override
        protected String doInBackground(String... params) {
            RequestBody
                    formBody = new FormEncodingBuilder()
                    .add("NationalID", national_id)
                    .add("PhoneNumber", phone_number)
                    .add("DeviceID", unique_id)
                    .add("DeviceName", getDeviceName())
                    .add("Imei", getDeviceIMEI())
                    .add("MacAddress", getMacAddr())
                    .add("VerificationCode", et_verificatonCode.getText().toString())
//                    .add("SecurityQueryOne", spQuestionOne.getSelectedItem().toString())
                    //       .add("SecurityQueryTwo", spQuestionTwo.getSelectedItem().toString())
                    //   .add("SecurityAnsOne", edtAnswerOne.getText().toString())
                    //   .add("SecurityAnsTwo", edtAnswerTwo.getText().toString())
                    .add("Password", password.getText().toString())
                    .build();


            Request request = new Request.Builder()
                    .url(params[0]).post(formBody)
                    .build();

            try {
                Response response = client.newCall(request).execute();
                if (!response.isSuccessful())
                    throw new IOException("Unexpected code " + response.toString());

                result = response.body().string();

                Log.i(TAG, result);
//                Toast.makeText(getApplicationContext(),result+"",Toast.LENGTH_LONG).show();

            } catch (Exception e) {
                connected = true;
                Log.i(TAG, "" + Log.getStackTraceString(e));
                return null;
            }
            return result;
        }
    }

    public class FetchClientDetails extends AsyncTask<String, Void, String> {
        OkHttpClient client = new OkHttpClient();
        boolean connected = false;
        String result = "";


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (connected) {
                Toast.makeText(getApplicationContext(), "Check connection", Toast.LENGTH_LONG).show();
                // Toast.makeText(getApplicationContext(),"User Name is"+userName+password,Toast.LENGTH_LONG).show();

            } else {

                try {
                    JSONObject object = new JSONObject(s);
                    if (object.getString("Status").equals("success")) {
                        //pref.setAuthToken(object.getString("ClientID"));

                        pref.setClientID(object.getString("ClientID"));
                        pref.setODSAccID(object.getString("AccountID"));
                        pref.setPhoneNumber(object.getString("PhoneNumber"));
                        pref.setLoanAccountID(object.getString("LoanAccountID"));
                        pref.setAccountName(object.getString("Name"));
                        pref.setAccountReminder(object.getString("Reminder"));
                        pref.setMBDAccountID(object.getString("MBDAccountID"));

                    } else {

                    }
                } catch (Exception e) {


                    Log.i(TAG, Log.getStackTraceString(e));
                }

            }

        }

        @Override
        protected String doInBackground(String... params) {
            RequestBody
                    formBody = new FormEncodingBuilder()
                    .add("DeviceID", android.provider.Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID))
                    .build();


            Request request = new Request.Builder()
                    .url(params[0]).post(formBody)
                    .build();

            try {
                Response response = client.newCall(request).execute();
                if (!response.isSuccessful())
                    throw new IOException("Unexpected code " + response.toString());

                result = response.body().string();

                Log.i(TAG, result);
//                Toast.makeText(getApplicationContext(),result+"",Toast.LENGTH_LONG).show();

            } catch (Exception e) {
                connected = true;
                Log.i(TAG, "" + Log.getStackTraceString(e));
                return null;
            }
            return result;
        }
    }


}