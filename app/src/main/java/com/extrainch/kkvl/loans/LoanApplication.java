package com.extrainch.kkvl.loans;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.extrainch.kkvl.LoginActivity;
import com.extrainch.kkvl.R;
import com.extrainch.kkvl.adapter.Products_Adapter;
import com.extrainch.kkvl.databinding.ActivityLoanApplicationBinding;
import com.extrainch.kkvl.models.Product;
import com.extrainch.kkvl.other.MyNotificationManager;
import com.extrainch.kkvl.ui.dashboard.Dashboard_increado;
import com.extrainch.kkvl.utils.Constants;
import com.extrainch.kkvl.utils.MyPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoanApplication extends AppCompatActivity {

    final Context context = this;
    ActivityLoanApplicationBinding binding;
    String applicationID;
    float netAmount, netTerm;
    MyPreferences pref;
    Typeface font_bold;
    ArrayList<Product> products = new ArrayList<>();
    String TAG = LoanApplication.class.getSimpleName();
    ArrayList<String> product, accountID, accountBal, termFrom, term_to;
    Product loanProducts;
    Products_Adapter adapter;
    String interest_rate, product_id, i_due_amount, i_product, i_total_amount, i_due_date, loanAmount, loanType;
    private ProgressDialog progress;

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
            ex.printStackTrace();
        }

        return "02:00:00:00:00:00";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoanApplicationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        progress = new ProgressDialog(LoanApplication.this);
        pref = new MyPreferences(LoanApplication.this);

