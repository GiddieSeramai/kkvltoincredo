package com.extrainch.kkvl.other;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.extrainch.kkvl.LoginActivity;
import com.extrainch.kkvl.R;
import com.extrainch.kkvl.RegistrationActivity;
import com.extrainch.kkvl.utils.Constants;
import com.extrainch.kkvl.utils.MyPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.NetworkInterface;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChangePIN extends AppCompatActivity {

    final Context context = this;
    String TAG = ChangePIN.class.getSimpleName();
    String unique_id;
    MyPreferences pref;

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


    //https://197.232.33.44:56759/SupremeApp/api/Security/UpdateClientPassword
    // {"ClientID":"0000266","DeviceID":"eee" ,"MacAddress":"2","PhoneNumber":"COL",
    // "Password":"pass1234","NationalID":"sss"}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_p_i_n);

        pref = new MyPreferences(ChangePIN.this);

        unique_id = android.provider.Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);


        changePIN(Constants.BASE_URL + "Security/UpdateClientPassword");
    }

    public void changePIN(String url) {
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("MacAddress", getMacAddr());
            jsonObject.put("ClientID", pref.getClientID());
            jsonObject.put("DeviceID", unique_id);
            jsonObject.put("PhoneNumber", pref.getPhoneNumber());
            jsonObject.put("NationalID", pref.getNationalID());
            jsonObject.put("TokenCode", pref.getAuthToken());

            Log.d("ChangePIN", "CC" + jsonObject.toString());
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


                    if (object.getString("code").equals("300")) {
                        final Dialog dialog = new Dialog(context);
                        dialog.setContentView(R.layout.dialog_alert);
                        dialog.setCancelable(false);
                        Button okbtn = (Button) dialog.findViewById(R.id.dialogButtonOK);

                        TextView txtBody = dialog.findViewById(R.id.tv_response_id);
                        txtBody.setText("Your session has expired. Kindly relogin to continue");
                        okbtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                dialog.dismiss();

                                Intent i = new Intent(ChangePIN.this, LoginActivity.class);
                                startActivity(i);

                            }
                        });

                        dialog.getWindow().getAttributes().windowAnimations = R.anim.dialog_in;
                        dialog.show();
                    } else if (object.getString("code").equals("200")) {
                        {

                            JSONObject data = new JSONObject(object.getString("data"));

                            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                            finish();
                            // new updateLoginstatus().execute(Constants.LOGIN_URL + "UpdateLoginStatus");
                        }
                    } else {


                        startActivity(new Intent(getApplicationContext(), RegistrationActivity.class));
                        finish();
                    }

                } catch (Exception e) {

                    Toast.makeText(ChangePIN.this, Log.getStackTraceString(e) + "",
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