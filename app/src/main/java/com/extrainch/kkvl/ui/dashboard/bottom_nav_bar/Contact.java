package com.extrainch.kkvl.ui.dashboard.bottom_nav_bar;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.extrainch.kkvl.DashboardActivity;
import com.extrainch.kkvl.R;
import com.extrainch.kkvl.databinding.FragmentContactusBinding;
import com.extrainch.kkvl.utils.Constants;
import com.extrainch.kkvl.utils.MyPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class Contact extends Fragment {
    FragmentContactusBinding binding;
    MyPreferences pref;

    public Contact() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pref = new MyPreferences(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentContactusBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.etClientName.setText(pref.getClientID() + " " + pref.getAccountName());
        binding.etPhone.setText(pref.getPhoneNumber());

        binding.txtSend.setOnClickListener(v -> supremeSendMessage(Constants.BASE_URL + "MobileClient/ClientContactUs"));
    }

    public void supremeSendMessage(String url) {
        RequestQueue queue = Volley.newRequestQueue(getContext());
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
                    final Dialog dialog = new Dialog(getContext());
                    dialog.setContentView(R.layout.dialog_alert);
                    dialog.setCancelable(false);
                    Button okbtn = dialog.findViewById(R.id.dialogButtonOK);

                    TextView txtBody = dialog.findViewById(R.id.tv_response_id);
                    txtBody.setText("Your session has expired. Kindly relogin to continue");
                    okbtn.setOnClickListener(v -> {
                        dialog.dismiss();
//                        Intent i = new Intent(getContext(), ContactUsActivity.class);
//                        startActivity(i);
                    });

                    dialog.getWindow().getAttributes().windowAnimations = R.anim.dialog_in;
                    dialog.show();
                } else if (object.getString("msg").equals("Success")) {
                    final Dialog dialog = new Dialog(getContext());
                    dialog.setContentView(R.layout.dialog_alert);
                    dialog.setCancelable(false);
                    Button okbtn = dialog.findViewById(R.id.dialogButtonOK);
                    TextView tv_response_id = dialog.findViewById(R.id.tv_response_id);
                    tv_response_id.setText("Message has been submitted successfully");
                    // if button is clicked, close the custom dialog

                    okbtn.setOnClickListener(v -> {
                        dialog.dismiss();
                        Intent i = new Intent(getContext(), DashboardActivity.class);
                        startActivity(i);
                    });
                    dialog.getWindow().getAttributes().windowAnimations = R.anim.dialog_in;
                    dialog.show();
                }
            } catch (Exception e) {
                Toast.makeText(getContext(), Log.getStackTraceString(e) + "", Toast.LENGTH_LONG).show();
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