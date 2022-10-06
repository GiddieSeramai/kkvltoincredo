package com.extrainch.kkvl;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.extrainch.kkvl.databinding.ActivityPinBinding;
import com.extrainch.kkvl.ui.dashboard.Dashboard_increado;
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

public class ChangePinActivity extends AppCompatActivity {

    final Context context = this;
    //Data Binding
    ActivityPinBinding binding;
    ProgressDialog progress;
    MyPreferences pref;
    EditText edtOldPIN, edtNewPIN, edtConfirmPIN;
    TextView tv_continue;
    String current_pass, new_pass;
    String TAG = "Change PIN:";
    LinearLayout lnView;
    SpinKitView sp_save;

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
        binding = ActivityPinBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        pref = new MyPreferences(ChangePinActivity.this);
        progress = new ProgressDialog(ChangePinActivity.this);


        lnView = findViewById(R.id.lnView);
        sp_save = findViewById(R.id.sp_save);
        tv_continue = findViewById(R.id.tv_reg_continue);
        edtOldPIN = findViewById(R.id.et_current_pin);
        edtNewPIN = findViewById(R.id.et_new_pin);
        edtConfirmPIN = findViewById(R.id.et_confirm_pin);

        tv_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(edtOldPIN.getText().toString())) {
                    edtOldPIN.setError("Current PIN is required");
                } else if (TextUtils.isEmpty(edtNewPIN.getText().toString())) {
                    edtNewPIN.setError("New PIN is required");
                } else if (TextUtils.isEmpty(edtConfirmPIN.getText().toString())) {
                    edtConfirmPIN.setError("Confirm password is required");
                } else if (!edtNewPIN.getText().toString().equals(edtConfirmPIN.getText().toString())) {
                    edtConfirmPIN.setError("Password and Confirm Password does not match");
                } else {
                    current_pass = edtOldPIN.getText().toString();
                    new_pass = edtNewPIN.getText().toString();
                    pref.getPhoneNumber();
                    supremeClientPIN(Constants.BASE_URL + "Security/ResetClientPIN ");
                }
            }
        });

        binding.backPressedSettings.setOnClickListener(v -> {
            startActivity(new Intent(this, Dashboard_increado.class)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        });

    }

    public void supremeClientPIN(String url) {
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        JSONObject jsonObject = new JSONObject();
        sp_save.setVisibility(View.VISIBLE);
        try {
//        {"ClientID":"0000266","DeviceID":"eee" ,"MacAddress":"2","PhoneNumber":"COL",
//                "Password":"pass1234","NationalID":"sss"}


            jsonObject.put("DeviceID", Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID));
            jsonObject.put("OldPassword", edtOldPIN.getText().toString());
            jsonObject.put("NewPassword", edtNewPIN.getText().toString());


            Log.d("INSURANCE", "CC" + jsonObject);
        } catch (final JSONException e) {
            e.printStackTrace();

        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(com.android.volley.Request.Method.POST, url, jsonObject, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                sp_save.setVisibility(View.GONE);
                Log.d("LLL", response.toString());
                // progress.dismiss();
                try {
                    Log.d("LLL", response.toString());

                    JSONObject object = new JSONObject(response.toString());


                    if (object.getString("code").equals("200")) {
                        {

                            final Dialog dialog = new Dialog(ChangePinActivity.this);
                            dialog.setContentView(R.layout.dialog_alert);
                            dialog.setCancelable(false);
                            Button okbtn = dialog.findViewById(R.id.dialogButtonOK);
                            TextView tv_response_id = dialog.findViewById(R.id.tv_response_id);
                            tv_response_id.setText("Your query has been successfully submitted");
                            // if button is clicked, close the custom dialog

                            okbtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    dialog.dismiss();
                                    finish();

                                    Intent i = new Intent(ChangePinActivity.this, LoginActivity.class);
                                    startActivity(i);

                                }
                            });
                            dialog.show();
                            // new updateLoginstatus().execute(Constants.LOGIN_URL + "UpdateLoginStatus");
                        }
                    } else {

                        final Dialog dialog = new Dialog(ChangePinActivity.this);
                        dialog.setContentView(R.layout.dialog_alert);
                        dialog.setCancelable(false);
                        Button okbtn = dialog.findViewById(R.id.dialogButtonOK);
                        TextView tv_response_id = dialog.findViewById(R.id.tv_response_id);
                        tv_response_id.setText(object.getString("msg"));
                        // if button is clicked, close the custom dialog

                        okbtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                dialog.dismiss();
                                finish();

                                edtConfirmPIN.setText("");
                                edtNewPIN.setText("");
                                edtOldPIN.setText("");

                            }
                        });
                        dialog.show();


                    }

                } catch (Exception e) {

                    Toast.makeText(ChangePinActivity.this, Log.getStackTraceString(e) + "",
                            Toast.LENGTH_LONG).show();
                    Log.i(TAG, Log.getStackTraceString(e));
                }
            }


        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error", error.toString());

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
}