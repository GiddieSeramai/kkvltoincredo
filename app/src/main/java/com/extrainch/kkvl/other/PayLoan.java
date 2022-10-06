package com.extrainch.kkvl.other;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.extrainch.kkvl.R;
import com.extrainch.kkvl.adapter.Accounts_Adapter;
import com.extrainch.kkvl.databinding.ActivityPayLoanBinding;
import com.extrainch.kkvl.models.Portfolio;
import com.extrainch.kkvl.ui.dashboard.Dashboard_increado;
import com.extrainch.kkvl.utils.Constants;
import com.extrainch.kkvl.utils.MyPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PayLoan extends AppCompatActivity {
    final Context context = this;
    ActivityPayLoanBinding binding;
    String TAG = PayLoan.class.getSimpleName();
    MyPreferences pref;
    ArrayList<String> product, accountID, accountBal;
    //    ArrayList<Portfolio> portfolios = new ArrayList<Portfolio>();
    ArrayList<String> portfolios = new ArrayList<>();
    Portfolio portfolio;
    Accounts_Adapter adapter;
    ArrayAdapter<String> loansAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPayLoanBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        pref = new MyPreferences(PayLoan.this);
        fetchClientPortFolio(Constants.BASE_URL + "MobileClient/FetchClientPortfolio");

//        binding.etLoanAccount.setText(pref.getClientID());


        binding.etPhoneNo.setText(pref.getPhoneNumber());

        binding.txtPay.setOnClickListener(v -> {
            if (binding.rdRadiogroup.getCheckedRadioButtonId() == -1) {
                Toast.makeText(PayLoan.this, "Kindly choose account payment mode", Toast.LENGTH_LONG).show();
            }
        });

        binding.backPressed.setOnClickListener(v -> startActivity(new Intent(this, Dashboard_increado.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)));

        binding.rdRadiogroup.setOnCheckedChangeListener((group, checkedId) -> {

            if (checkedId == R.id.rdSelf) {
                binding.tvPhoneNo.setVisibility(View.VISIBLE);
                binding.etPhoneNo.setVisibility(View.VISIBLE);
                binding.txtPay.setOnClickListener(v -> supremeLoanPayment("https://197.248.16.77:1400/Bridge/api/Request/StkPush"));

            } else if (checkedId == R.id.rdWallet) {
                binding.tvPhoneNo.setVisibility(View.GONE);
                binding.etPhoneNo.setVisibility(View.GONE);
                binding.txtPay.setOnClickListener(v -> PayFromWallet(Constants.BASE_URL + "MobileLoan/PayKaribuKashLoan"));
            }

        });
    }

    private void PayFromWallet(String url) {
        if (TextUtils.isEmpty(binding.etPayAmount.getText().toString().trim())) {
            binding.etPayAmount.setError("Kindly enter amount to pay");
        } else {
            ProgressDialog progressDialog = new ProgressDialog(PayLoan.this);
            progressDialog.setMessage("Processing Payment...");
            progressDialog.show();

            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            JSONObject jsonObject = new JSONObject();
            try {
                binding.spPayLoan.setVisibility(View.VISIBLE);
                //string.replace("to", "xyz");
                jsonObject.put("OurBranchID", pref.getOurBranchID());
                jsonObject.put("ClientID", pref.getClientID());
                jsonObject.put("AccountID", pref.getLoanID());
                jsonObject.put("TokenCode", pref.getAuthToken());

                Log.d("Loan Payment", jsonObject.toString());
            } catch (final JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(com.android.volley.Request.Method.POST, url, jsonObject, response -> {
                binding.spPayLoan.setVisibility(View.GONE);
                // progress.dismiss();
                try {
                    Log.d("LLL", response.toString());

                    JSONObject object = new JSONObject(response.toString());
                    if (response.getString("code").equals("200")) {
                        progressDialog.dismiss();

                        final Dialog dialog = new Dialog(context);
                        dialog.setContentView(R.layout.dialog_alert);
                        dialog.setCancelable(false);
                        Button okbtn = dialog.findViewById(R.id.dialogButtonOK);

                        TextView txtBody = dialog.findViewById(R.id.tv_response_id);
                        txtBody.setText("Dear " + pref.getAccountName() + " your request to pay via your wallet has been recieved.");
                        okbtn.setOnClickListener(v -> {
//                            dialog.dismiss();
                            startActivity(new Intent(PayLoan.this, Dashboard_increado.class)
                                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        });

                        dialog.getWindow().getAttributes().windowAnimations = R.anim.dialog_in;
                        dialog.show();
                    } else {
                        final Dialog dialog = new Dialog(context);
                        dialog.setContentView(R.layout.dialog_logout);
                        dialog.setCancelable(false);
                        Button okbtn = dialog.findViewById(R.id.dialogBtnOK);
                        Button cancelBtn = dialog.findViewById(R.id.dialogBtnCancel);

                        TextView txtBody = dialog.findViewById(R.id.tv_response_id);
                        txtBody.setText("An error occured. Kindly try again later");
                        okbtn.setOnClickListener(v -> dialog.dismiss());
                        cancelBtn.setOnClickListener(v -> dialog.dismiss());
                        dialog.getWindow().getAttributes().windowAnimations = R.anim.dialog_in;
                        dialog.show();
                    }
                } catch (Exception e) {
                    Toast.makeText(PayLoan.this, Log.getStackTraceString(e) + "", Toast.LENGTH_LONG).show();
                    Log.i(TAG, Log.getStackTraceString(e));
                }
            }, error -> {
                binding.spPayLoan.setVisibility(View.GONE);
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
    }

    public void supremeLoanPayment(String url) {
        if (TextUtils.isEmpty(binding.etPhoneNo.getText().toString().trim())) {
            binding.etPhoneNo.setError("Kindly enter phone number");
        } else if (TextUtils.isEmpty(binding.etPayAmount.getText().toString().trim())) {
            binding.etPayAmount.setError("Kindly enter amount to pay");
        } else {
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            JSONObject jsonObject = new JSONObject();
            try {
                binding.spPayLoan.setVisibility(View.VISIBLE);
                //string.replace("to", "xyz");
                jsonObject.put("phonenumber", "254" + pref.getPhoneNumber().substring(pref.getPhoneNumber().length() - 9));
                jsonObject.put("Amount", Integer.parseInt(binding.etPayAmount.getText().toString()));
                jsonObject.put("accountReference", pref.getLoanID());
                jsonObject.put("transactionDesc", "Loan Payment");
                jsonObject.put("TokenCode", pref.getAuthToken());
                //  {"phonenumber":"254717002320","Amount":10,"accountReference":"BL000000089","transactionDesc":"Loan"}


                //  {"phonenumber":"254717002320","Amount":10,"accountReference":"BL000000089","transactionDesc":"Loan"}
                Log.d("Loan Payment", jsonObject.toString());

            } catch (final JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(com.android.volley.Request.Method.POST, url, jsonObject, response -> {
                binding.spPayLoan.setVisibility(View.GONE);
                // progress.dismiss();
                try {
                    Log.d("LLL", response.toString());

                    JSONObject object = new JSONObject(response.toString());
                    if (object.getString("IsSuccess").equals("true")) {
                        {
                            final Dialog dialog = new Dialog(context);
                            dialog.setContentView(R.layout.dialog_alert);
                            dialog.setCancelable(false);
                            Button okbtn = dialog.findViewById(R.id.dialogButtonOK);

                            TextView txtBody = dialog.findViewById(R.id.tv_response_id);
                            txtBody.setText("Dear " + pref.getAccountName() + " your request to pay via mpesa no " + pref.getPhoneNumber() + " has been recieved. Enter your Mpesa PIN on the prompt");
                            okbtn.setOnClickListener(v -> dialog.dismiss());

                            dialog.getWindow().getAttributes().windowAnimations = R.anim.dialog_in;
                            dialog.show();
                        }
                    } else {

                        final Dialog dialog = new Dialog(context);
                        dialog.setContentView(R.layout.dialog_logout);
                        dialog.setCancelable(false);
                        Button okbtn = dialog.findViewById(R.id.dialogBtnOK);
                        Button cancelBtn = dialog.findViewById(R.id.dialogBtnCancel);

                        TextView txtBody = dialog.findViewById(R.id.tv_response_id);
                        txtBody.setText("An error occured. Kindly try again later");
                        okbtn.setOnClickListener(v -> dialog.dismiss());
                        cancelBtn.setOnClickListener(v -> dialog.dismiss());
                        dialog.getWindow().getAttributes().windowAnimations = R.anim.dialog_in;
                        dialog.show();
                    }
                } catch (Exception e) {
                    Toast.makeText(PayLoan.this, Log.getStackTraceString(e) + "", Toast.LENGTH_LONG).show();
                    Log.i(TAG, Log.getStackTraceString(e));
                }
            }, error -> {
                binding.spPayLoan.setVisibility(View.GONE);
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
    }

    public void fetchClientPortFolio(String url) {
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("OurBranchID", pref.getOurBranchID());
            jsonObject.put("ClientID", pref.getClientID());
            jsonObject.put("TokenCode", pref.getAuthToken());

            Log.d(TAG, "fetchClientPortFolio" + jsonObject);
        } catch (final JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(com.android.volley.Request.Method.POST, url, jsonObject, response -> {
            try {
                JSONObject object = new JSONObject(response.toString());
                String s = object.getString("data");
                Log.d("loans", s);
                JSONArray array = new JSONArray(s);
                if (array.length() == 0) {

                } else {
                    for (int i = 0; i < array.length(); i++) {
                        DecimalFormat formatter = new DecimalFormat("#,###,###");
                        JSONObject data = array.getJSONObject(i);

//                        product.add(data.getString("productID"));
//                        accountID.add(data.getString("accountID"));
                        String balance = formatter.format(Double.parseDouble(data.getString("balance")));
                        String productId = data.getString("productID");
                        String accountId = data.getString("accountID");
//
                        portfolios.add(productId + ":" + accountId);
                        loansAdapter = new ArrayAdapter<>(PayLoan.this, android.R.layout.simple_spinner_item, portfolios);
                        loansAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        binding.etLoanAccount.setAdapter(loansAdapter);

                        binding.etLoanAccount.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                String brT = binding.etLoanAccount.getSelectedItem().toString().trim();
                                String[] br = brT.split(":");
                                String item = br[1];
                                pref.setLoanID(item);

                                binding.etPayAmount.setText(balance);
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {

                            }
                        });
                    }
                    Log.i(TAG, "");
                }
            } catch (Exception e) {
                Log.i(TAG, Log.getStackTraceString(e));
            }
        }, error -> Log.e("error", error.toString())) {
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