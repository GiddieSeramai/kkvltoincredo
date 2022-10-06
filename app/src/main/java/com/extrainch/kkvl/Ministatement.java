package com.extrainch.kkvl;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.extrainch.kkvl.adapter.AccountStatementAdapter;
import com.extrainch.kkvl.adapter.LoanStatementAdapter;
import com.extrainch.kkvl.databinding.ActivityMinistatementBinding;
import com.extrainch.kkvl.loans.LoanInstallment;
import com.extrainch.kkvl.models.LoanStatement;
import com.extrainch.kkvl.models.Statement;
import com.extrainch.kkvl.ui.dashboard.Dashboard_increado;
import com.extrainch.kkvl.utils.Constants;
import com.extrainch.kkvl.utils.MyPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Ministatement extends AppCompatActivity {
    private static String TAG = Ministatement.class.getSimpleName();
    public MyPreferences pref;
    ActivityMinistatementBinding binding;
    String accountId;
    String branchId;
    String closingBalValue = "";
    private List<LoanStatement> loanitems = new ArrayList<>();
    private List<Statement> items = new ArrayList<>();
    private RecyclerView recyclerView;
    private AccountStatementAdapter mAdapter;
    private LoanStatementAdapter mLoanAdapter;
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMinistatementBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        pref = new MyPreferences(Ministatement.this);

        mHandler = new Handler();

        recyclerView = new RecyclerView(this);

        binding.imBack.setOnClickListener(v -> {
            Intent i = new Intent(Ministatement.this, Dashboard_increado.class);
            startActivity(i);
        });


//        im_statement.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//            Intent i = new Intent(Ministatement.this,LoanInstallment.class);
//            startActivity(i);
//            }
//        });

        // attach click listener to folding cell
        binding.foldingCell.setOnClickListener(v -> binding.foldingCell.toggle(false));
        binding.txtInstallment.setOnClickListener(v -> {
            Intent i = new Intent(Ministatement.this, LoanInstallment.class);
            startActivity(i);
        });

        if (pref.getAccountTypeId().equals("Loans")) {
            binding.foldingCell.setVisibility(View.VISIBLE);
            fetchLoanStatment(Constants.BASE_URL + "MobileLoan/FetchLoanStatement");
        } else {
            binding.foldingCell.setVisibility(View.GONE);
            //ln_installmnt.setVisibility(View.GONE);
            fetchStatment(Constants.BASE_URL + "MobileClient/FetchAccountStatement");
        }

        if (pref.getAccID() == null || pref.getAccountName().equals("")) {
            Toast.makeText(getApplicationContext(), "No account Details", Toast.LENGTH_LONG).show();

        } else {
            accountId = pref.getAccID();
            branchId = "01";
            Log.i(TAG, branchId);


            setTitle(accountId + "  - Account Statement");
            binding.name.setText("" + pref.getAccountName());
            binding.accNumber.setText("" + accountId);

            //   clearBalance.setText("Clear Balance: KES" + AbsoluteValue.abs((int) Float.parseFloat(pref.getAcBalance())));
            //  closingBalance.setText("UGX " + AbsoluteValue.abs((int) Float.parseFloat(getIntent().getStringExtra("ClearBalance"))));

        }
        CoordinatorLayout layout = new CoordinatorLayout(this);
        binding.rootLayout.setForeground(new ColorDrawable(Color.BLACK));
        binding.rootLayout.getForeground().setAlpha(0);

        // layout.addView(mSwipeRefreshLayout);


        layout.addView(recyclerView);
        //   mSwipeRefreshLayout.addView(recyclerView);

        CoordinatorLayout.LayoutParams params = new CoordinatorLayout.LayoutParams(
                CoordinatorLayout.LayoutParams.MATCH_PARENT, CoordinatorLayout.LayoutParams.MATCH_PARENT);
        params.gravity = Gravity.BOTTOM | Gravity.END;
        params.setMargins(6, 6, 6, 6);

        binding.rootLayout.addView(layout);
        recyclerView.setLayoutParams(params);

        //  recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mAdapter = new AccountStatementAdapter(items);
        mLoanAdapter = new LoanStatementAdapter(loanitems);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        //  recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        //fetchLoanDetails();
        supremeLoanDetails(Constants.BASE_URL + "MobileLoan/FetchLoanDetails");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(Ministatement.this, Dashboard_increado.class);
        startActivity(i);
    }


    public void fetchStatment(String url) {
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("OurBranchID", pref.getOurBranchID());
            jsonObject.put("AccountID", pref.getAccID());
            jsonObject.put("TokenCode", pref.getAuthToken());
            Log.d(TAG, "fetchStatement" + jsonObject);
        } catch (final JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(com.android.volley.Request.Method.POST, url, jsonObject, response -> {
            try {
                JSONObject obj = new JSONObject(response.toString());
                String s = obj.getString("data");
                Log.d(TAG, "fetchStatement" + s);
                if (!items.isEmpty())
                    items.clear();
                //  {"Status":"Fail","Remarks":"The number of rows provided for a FETCH clause must be greater then zero."}
                Object json = new JSONTokener(s).nextValue();
                if (json instanceof JSONArray) {

                    JSONArray array = new JSONArray(s);
                    if (array.length() == 0) {
                        binding.noDetails.setVisibility(View.VISIBLE);
                        binding.rootLayout.setVisibility(View.GONE);
                        binding.lnNoBalance.setVisibility(View.GONE);
                    } else {
//                            {
//                                "ColumnID":"2", "TrxDate":"8/19/2016 12:00:00 AM", "ValueDate":
//                                "8/19/2016 12:00:00 AM",
//                                        "Particulars":"Disbursement of loan", "Credit":null,
//                                    "Debit":"2500000.0000", "Closing":"-2500000.0000",
//                                    "OperatorID":"GEORGE", "SupervisedID":null
//                            }
                        String credit = "";
                        String debit = "";
                        String trxDate = "";
                        binding.noDetails.setVisibility(View.GONE);
                        binding.rootLayout.setVisibility(View.VISIBLE);

                        for (int i = 0; i < array.length(); i++) {
                            JSONObject object = array.getJSONObject(i);

                            //   Log.d(TAG, result);
                            DecimalFormat formatter = new DecimalFormat("#,###,###");

                            Statement statement = new Statement(object.getString("valueDate"), object.getString("particulars"),
                                    formatter.format(Double.parseDouble(object.getString("debit"))),
                                    formatter.format(Double.parseDouble(object.getString("credit"))),
                                    "KES " + formatter.format(Double.parseDouble(object.getString("amount")))
                                    , "KES " + formatter.format(Double.parseDouble(object.getString("runningTotal"))));

                            items.add(statement);
                            closingBalValue = "KES " + (object.getString("runningTotal"));

                            binding.closingBalance.setText(closingBalValue);

                            mAdapter.notifyDataSetChanged();

                            recyclerView.setAdapter(mAdapter);
                        }
                    }
                } else {
                    JSONObject object = new JSONObject(s);
                    if (object.getString("Status").equalsIgnoreCase("Fail")) {
                        AlertDialog.Builder alert = new AlertDialog.Builder(Ministatement.this);
                        alert.setMessage(object.getString("Remarks"));
                        alert.setCancelable(false);
                        alert.setPositiveButton("Ok", (dialog, which) -> {
                            dialog.dismiss();
                            finish();
                        });
                        alert.show();
                    }
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

    public void fetchLoanStatment(String url) {
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("OurBranchID", pref.getOurBranchID());
            jsonObject.put("AccountID", pref.getAccID());
            jsonObject.put("TokenCode", pref.getAuthToken());

            Log.d(TAG, "fetchStatement" + jsonObject);
        } catch (final JSONException e) {
            e.printStackTrace();

        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(com.android.volley.Request.Method.POST, url, jsonObject, response -> {
            try {
                JSONObject obj = new JSONObject(response.toString());
                String s = obj.getString("data");
                Log.d(TAG, "fetchStatement" + s);
                if (!loanitems.isEmpty())
                    loanitems.clear();
                //  {"Status":"Fail","Remarks":"The number of rows provided for a FETCH clause must be greater then zero."}
                Object json = new JSONTokener(s).nextValue();
                if (json instanceof JSONArray) {

                    JSONArray array = new JSONArray(s);
                    if (array.length() == 0) {
                        binding.noDetails.setVisibility(View.VISIBLE);
                        binding.rootLayout.setVisibility(View.GONE);
                        binding.lnNoBalance.setVisibility(View.GONE);
                    } else {
                        String credit = "";
                        String debit = "";
                        String trxDate = "";
                        binding.noDetails.setVisibility(View.GONE);
                        binding.rootLayout.setVisibility(View.VISIBLE);

                        for (int i = 0; i < array.length(); i++) {
                            JSONObject object = array.getJSONObject(i);
                            DecimalFormat formatter = new DecimalFormat("#,###,###");
                            LoanStatement statement = new LoanStatement(object.getString("trxDate"), object.getString("trxDescription"), formatter.format(Double.parseDouble(object.getString("amount"))));
                            loanitems.add(statement);
                            mLoanAdapter.notifyDataSetChanged();
                            recyclerView.setAdapter(mLoanAdapter);
                        }
                    }
                } else {
                    JSONObject object = new JSONObject(s);
                    if (object.getString("Status").equalsIgnoreCase("Fail")) {
                        AlertDialog.Builder alert = new AlertDialog.Builder(Ministatement.this);
                        alert.setMessage(object.getString("Remarks"));
                        alert.setCancelable(false);
                        alert.setPositiveButton("Ok", (dialog, which) -> {
                            dialog.dismiss();
                            finish();
                        });
                        alert.show();
                    }
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

    public void supremeLoanDetails(String url) {
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("OurBranchID", pref.getOurBranchID());
            jsonObject.put("ClientID", pref.getClientID());
            jsonObject.put("AccountID", accountId);
            jsonObject.put("LoanSeries", "1");
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
                if (object.getString("code").equals("200")) {

                    if (object.getString("msg").equals("Success")) {
                        //  JSONObject data = new JSONObject(object.getString("data"));
                        Log.d("BarD", object.getString("data"));
                        JSONArray dat = new JSONArray(object.getString("data"));
                        JSONArray alsoKnownAsArray = dat;
                        for (int i = 0; i < alsoKnownAsArray.length(); i++) {
                            JSONObject ob = alsoKnownAsArray.getJSONObject(i);

                            DecimalFormat formatter = new DecimalFormat("#,###,###");
                            // String yourFormattedString = formatter.format( Double.parseDouble(object.getString("prnBalance")));

                            binding.lnAmount.setText(formatter.format(Double.parseDouble(ob.getString("disbursedAmount"))));
                            binding.lnDueDate.setText(ob.getString("maturityDate"));
                            binding.lnBalance.setText(formatter.format(Double.parseDouble(ob.getString("loanBalance"))));
                            binding.term.setText(ob.getString("repaymentTerm"));
                            binding.frequency.setText(ob.getString("repaymentFrequency"));
                        }
                    } else if (object.getString("code").equals("300")) {
                        final Dialog dialog = new Dialog(getApplicationContext());
                        dialog.setContentView(R.layout.dialog_alert);
                        dialog.setCancelable(false);
                        Button okbtn = dialog.findViewById(R.id.dialogButtonOK);

                        TextView txtBody = dialog.findViewById(R.id.tv_response_id);
                        txtBody.setText("Your session has expired. Kindly relogin to continue");
                        okbtn.setOnClickListener(v -> {
                            dialog.dismiss();
                            Intent i = new Intent(Ministatement.this, LoginActivity.class);
                            startActivity(i);
                        });
                        dialog.getWindow().getAttributes().windowAnimations = R.anim.dialog_in;
                        dialog.show();
                    }
                }
            } catch (Exception e) {
                Toast.makeText(Ministatement.this, Log.getStackTraceString(e) + "", Toast.LENGTH_LONG).show();
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
}