//        et_loan_term.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                fetchProducts("https://197.232.33.44:56759/SupremeApp/api/MobileLoan/FetchProductInterest");
//            }
//
//        });
        binding.txtContinue.setOnClickListener(v -> {
            if (binding.rdRadiogroup.getCheckedRadioButtonId() == -1) {
                Toast.makeText(LoanApplication.this, "Kindly choose the type of loan you need", Toast.LENGTH_LONG).show();
            }
        });

        binding.rdRadiogroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rdNew) {
                loanType = "N";
                binding.txtContinue.setOnClickListener(v -> {

//                loanAmount = binding.edLoanAmount.getText().toString();
                    //fetchProducts("https://197.232.33.44:56759/SupremeApp/api/MobileLoan/FetchProductInterest");
                    supremeConfirmLoan(Constants.BASE_URL + "MobileLoan/LoanConfirmation", loanType);
//            }
                });

            } else if (checkedId == R.id.rdTopUp) {
                loanType = "T";
                binding.txtContinue.setOnClickListener(v -> {

//                loanAmount = binding.edLoanAmount.getText().toString();
                    //fetchProducts("https://197.232.33.44:56759/SupremeApp/api/MobileLoan/FetchProductInterest");
                    supremeConfirmLoan(Constants.BASE_URL + "MobileLoan/LoanConfirmation", loanType);
//            }
                });
            }

        });

        binding.etLoanPurpose.setOnClickListener(v -> {
            AlertDialog.Builder builderSingle = new AlertDialog.Builder(LoanApplication.this);
            builderSingle.setIcon(R.drawable.ic_incredo_logo);
            builderSingle.setTitle("Select One Purpose:-");

            final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(LoanApplication.this, android.R.layout.select_dialog_singlechoice);
            arrayAdapter.add("Education");
            arrayAdapter.add("Medical");
            arrayAdapter.add("Emergency");
            arrayAdapter.add("Other");

            builderSingle.setNegativeButton("cancel", (dialog, which) -> dialog.dismiss());

            builderSingle.setAdapter(arrayAdapter, (dialog, which) -> {
                String strName = arrayAdapter.getItem(which);

                binding.etLoanPurpose.setText(strName);
//                        AlertDialog.Builder builderInner = new AlertDialog.Builder(LoanApplication.this);
//                        builderInner.setMessage(strName);
//                       // builderInner.setTitle("Your Selected Item is");
//                        builderInner.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog,int which) {
//                                dialog.dismiss();
//                            }
//                        });
                //  builderInner.show();
            });
            builderSingle.show();

        });

        binding.backPressed.setOnClickListener(v -> {
            startActivity(new Intent(this, Dashboard_increado.class)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        });
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    public void supremeApplyLoan(String url) {
        if (TextUtils.isEmpty(binding.edLoanAmount.getText().toString())) {
            binding.edLoanAmount.setError("Loan Amount is Required");
        } else if (TextUtils.isEmpty(binding.etLoanTerm.getText().toString())) {
            binding.etLoanTerm.setError("Loan Term is Required");
        } else if (Float.parseFloat(binding.etLoanTerm.getText().toString()) < 1) {
            binding.etLoanTerm.setError("Loan Term cannot be less than one month");
        } else if (Float.parseFloat(binding.edLoanAmount.getText().toString()) < 1000) {
            binding.edLoanAmount.setError("Loan Amount cannot be lower than 1000KSH");
        } else {
//        binding.spApplication.setVisibility(View.VISIBLE);
            ProgressDialog progressDialog = new ProgressDialog(LoanApplication.this);
            progressDialog.setMessage("Applying Loan...");
            progressDialog.show();

            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("OurBranchID", pref.getOurBranchID());
                jsonObject.put("ClientID", pref.getClientID());
                jsonObject.put("Mobile", pref.getPhoneNumber());
                jsonObject.put("LoanTypeID", loanType);
                jsonObject.put("LoanTerm", binding.etLoanTerm.getText().toString());
                jsonObject.put("LoanProductID", product_id);
                jsonObject.put("LoanAmount", Integer.valueOf(binding.edLoanAmount.getText().toString()));
                jsonObject.put("TokenCode", pref.getAuthToken());
                Log.d("Loan Application", jsonObject.toString());

            } catch (final JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(com.android.volley.Request.Method.POST, url, jsonObject, response -> {
//            binding.spApplication.setVisibility(View.VISIBLE);
                try {
                    Log.d("LLL", response.toString());
                    JSONObject object = new JSONObject(response.toString());
//                binding.spApplication.setVisibility(View.GONE);

                    if (object.getString("code").equals("200")) {
                        progressDialog.dismiss();
                        if (object.getString("msg").equals("Success")) {
                            JSONObject data = new JSONObject(object.getString("data"));
                            pref.setAccID(data.getString("accountID"));
                            final Dialog dialog = new Dialog(context);
                            dialog.setContentView(R.layout.dialog_alert);
                            dialog.setCancelable(false);
                            Button okbtn = dialog.findViewById(R.id.dialogButtonOK);
                            TextView tv_response_id = dialog.findViewById(R.id.tv_response_id);
                            tv_response_id.setText("Loan has been submitted successfully");
                            // if button is clicked, close the custom dialog

                            okbtn.setOnClickListener(v -> {
                                dialog.dismiss();
                                Intent i = new Intent(LoanApplication.this, LoanConfirmation.class);
                                i.putExtra("DueDate", i_due_date);
                                i.putExtra("Amount", i_due_amount);
                                i.putExtra("Product", i_product);
                                i.putExtra("TotalAmount", i_due_amount);

                                Log.d("Intent awy", i_due_amount + "Prod" + i_product + "Amount" + i_due_amount + "Totall" + i_due_amount);
                                startActivity(i);
                            });

                            dialog.getWindow().getAttributes().windowAnimations = R.anim.dialog_in;
                            dialog.show();

                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                                int importance = NotificationManager.IMPORTANCE_HIGH;
                                NotificationChannel mChannel = new NotificationChannel(Constants.CHANNEL_ID, Constants.CHANNEL_NAME, importance);
                                mChannel.setDescription(Constants.CHANNEL_DESCRIPTION);
                                mChannel.enableLights(true);
                                mChannel.setLightColor(Color.RED);
                                mChannel.enableVibration(true);
                                mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                                mNotificationManager.createNotificationChannel(mChannel);
                            }
                            MyNotificationManager.getInstance(LoanApplication.this).displayNotification("Congratulations", " your loan is approved");
                        }
                    } else if (object.getString("code").equals("500")) {
                        progressDialog.dismiss();
                        final Dialog dialog = new Dialog(context);
                        dialog.setContentView(R.layout.dialog_alert);
                        dialog.setCancelable(false);
                        dialog.show();

                        Button okbtn = dialog.findViewById(R.id.dialogButtonOK);
                        TextView tv_response_id = dialog.findViewById(R.id.tv_response_id);
                        tv_response_id.setText(object.getString("msg"));
                        okbtn.setOnClickListener(v -> {
                            dialog.dismiss();
                            binding.etLoanTerm.setText("");
                            binding.edLoanAmount.setText("");
                            binding.etLoanPurpose.setText("");
                        });
                    }
                } catch (Exception e) {
                    Toast.makeText(LoanApplication.this, Log.getStackTraceString(e) + "", Toast.LENGTH_LONG).show();
                    Log.i(TAG, Log.getStackTraceString(e));
                }
            }, error -> {
                Log.e("error", error.toString());
                progressDialog.dismiss();
//            binding.spApplication.setVisibility(View.GONE);
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

    public void supremeConfirmLoan(String url, String loanType) {
        if (TextUtils.isEmpty(binding.edLoanAmount.getText().toString())) {
            binding.edLoanAmount.setError("Loan Amount is Required");
        } else if (TextUtils.isEmpty(binding.etLoanTerm.getText().toString())) {
            binding.etLoanTerm.setError("Loan Term is Required");
        } else if (Float.parseFloat(binding.etLoanTerm.getText().toString()) < 1) {
            binding.etLoanTerm.setError("Loan Term cannot be less than one month");
        } else if (Float.parseFloat(binding.edLoanAmount.getText().toString()) < 1000) {
            binding.edLoanAmount.setError("Loan Amount cannot be lower than 1000KSH");
        } else {
//        binding.spApplication.setVisibility(View.VISIBLE);
            ProgressDialog progressDialog = new ProgressDialog(LoanApplication.this);
            progressDialog.setMessage("Applying Loan...");
            progressDialog.show();

            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("OurBranchID", pref.getOurBranchID());
                jsonObject.put("ClientID", pref.getClientID());
                jsonObject.put("LoanTerm", binding.etLoanTerm.getText().toString());
                jsonObject.put("LoanTypeID", loanType);
                jsonObject.put("LoanAmount", binding.edLoanAmount.getText().toString());
                jsonObject.put("LoanPurpose", binding.etLoanPurpose.getText().toString());
                jsonObject.put("TokenCode", pref.getAuthToken());

                Log.d("Loan Application", jsonObject.toString());
            } catch (final JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(com.android.volley.Request.Method.POST, url, jsonObject, response -> {
//            binding.spApplication.setVisibility(View.VISIBLE);
                try {
                    Log.d("Loan Confirmations", response.toString());

                    JSONObject object = new JSONObject(response.toString());

                    if (object.getString("code").equals("300")) {
                        progressDialog.dismiss();
                        final Dialog dialog = new Dialog(context);
                        dialog.setContentView(R.layout.dialog_alert);
                        dialog.setCancelable(false);
                        Button okbtn = dialog.findViewById(R.id.dialogButtonOK);

                        TextView txtBody = dialog.findViewById(R.id.tv_response_id);
                        txtBody.setText("Your session has expired. Kindly relogin to continue");
                        okbtn.setOnClickListener(v -> {
                            dialog.dismiss();
                            Intent i = new Intent(LoanApplication.this, LoginActivity.class);
                            startActivity(i);
                        });

                        dialog.getWindow().getAttributes().windowAnimations = R.anim.dialog_in;
                        dialog.show();
                    } else if (object.getString("code").equals("200")) {
                        progressDialog.dismiss();
//                    if (object.getString("msg").equals("Success")) {
                        JSONObject data = new JSONObject(object.getString("data"));

                        pref.setNotification("Dear " + pref.getAccountName() + " kindly confirm the details below before borrowing");

                        final Dialog dialog = new Dialog(context);
                        dialog.setContentView(R.layout.dialog_loan);
                        dialog.setCancelable(false);
                        Button okbtn = dialog.findViewById(R.id.dialogBtnOK);
                        Button calcelBtn = dialog.findViewById(R.id.dialogBtnCancel);
                        TextView tv_response_id = dialog.findViewById(R.id.tv_response_id);
                        TextView tvAmount = dialog.findViewById(R.id.tv_amount);
                        TextView tvProduct = dialog.findViewById(R.id.tv_product);
                        TextView tvrate = dialog.findViewById(R.id.tv_rate);
                        TextView tvDueDate = dialog.findViewById(R.id.tv_due_date);
                        TextView tvCharges = dialog.findViewById(R.id.tv_charges);
                        tv_response_id.setText(pref.getNotification());
                        tvAmount.setText(binding.edLoanAmount.getText().toString());
                        tvProduct.setText(data.getString("description"));
                        tvrate.setText(data.getString("effectiveRate"));
                        tvDueDate.setText(data.getString("dueDate"));
                        tvCharges.setText(data.getString("charges"));
                        // if button is clicked, close the custom dialog
                        product_id = (data.getString("productID"));

                        i_due_amount = binding.edLoanAmount.getText().toString();
                        i_product = data.getString("description");
                        i_due_amount = binding.edLoanAmount.getText().toString();
                        i_due_date = binding.edLoanAmount.getText().toString();

                        okbtn.setOnClickListener(v -> {
                            dialog.dismiss();
                            supremeApplyLoan(Constants.BASE_URL + "MobileLoan/MobileLoanApplication");
                        });
                        calcelBtn.setOnClickListener(v -> {
                            progressDialog.dismiss();
                            dialog.dismiss();
//                        binding.spApplication.setVisibility(View.GONE);

                            binding.edLoanAmount.setText("");
                            binding.etLoanTerm.setText("");
                            binding.etLoanPurpose.setText("");
                        });
                        dialog.show();
//                    }

                    } else if (object.getString("code").equals("500")) {
                        progressDialog.dismiss();
//                    binding.spApplication.setVisibility(View.GONE);
                        final Dialog dialog = new Dialog(context);
                        dialog.setContentView(R.layout.dialog_alert);
                        dialog.setCancelable(false);
                        dialog.show();

                        Button okbtn = dialog.findViewById(R.id.dialogButtonOK);
                        TextView tv_response_id = dialog.findViewById(R.id.tv_response_id);
                        tv_response_id.setText(object.getString("msg"));
                        okbtn.setOnClickListener(v -> {
                            dialog.dismiss();
                            binding.etLoanTerm.setText("");
                            binding.edLoanAmount.setText("");
                            binding.etLoanPurpose.setText("");
                        });
                    }
                } catch (Exception e) {
                    Toast.makeText(LoanApplication.this, Log.getStackTraceString(e) + "", Toast.LENGTH_LONG).show();
                    Log.i(TAG, Log.getStackTraceString(e));
                }
            }, error -> {
                Log.e("error", error.toString());
//            binding.spApplication.setVisibility(View.GONE);
                progressDialog.dismiss();
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

                                int term_com = Integer.parseInt(binding.etLoanTerm.getText().toString());
                                int max_term = Integer.parseInt(data.getString("termTo"));

                                Log.d("All", binding.etLoanTerm.getText().toString() + "" + Integer.parseInt(data.getString("termTo")));

                                if (Integer.parseInt(binding.etLoanTerm.getText().toString()) >= Integer.parseInt(data.getString("termTo"))) {
//                              &&(Integer.parseInt(et_loan_term.getText().toString()) >= Integer.parseInt(data.getString("termFrom")))) {
                                    Log.d("CVCVC", binding.etLoanTerm.getText().toString() + "MAX" + Integer.parseInt(data.getString("termTo")));

                                    product.add(data.getString("productID"));
                                    accountID.add(data.getString("description"));
                                    accountBal.add(data.getString("effectiveRate"));
                                    termFrom.add("Min Term " + data.getString("termFrom"));
                                    term_to.add("Max Term " + data.getString("termTo"));

                                    loanProducts = new Product(data.getString("productID"),
                                            data.getString("description"),
                                            data.getString("effectiveRate"),
                                            data.getString("termFrom"),
                                            data.getString("termTo"));

                                    products.add(loanProducts);
                                    Log.i(TAG, s);
                                }
                            }

                            final Dialog dialog = new Dialog(LoanApplication.this);
                            dialog.setContentView(R.layout.product_list);
                            dialog.setCancelable(false);

                            Button cancelBtn = dialog.findViewById(R.id.dialogBtnCancel);

                            cancelBtn.setOnClickListener(v -> dialog.dismiss());
                            ListView lv_account_d = dialog.findViewById(R.id.d_lv_account);

                            adapter = new Products_Adapter(LoanApplication.this, product, accountID, accountBal, termFrom, term_to);

                            lv_account_d.setAdapter(adapter);

                            lv_account_d.setOnItemClickListener((parent, view, position, id) -> {
                                loanProducts = products.get(position);
                                String rate = loanProducts.getEffectiveRate();
                                binding.etLoanTerm.setText(loanProducts.getTermTo());
                                interest_rate = loanProducts.getEffectiveRate();
                                product_id = loanProducts.getProductID();
                                dialog.dismiss();
                            });
                            dialog.getWindow().getAttributes().windowAnimations = R.anim.dialog_in;
                            dialog.show();
                        } catch (Exception e) {
                            Toast.makeText(LoanApplication.this, Log.getStackTraceString(e) + "", Toast.LENGTH_LONG).show();
                            Log.i(TAG, Log.getStackTraceString(e));
                        }
                    }
                } else {
                    final Dialog dialog = new Dialog(getApplicationContext());
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
                Toast.makeText(LoanApplication.this, Log.getStackTraceString(e) + "", Toast.LENGTH_LONG).show();
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



