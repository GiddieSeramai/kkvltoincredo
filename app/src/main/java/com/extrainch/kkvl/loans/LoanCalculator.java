package com.extrainch.kkvl.loans;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
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
import com.extrainch.kkvl.adapter.CalculatorAdapter;
import com.extrainch.kkvl.adapter.Products_Adapter;
import com.extrainch.kkvl.models.Calculator;
import com.extrainch.kkvl.models.Product;
import com.extrainch.kkvl.utils.Constants;
import com.extrainch.kkvl.utils.MyPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoanCalculator extends AppCompatActivity {

    MyPreferences pref;
    String TAG = LoanCalculator.class.getSimpleName();
    ImageView im_back;
    EditText etLoanTerm, etInterestRate, etLoanAmount;
    Button btnCalculate;
    LinearLayout lnViews, lblLoans;
    ProgressDialog dialog;
    ArrayList<String> product, accountID, accountBal, termFrom, term_to;
    ArrayList<Product> products = new ArrayList<Product>();
    Product portfolio;
    Products_Adapter adapter;
    private List<Calculator> calculatorList = new ArrayList<>();
    private CalculatorAdapter mAdapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loan_calculator);
        mAdapter = new CalculatorAdapter(calculatorList);
        etLoanTerm = findViewById(R.id.et_term);
        etInterestRate = findViewById(R.id.et_product);
        etLoanAmount = findViewById(R.id.et_loan_amount);
        lblLoans = findViewById(R.id.lblLoans);
        lnViews = findViewById(R.id.ln_views);
        dialog = new ProgressDialog(LoanCalculator.this);

        lblLoans.setVisibility(View.GONE);
        lnViews.setVisibility(View.GONE);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        pref = new MyPreferences(LoanCalculator.this);

        btnCalculate = findViewById(R.id.btn_calculate);

        btnCalculate.setOnClickListener(v -> {
            //   Toast.makeText(getApplicationContext(), "", Toast.LENGTH_LONG).show();
            calculatorList.clear();
            if (TextUtils.isEmpty(etLoanAmount.getText().toString())) {
                etLoanAmount.setError("Loan Amount is required");
            } else if (TextUtils.isEmpty(etInterestRate.getText().toString())) {
                etInterestRate.setError("Interest Rate is required");
            } else if (TextUtils.isEmpty(etLoanTerm.getText().toString())) {
                etLoanTerm.setError("Term is required");
            } else {
                //getLoanInstallment();
                calculateLoan(Constants.BASE_URL + "MobileLoan/LoanCalculator");
            }
        });
    }

    public void calculateLoan(String url) {
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("LoanAmount", Double.parseDouble(etLoanAmount.getText().toString()));
            jsonObject.put("InterestRate", Double.parseDouble(etInterestRate.getText().toString()));
            jsonObject.put("CalculationMethodID", "FN");
            jsonObject.put("PeriodTypeID", "M");
            jsonObject.put("FrequencyID", "M");
            jsonObject.put("LoanTerm", Double.parseDouble(etLoanTerm.getText().toString()));
            jsonObject.put("NoOfInstallments", Double.parseDouble(etLoanTerm.getText().toString()));
            jsonObject.put("Rule78", 0);
            jsonObject.put("TokenCode", pref.getAuthToken());
            jsonObject.put("TokenCode", pref.getAuthToken());
            jsonObject.put("TokenCode", pref.getAuthToken());
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
                    Toast.makeText(LoanCalculator.this, "Error loading installments", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                } else {
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object = array.getJSONObject(i);
                        lnViews.setVisibility(View.VISIBLE);
                        lblLoans.setVisibility(View.VISIBLE);

                        DecimalFormat formatter = new DecimalFormat("#,###,###");
                        String yourFormattedString = formatter.format(Double.parseDouble(object.getString("prnBalance")));

                        Calculator calculator = new Calculator(
                                object.getString("prnBalance"),
                                formatter.format(Double.parseDouble(object.getString("prnBalance"))),
                                formatter.format(Double.parseDouble(object.getString("intAmount"))),
                                formatter.format(Double.parseDouble(object.getString("instAmount"))));
                        calculatorList.add(calculator);
                    }

                    mAdapter.notifyDataSetChanged();
                    recyclerView.setAdapter(mAdapter);
                    dialog.dismiss();
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

    public void fetchProducts(String url) {
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("OurBranchID", pref.getOurBranchID());
            jsonObject.put("ClientID", pref.getClientID());

            Log.d("VERIFYCODE", "CC" + jsonObject);
        } catch (final JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(com.android.volley.Request.Method.POST, url, jsonObject, response -> {
            try {
                Log.d("LLL", response.toString());
                JSONObject object = new JSONObject(response.toString());
                String s = object.getString("data");

                if (object.getString("code").equals("200")) {
                    {
                        try {
                            JSONArray array = new JSONArray(s);
                            Log.i(TAG, "Response " + s);

                            product = new ArrayList<>();
                            accountID = new ArrayList<>();
                            accountBal = new ArrayList<>();
                            termFrom = new ArrayList<>();
                            term_to = new ArrayList<>();

                            for (int i = 0; i < array.length(); i++) {
                                JSONObject data = array.getJSONObject(i);
                                product.add(data.getString("productID"));
                                accountID.add(data.getString("description"));
                                accountBal.add(data.getString("effectiveRate"));
                                termFrom.add("Min Term " + data.getString("termFrom"));
                                term_to.add("Max Term " + data.getString("termTo"));

                                portfolio = new Product(data.getString("productID"),
                                        data.getString("description"),
                                        data.getString("effectiveRate"),
                                        data.getString("termFrom"),
                                        data.getString("termTo"));
                                products.add(portfolio);
                                Log.i(TAG, s);
                            }


                            final Dialog dialog = new Dialog(LoanCalculator.this);
                            dialog.setContentView(R.layout.product_list);
                            dialog.setCancelable(false);

                            Button cancelBtn = dialog.findViewById(R.id.dialogBtnCancel);

                            cancelBtn.setOnClickListener(v -> dialog.dismiss());
                            ListView lv_account_d = dialog.findViewById(R.id.d_lv_account);


                            adapter = new Products_Adapter(LoanCalculator.this, product, accountID, accountBal, termFrom, term_to);

                            lv_account_d.setAdapter(adapter);

                            lv_account_d.setOnItemClickListener((parent, view, position, id) -> {
                                portfolio = products.get(position);
                                String rate = portfolio.getEffectiveRate();
                                etInterestRate.setText(portfolio.getEffectiveRate());
                                etLoanTerm.setText(portfolio.getTermTo());
//                                        pref.setAccountTypeId(portfolio.getAccountTypeID());
//                                        String name = portfolio.getName();
//                                        String balance = portfolio.Balance;
//                                        pref.setAccID(accountID);
//                                        pref.setAccountDetails(accountID, name, balance);
                                dialog.dismiss();
                            });
                            dialog.getWindow().getAttributes().windowAnimations = R.anim.dialog_in;
                            dialog.show();
                        } catch (Exception e) {
                            Toast.makeText(LoanCalculator.this, Log.getStackTraceString(e) + "", Toast.LENGTH_LONG).show();
                            Log.i(TAG, Log.getStackTraceString(e));
                        }
                    }
                } else {
                    final Dialog dialog = new Dialog(getApplicationContext());
                    dialog.setContentView(R.layout.dialog_alert);
                    dialog.setCancelable(false);
                    Button okbtn = (Button) dialog.findViewById(R.id.dialogButtonOK);

                    TextView txtBody = dialog.findViewById(R.id.tv_response_id);
                    txtBody.setText("" + object.getString("msg"));
                    okbtn.setOnClickListener(v -> dialog.dismiss());
                    dialog.getWindow().getAttributes().windowAnimations = R.anim.dialog_in;
                    dialog.show();
                }
            } catch (Exception e) {
                Toast.makeText(LoanCalculator.this, Log.getStackTraceString(e) + "", Toast.LENGTH_LONG).show();
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