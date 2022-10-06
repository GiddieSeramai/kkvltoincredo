package com.extrainch.kkvl.loans;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.extrainch.kkvl.DashboardActivity;
import com.extrainch.kkvl.R;
import com.extrainch.kkvl.utils.Constants;
import com.extrainch.kkvl.utils.MyPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class LoanConfirmation extends AppCompatActivity {
    TextView txtDescription;
    MyPreferences pref;
    ImageView im_view;
    TextView txtInstallmentDate, txtMaturityDate, txtTerm, txtAccountNumber, txtLoanAmount, txtInstallmentAmount;
    Intent intent = getIntent();
    String TAG = LoanConfirmation.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loan_confirmation);

        txtLoanAmount = findViewById(R.id.txt_loan_amount);
        txtInstallmentAmount = findViewById(R.id.txt_installment_amount);
        txtInstallmentDate = findViewById(R.id.installment_date);
        txtMaturityDate = findViewById(R.id.maturity_date);
        txtTerm = findViewById(R.id.txt_term);
        txtAccountNumber = findViewById(R.id.account_number);


        pref = new MyPreferences(LoanConfirmation.this);

        txtDescription = findViewById(R.id.txt_description);
        im_view = findViewById(R.id.im_view);

        im_view.setImageResource(R.drawable.checked);

        txtDescription.setText("Dear " + pref.getAccountName() + " your loan is has been processed successfully");

        supremeLoanDetails(Constants.BASE_URL + "MobileLoan/FetchLoanDetails");
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(LoanConfirmation.this, DashboardActivity.class);
        startActivity(i);
    }

    public void supremeLoanDetails(String url) {
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("OurBranchID", pref.getOurBranchID());
            jsonObject.put("ClientID", pref.getClientID());
            jsonObject.put("AccountID", pref.getAccID());
            jsonObject.put("LoanSeries", "1");

            Log.d("Loan Application", jsonObject.toString());
        } catch (final JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(com.android.volley.Request.Method.POST, url, jsonObject, response -> {
            // progress.dismiss();
            try {
                Log.d("LLL", response.toString());
                JSONObject object = new JSONObject(response.toString());
                if (object.getString("code").equals("200")) {
                    {
                        if (object.getString("msg").equals("Success")) {
                            //  JSONObject data = new JSONObject(object.getString("data"));
                            Log.d("BarD", object.getString("data"));
                            JSONArray dat = new JSONArray(object.getString("data"));
                            JSONArray alsoKnownAsArray = dat;
                            for (int i = 0; i < alsoKnownAsArray.length(); i++) {
                                JSONObject ob = alsoKnownAsArray.getJSONObject(i);

                                DecimalFormat formatter = new DecimalFormat("#,###,###");
                                // String yourFormattedString = formatter.format( Double.parseDouble(object.getString("prnBalance")));

                                txtInstallmentDate.setText(ob.getString("installmentStartDate"));
                                txtLoanAmount.setText(("KES " + formatter.format(Double.parseDouble(ob.getString("disbursedAmount")))));
                                txtTerm.setText(ob.getString("repaymentTerm"));
                                txtInstallmentDate.setText(ob.getString("installmentStartDate"));
                                txtMaturityDate.setText(ob.getString("maturityDate"));
                                txtAccountNumber.setText(pref.getAccID());
                                txtInstallmentAmount.setText("KES " + formatter.format(Double.parseDouble(ob.getString("installmentAmount"))));
                            }
                        }
                    }
                }
            } catch (Exception e) {
                Toast.makeText(LoanConfirmation.this, Log.getStackTraceString(e) + "", Toast.LENGTH_LONG).show();
                Log.i(TAG, Log.getStackTraceString(e));
            }
        }, error -> {
            Log.e("error", error.toString());
            //     progress.dismiss();
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<String, String>();
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