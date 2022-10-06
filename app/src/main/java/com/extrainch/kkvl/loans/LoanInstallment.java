package com.extrainch.kkvl.loans;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.extrainch.kkvl.R;
import com.extrainch.kkvl.adapter.InstallmentAdapter;
import com.extrainch.kkvl.models.Installment;
import com.extrainch.kkvl.ui.dashboard.Dashboard_increado;
import com.extrainch.kkvl.utils.Constants;
import com.extrainch.kkvl.utils.MyPreferences;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoanInstallment extends AppCompatActivity {

    String TAG = LoanInstallment.class.getSimpleName();
    MyPreferences pref;
    LinearLayout lnViews, lblLoans;
    ImageView im_back;
    private List<Installment> installmentList = new ArrayList<>();
    private InstallmentAdapter mAdapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loan_installment);

        pref = new MyPreferences(this);

        mAdapter = new InstallmentAdapter(installmentList);

        lnViews = findViewById(R.id.ln_views);
        lblLoans = findViewById(R.id.lblLoans);

        lblLoans.setVisibility(View.GONE);
        lnViews.setVisibility(View.GONE);

        recyclerView = findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);


        im_back = findViewById(R.id.im_back);

        im_back.setOnClickListener(v -> startActivity(new Intent(this, Dashboard_increado.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)));


        calculateInstallment("https://197.232.33.44:56759/SupremeApp/api/MobileLoan/FetchLoanInstallment");

    }

    public void calculateInstallment(String url) {
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        JSONObject jsonObject = new JSONObject();
        try {


            jsonObject.put("AccountID", pref.getAccID());

            jsonObject.put("LoanSeries", "1");


            Log.d("VERIFYCODE", "CC" + jsonObject);
        } catch (final JSONException e) {
            e.printStackTrace();

        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(com.android.volley.Request.Method.POST, url, jsonObject, response -> {
            try {
                JSONObject data = new JSONObject(response.toString());

                String s = data.getString("data");
                Log.i(TAG, s);
                JSONArray array = new JSONArray(s);

                if (array.length() == 0) {
                    Toast.makeText(LoanInstallment.this, "Error loading installments", Toast.LENGTH_SHORT).show();
                } else {
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object = array.getJSONObject(i);
                        lnViews.setVisibility(View.VISIBLE);
                        lblLoans.setVisibility(View.VISIBLE);

                        DecimalFormat formatter = new DecimalFormat("#,###,###");
//                            String yourFormattedString = formatter.format( Double.parseDouble(object.getString("prnBalance")));

                        Installment installment = new Installment(
                                object.getString("installmentNo") + ".",
                                object.getString("installmentDueDate"),
                                formatter.format(Double.parseDouble(object.getString("installmentAmount"))),
                                formatter.format(Double.parseDouble(object.getString("installmentAmount"))),
                                object.getString("paidStatus"));
                        installmentList.add(installment);
                    }

                    mAdapter.notifyDataSetChanged();

                    recyclerView.setAdapter(mAdapter);

                    Log.i(TAG, s);
                }
            } catch (Exception e) {
                Log.i(TAG, Log.getStackTraceString(e));
            }
        }, error -> Log.e("error", error.toString())) {
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

    public void getLoanInstallment() {

        new AsyncTask<String, Void, String>() {
            ProgressDialog dialog;
            OkHttpClient client = new OkHttpClient();
            boolean connected = false;
            String result;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                dialog = new ProgressDialog(LoanInstallment.this);
                dialog.setMessage("Please wait");
                dialog.show();
            }

            @Override
            protected String doInBackground(String... params) {

                RequestBody
                        formBody = new FormEncodingBuilder()
                        .add("TokenCode", pref.getAuthToken())
                        .add("AccountID", pref.getAccountId())
                        .add("LoanSeries", "1")
                        .build();

                Request request = new Request.Builder()
                        .url(params[0]).post(formBody)
                        .build();

                try {
                    Response response = client.newCall(request).execute();
                    if (!response.isSuccessful())
                        throw new IOException("Unexpected code " + response.toString());

                    result = response.body().string();

                } catch (Exception e) {
                    connected = true;
                    Log.i(TAG, "" + Log.getStackTraceString(e));
                    return null;
                }
                return result;
            }

            @Override
            protected void onPostExecute(String s) {

                super.onPostExecute(s);

                try {
                    Log.i(TAG, s);
                    JSONArray array = new JSONArray(s);

                    if (array.length() == 0) {
                        Toast.makeText(LoanInstallment.this, "Error loading installments", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();

                    } else {
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject object = array.getJSONObject(i);
                            lnViews.setVisibility(View.VISIBLE);
                            lblLoans.setVisibility(View.VISIBLE);
//
//                            Calculator calculator = new Calculator(
//                                    object.getString("PrnBalance"),
//                                    object.getString("PrnAmount"),
//                                    object.getString("IntAmount"),
//                                    object.getString("InstAmount"));
//                            calculatorList.add(calculator);

//Toast.makeText(getApplicationContext(),calculator.getInstAmount(),Toast.LENGTH_LONG).show();
                        }

                        mAdapter.notifyDataSetChanged();

                        recyclerView.setAdapter(mAdapter);
                        dialog.dismiss();
                        Log.i(TAG, s);
                    }
                } catch (Exception e) {
                    Log.i(TAG, Log.getStackTraceString(e));
                }

            }

        }.execute(Constants.LOAN_URL + "FetchLoanInstallment");
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, Dashboard_increado.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
    }
}