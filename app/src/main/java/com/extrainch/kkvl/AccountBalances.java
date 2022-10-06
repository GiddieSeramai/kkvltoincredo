package com.extrainch.kkvl;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
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
import com.extrainch.kkvl.adapter.Balances_Adapter;
import com.extrainch.kkvl.databinding.ActivityAccountBalancesBinding;
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

public class AccountBalances extends AppCompatActivity {
    final Context context = this;
    ActivityAccountBalancesBinding binding;
    Portfolio portfolio;
    ArrayList<Portfolio> portfolios = new ArrayList<>();
    ArrayList<String> product, accountID, accountBal;
    Balances_Adapter adapter;
    Typeface typeface;
    private String TAG = AccountBalances.class.getSimpleName();
    private MyPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAccountBalancesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        typeface = Typeface.createFromAsset(getApplicationContext().getAssets(), "font/red_hat_display_regular.otf");

        pref = new MyPreferences(AccountBalances.this);

        fetchClientPortFolio(Constants.BASE_URL + "MobileClient/FetchClientPortfolio");

        binding.backPressed.setOnClickListener(v -> {
            startActivity(new Intent(this, Dashboard_increado.class)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        });
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    public void fetchClientPortFolio(String url) {
        final ProgressDialog progressDialog = new ProgressDialog(AccountBalances.this);
        progressDialog.setCancelable(false); // set cancelable to false
        progressDialog.setMessage("Fetching Balances..."); // set message
        progressDialog.show();

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        JSONObject jsonObject = new JSONObject();

//        binding.spAccounts.setVisibility(View.VISIBLE);
        try {
            jsonObject.put("OurBranchID", pref.getOurBranchID());
            jsonObject.put("ClientID", pref.getClientID());
            jsonObject.put("TokenCode", pref.getAuthToken());

            Log.d(TAG, "fetchClientPortFolio" + jsonObject);
        } catch (final JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(com.android.volley.Request.Method.POST, url, jsonObject, response -> {
//            binding.spAccounts.setVisibility(View.GONE);
            progressDialog.dismiss();
            try {
                Log.d("LLL", response.toString());
                JSONObject object = new JSONObject(response.toString());

                if (object.getString("code").equals("300")) {
                    final Dialog dialog = new Dialog(context);
                    dialog.setContentView(R.layout.dialog_alert);
                    dialog.setCancelable(true);
                    Button okbtn = dialog.findViewById(R.id.dialogButtonOK);

                    TextView txtBody = dialog.findViewById(R.id.tv_response_id);
                    txtBody.setText("Your session has expired. Kindly relogin to continue");
                    okbtn.setOnClickListener(v -> {
                        dialog.dismiss();
                        Intent i = new Intent(AccountBalances.this, LoginActivity.class);
                        startActivity(i);
                    });

                    dialog.getWindow().getAttributes().windowAnimations = R.anim.dialog_in;
                    dialog.show();
                } else if (object.getString("code").equals("200")) {

                    String s = object.getString("data");
                    try {
                        JSONArray array = new JSONArray(s);
                        Log.i(TAG, "Response " + s);
                        product = new ArrayList<>();
                        accountID = new ArrayList<>();
                        accountBal = new ArrayList<>();

                        for (int i = 0; i < array.length(); i++) {
                            JSONObject data = array.getJSONObject(i);

                            DecimalFormat formatter = new DecimalFormat("#,###,###");
                            //  String yourFormattedString = formatter.format( Double.parseDouble(object.getString("prnBalance")));
                            product.add(data.getString("productID"));
                            accountID.add(data.getString("accountID"));
                            accountBal.add(formatter.format(Double.parseDouble(data.getString("balance"))));

                            portfolio = new Portfolio(pref.getClientID(), data.getString("name"),
                                    data.getString("productID"),
                                    data.getString("accountID"),
                                    data.getString("productTypeID"),
                                    formatter.format(Double.parseDouble(data.getString("balance"))));

//                        userName.setText(object.getString("Name"));
                            portfolios.add(portfolio);
                            Log.i(TAG, s);
                        }

                        adapter = new Balances_Adapter(AccountBalances.this, product, accountID, accountBal);

//                    Toast.makeText(MainTransaction.this, adapter+"",
//                            Toast.LENGTH_LONG).show();
                        binding.lvAccount.setAdapter(adapter);

                        binding.lvAccount.setOnItemClickListener((parent, view, position, id) -> {
                            portfolio = portfolios.get(position);
                            String accountID = portfolio.getAccountID();
                            String name = portfolio.getName();
                            String balance = portfolio.Balance;
                            pref.setAccID(accountID);
                            pref.setAccountDetails(accountID, name, balance);
                            final Dialog dialog = new Dialog(context);
                            dialog.setContentView(R.layout.dialog_logout);
                            dialog.setCancelable(false);
                            Button okbtn = dialog.findViewById(R.id.dialogBtnOK);
                            Button cancelBtn = dialog.findViewById(R.id.dialogBtnCancel);
                            TextView txtBody = dialog.findViewById(R.id.tv_response_id);
                            txtBody.setText("Dear customer you are about to view your ministatement.");
                            txtBody.setTypeface(typeface);
                            okbtn.setTypeface(typeface);
                            cancelBtn.setTypeface(typeface);
                            // if button is clicked, close the custom dialog
                            pref.setAccountTypeId(portfolio.getAccountTypeID());

                            okbtn.setOnClickListener(v -> {

                                Intent i = new Intent(AccountBalances.this, Ministatement.class);
                                startActivity(i);
                                dialog.dismiss();
                            });
                            cancelBtn.setOnClickListener(v -> dialog.dismiss());
                            dialog.getWindow().getAttributes().windowAnimations = R.anim.dialog_in;
                            dialog.show();
                        });
                    } catch (Exception e) {
                        Toast.makeText(AccountBalances.this, Log.getStackTraceString(e) + "", Toast.LENGTH_LONG).show();
                        Log.i(TAG, Log.getStackTraceString(e));
                    }
                } else {
                    final Dialog dialog = new Dialog(context);
                    dialog.setContentView(R.layout.dialog_alert);
                    dialog.setCancelable(false);
                    Button okbtn = dialog.findViewById(R.id.dialogButtonOK);

                    TextView txtBody = dialog.findViewById(R.id.tv_response_id);
                    txtBody.setText("" + object.getString("msg"));
                    okbtn.setOnClickListener(v -> dialog.dismiss());

                    dialog.getWindow().getAttributes().windowAnimations = R.anim.dialog_in;
                    dialog.show();
                }
            } catch (Exception e) {
                Toast.makeText(AccountBalances.this, Log.getStackTraceString(e) + "", Toast.LENGTH_LONG).show();
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

}
