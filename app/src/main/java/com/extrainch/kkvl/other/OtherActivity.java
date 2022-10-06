package com.extrainch.kkvl.other;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.extrainch.kkvl.DashboardActivity;
import com.extrainch.kkvl.R;
import com.extrainch.kkvl.databinding.ActivityOtherBinding;
import com.extrainch.kkvl.ui.dashboard.Dashboard_increado;
import com.extrainch.kkvl.utils.Constants;
import com.extrainch.kkvl.utils.MyPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class OtherActivity extends AppCompatActivity {
    ActivityOtherBinding binding;
    MyPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOtherBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        pref = new MyPreferences(OtherActivity.this);

        binding.etClientName.setText(pref.getClientID() + " " + pref.getAccountName());
        binding.etPhone.setText(pref.getPhoneNumber());

        binding.txtSend.setOnClickListener(v -> supremeSendMessage(Constants.BASE_URL + "MobileClient/ClientContactUs"));

        binding.imBack.setOnClickListener(v -> startActivity(new Intent(this, Dashboard_increado.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK)));
    }

    public void supremeSendMessage(String url) {
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("Name", binding.etClientName.getText().toString());
            jsonObject.put("Phone", pref.getPhoneNumber());
            jsonObject.put("Email", binding.etClientEmail.getText().toString());
            jsonObject.put("Message", binding.etMessage.getText().toString());
            jsonObject.put("TokenCode", pref.getAuthToken());
            Log.d("Loan Application", jsonObject.toString());
        } catch (final JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(com.android.volley.Request.Method.POST, url, jsonObject, response -> {
            // progress.dismiss();
            try {
                Log.d("LLL", response.toString());

                JSONObject object = new JSONObject(response.toString());
                if (object.getString("code").equals("300")) {
                    final Dialog dialog = new Dialog(getApplicationContext());
                    dialog.setContentView(R.layout.dialog_alert);
                    dialog.setCancelable(false);
                    Button okbtn = dialog.findViewById(R.id.dialogButtonOK);

                    TextView txtBody = dialog.findViewById(R.id.tv_response_id);
                    txtBody.setText("Your session has expired. Kindly relogin to continue");
                    okbtn.setOnClickListener(v -> {
                        dialog.dismiss();
                        Intent i = new Intent(OtherActivity.this, OtherActivity.class);
                        startActivity(i);
                    });

                    dialog.getWindow().getAttributes().windowAnimations = R.anim.dialog_in;
                    dialog.show();
                } else if (object.getString("msg").equals("Success")) {
                    final Dialog dialog = new Dialog(OtherActivity.this);
                    dialog.setContentView(R.layout.dialog_alert);
                    dialog.setCancelable(false);
                    Button okbtn = (Button) dialog.findViewById(R.id.dialogButtonOK);
                    TextView tv_response_id = dialog.findViewById(R.id.tv_response_id);
                    tv_response_id.setText("Message has been submitted successfully");
                    // if button is clicked, close the custom dialog

                    okbtn.setOnClickListener(v -> {
                        dialog.dismiss();
                        Intent i = new Intent(OtherActivity.this, DashboardActivity.class);
                        startActivity(i);
                    });
                    dialog.getWindow().getAttributes().windowAnimations = R.anim.dialog_in;
                    dialog.show();
                }
            } catch (Exception e) {
                Toast.makeText(OtherActivity.this, Log.getStackTraceString(e) + "", Toast.LENGTH_LONG).show();
                Log.i("TAG", Log.getStackTraceString(e));
            }
        }, error -> {
            Log.e("error", error.toString());
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

    private boolean isValidEmailId(String email) {
        return Pattern.compile("^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$").matcher(email).matches();
    }
}