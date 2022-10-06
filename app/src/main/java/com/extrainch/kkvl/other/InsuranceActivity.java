package com.extrainch.kkvl.other;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.extrainch.kkvl.LoginActivity;
import com.extrainch.kkvl.R;
import com.extrainch.kkvl.databinding.ActivityInsuranceBinding;
import com.extrainch.kkvl.ui.dashboard.Dashboard_increado;
import com.extrainch.kkvl.utils.Constants;
import com.extrainch.kkvl.utils.MyPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

public class InsuranceActivity extends AppCompatActivity {
    final Context context = this;
    ActivityInsuranceBinding binding;
    String TAG = InsuranceActivity.class.getSimpleName();
    MyPreferences pref;
    String unique_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityInsuranceBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.imBack.setOnClickListener(v -> {
            startActivity(new Intent(this, Dashboard_increado.class)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        });

        binding.coverDate.setOnClickListener(view -> {
            Calendar calendar = new GregorianCalendar(binding.coverDate.getYear(),
                    binding.coverDate.getMonth(),
                    binding.coverDate.getDayOfMonth());
        });

        unique_id = android.provider.Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);

        pref = new MyPreferences(InsuranceActivity.this);

        createDialogWithoutDateField();
        binding.txtSend.setOnClickListener(view -> supremeInsurancePosting(Constants.BASE_URL + "MobileLoan/InsuranceLead"));
    }

    private DatePickerDialog createDialogWithoutDateField() {
        DatePickerDialog dpd = new DatePickerDialog(this, null, 2014, 1, 24);
        try {
            java.lang.reflect.Field[] datePickerDialogFields = dpd.getClass().getDeclaredFields();
            for (java.lang.reflect.Field datePickerDialogField : datePickerDialogFields) {
                if (datePickerDialogField.getName().equals("mDatePicker")) {
                    datePickerDialogField.setAccessible(true);
                    DatePicker datePicker = (DatePicker) datePickerDialogField.get(dpd);
                    java.lang.reflect.Field[] datePickerFields = datePickerDialogField.getType().getDeclaredFields();
                    for (java.lang.reflect.Field datePickerField : datePickerFields) {
                        Log.i("test", datePickerField.getName());
                        if ("mDaySpinner".equals(datePickerField.getName())) {
                            datePickerField.setAccessible(true);
                            Object dayPicker = datePickerField.get(datePicker);
                            ((View) dayPicker).setVisibility(View.GONE);
                        }
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return dpd;
    }

    public void supremeInsurancePosting(String url) {
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        JSONObject jsonObject = new JSONObject();
        binding.spSave.setVisibility(View.VISIBLE);
        try {
            jsonObject.put("Make", binding.etCarMake.getText().toString());
            jsonObject.put("Mobile", pref.getPhoneNumber());
            jsonObject.put("Model", binding.etCarModel.getText().toString());
            jsonObject.put("Value", binding.etEstimatedValue.getText().toString());
            jsonObject.put("Year", binding.etYear.getText().toString());
            jsonObject.put("CoverStartDate", binding.coverDate.getDayOfMonth() + "-" + binding.coverDate.getMonth() + 1 + "-" + binding.coverDate.getYear());
            jsonObject.put("TokenCode", pref.getAuthToken());
            //    jsonObject.put("CoverStartDate","31 March 2021");

            Log.d("INSURANCE", "CC" + jsonObject);
        } catch (final JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(com.android.volley.Request.Method.POST, url, jsonObject, response -> {
            binding.spSave.setVisibility(View.GONE);
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
                    okbtn.setOnClickListener(v -> {
                        dialog.dismiss();

                        Intent i = new Intent(InsuranceActivity.this, LoginActivity.class);
                        startActivity(i);
                    });

                    dialog.getWindow().getAttributes().windowAnimations = R.anim.dialog_in;
                    dialog.show();
                } else if (object.getString("code").equals("200")) {
                    {
                        final Dialog dialog = new Dialog(InsuranceActivity.this);
                        dialog.setContentView(R.layout.dialog_alert);
                        dialog.setCancelable(false);
                        Button okbtn = dialog.findViewById(R.id.dialogButtonOK);
                        TextView tv_response_id = dialog.findViewById(R.id.tv_response_id);
                        tv_response_id.setText("Your query has been successfully submitted");
                        // if button is clicked, close the custom dialog

                        okbtn.setOnClickListener(v -> {
                            dialog.dismiss();
                            finish();
                        });
                        dialog.show();
                        // new updateLoginstatus().execute(Constants.LOGIN_URL + "UpdateLoginStatus");
                    }
                }
            } catch (Exception e) {
                Toast.makeText(InsuranceActivity.this, Log.getStackTraceString(e) + "", Toast.LENGTH_LONG).show();
                Log.i(TAG, Log.getStackTraceString(e));
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, Dashboard_increado.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
    }
}