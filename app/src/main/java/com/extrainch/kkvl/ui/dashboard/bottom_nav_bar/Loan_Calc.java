package com.extrainch.kkvl.ui.dashboard.bottom_nav_bar;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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
import com.extrainch.kkvl.databinding.FragmentLoanCalcBinding;
import com.extrainch.kkvl.loans.LoanCalculator;
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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Loan_Calc#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Loan_Calc extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private final Loan_Calc context = this;
    RecyclerView.LayoutManager mLayoutManager;
    MyPreferences pref;
    String TAG = LoanCalculator.class.getSimpleName();
    ImageView im_back;
    ProgressDialog dialog;
    ArrayList<String> product, accountID, accountBal, termFrom, term_to;
    ArrayList<Product> products = new ArrayList<Product>();
    Product portfolio;
//    EditText etLoanTerm, etInterestRate, etLoanAmount;
    Products_Adapter adapter;
    FragmentLoanCalcBinding binding;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private List<Calculator> calculatorList = new ArrayList<>();
    private CalculatorAdapter mAdapter;

    public Loan_Calc() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Loan_Calc.
     */
    // TODO: Rename and change types and number of parameters
    public static Loan_Calc newInstance(String param1, String param2) {
        Loan_Calc fragment = new Loan_Calc();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pref = new MyPreferences(getContext());
        mAdapter = new CalculatorAdapter(calculatorList);
        dialog = new ProgressDialog(getActivity());
        mLayoutManager = new LinearLayoutManager(getContext());

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentLoanCalcBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.lblLoans.setVisibility(View.GONE);
        binding.lnViews.setVisibility(View.GONE);
        binding.recyclerView.setLayoutManager(mLayoutManager);
        binding.recyclerView.setItemAnimator(new DefaultItemAnimator());
        binding.recyclerView.setAdapter(mAdapter);

        binding.btnCalculate.setOnClickListener(v -> {
            calculatorList.clear();
            if (TextUtils.isEmpty(binding.etLoanAmount.getText().toString())) {
                binding.etLoanAmount.setError("Loan Amount is required");
            } else if (TextUtils.isEmpty(binding.etProduct.getText().toString())) {
                binding.etProduct.setError("Interest Rate is required");
            } else if (TextUtils.isEmpty(binding.etTerm.getText().toString())) {
                binding.etTerm.setError("Term is required");
            } else {
                //getLoanInstallment();
                calculateLoan(Constants.BASE_URL + "MobileLoan/LoanCalculator");
            }
        });
    }

    public void calculateLoan(String url) {
        RequestQueue queue = Volley.newRequestQueue(getContext());
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("LoanAmount", Double.parseDouble(binding.etLoanAmount.getText().toString()));
            jsonObject.put("InterestRate", Double.parseDouble(binding.etProduct.getText().toString()));
            jsonObject.put("CalculationMethodID", "FN");
            jsonObject.put("PeriodTypeID", "M");
            jsonObject.put("FrequencyID", "M");
            jsonObject.put("LoanTerm", Double.parseDouble(binding.etTerm.getText().toString()));
            jsonObject.put("NoOfInstallments", Double.parseDouble(binding.etTerm.getText().toString()));
            jsonObject.put("Rule78", 0);
            jsonObject.put("TokenCode", pref.getAuthToken());
            jsonObject.put("TokenCode", pref.getAuthToken());
            jsonObject.put("TokenCode", pref.getAuthToken());
            Log.d("VERIFYCODE", "CC" + jsonObject.toString());
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
                    Toast.makeText(getContext(), "Error loading installments", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();

                } else {
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object = array.getJSONObject(i);

                        binding.lblLoans.setVisibility(View.VISIBLE);
                        binding.lblLoans.setVisibility(View.VISIBLE);

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

                    binding.recyclerView.setAdapter(mAdapter);
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
        RequestQueue queue = Volley.newRequestQueue(getContext());
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

                            final Dialog dialog = new Dialog(getContext());
                            dialog.setContentView(R.layout.product_list);
                            dialog.setCancelable(false);
                            Button cancelBtn = dialog.findViewById(R.id.dialogBtnCancel);
                            cancelBtn.setOnClickListener(v -> dialog.dismiss());
                            ListView lv_account_d = dialog.findViewById(R.id.d_lv_account);

                            adapter = new Products_Adapter((Activity) getContext(), product, accountID, accountBal, termFrom, term_to);
                            lv_account_d.setAdapter(adapter);
                            lv_account_d.setOnItemClickListener((parent, view, position, id) -> {
                                portfolio = products.get(position);
                                String rate = portfolio.getEffectiveRate();
                                binding.etProduct.setText(portfolio.getEffectiveRate());
                                binding.etTerm.setText(portfolio.getTermTo());
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
                            Toast.makeText(getContext(), Log.getStackTraceString(e) + "", Toast.LENGTH_LONG).show();
                            Log.i(TAG, Log.getStackTraceString(e));
                        }
                    }
                } else {
                    final Dialog dialog = new Dialog(getContext());
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
                Toast.makeText(getContext(), Log.getStackTraceString(e) + "", Toast.LENGTH_LONG).show();
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