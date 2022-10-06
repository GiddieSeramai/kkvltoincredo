package com.extrainch.kkvl.ui.dashboard.bottom_nav_bar;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
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
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.extrainch.kkvl.AccountBalances;
import com.extrainch.kkvl.ChangePinActivity;
import com.extrainch.kkvl.LoginActivity;
import com.extrainch.kkvl.Ministatement;
import com.extrainch.kkvl.R;
import com.extrainch.kkvl.adapter.Accounts_Adapter;
import com.extrainch.kkvl.databinding.FragmentHome2Binding;
import com.extrainch.kkvl.loans.LoanApplication;
import com.extrainch.kkvl.models.Portfolio;
import com.extrainch.kkvl.mpesa.MpesaWithdrawal;
import com.extrainch.kkvl.other.InsuranceActivity;
import com.extrainch.kkvl.other.PayLoan;
import com.extrainch.kkvl.utils.Constants;
import com.extrainch.kkvl.utils.MyPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HomeFragment extends Fragment {
    private final int GALLERY_REQ_CODE = 1000;
    private final int CAMERA_REQ_CODE = 1001;
    FragmentHome2Binding binding;
    ArrayList<String> product, accountID, accountBal;
    ArrayList<Portfolio> portfolios = new ArrayList<Portfolio>();
    Portfolio portfolio;
    Accounts_Adapter adapter;
    String selectedAccountID;
    private MyPreferences pref;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pref = new MyPreferences(getContext());
        supremeLoanEligibility(Constants.BASE_URL + "MobileLoan/FetchCustomerLimit");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHome2Binding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Get Loan Status
        binding.myAccount.setOnClickListener(v -> startActivity(new Intent(getContext(), AccountBalances.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)));

        binding.loanStatement.setOnClickListener(v -> fetchClientPortFolio(Constants.BASE_URL + "MobileClient/FetchClientPortfolio"));

        binding.borrowLoan.setOnClickListener(v -> startActivity(new Intent(getContext(), LoanApplication.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)));
        binding.payLoan.setOnClickListener(v -> startActivity(new Intent(getContext(), PayLoan.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)));
        binding.withdrawCash.setOnClickListener(v -> startActivity(new Intent(getContext(), MpesaWithdrawal.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)));
        binding.settings.setOnClickListener(v -> startActivity(new Intent(getContext(), ChangePinActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)));
        binding.insuranceFinancing.setOnClickListener(v -> startActivity(new Intent(getContext(), InsuranceActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)));

        binding.cardViewDP.setOnClickListener(v -> pickImage());

        //Set Text on the TextViews
        binding.customerName.setText(pref.getAccountName());
        binding.accountNumber.setText(pref.getMbdAccountid());

        if (pref.getProfileBase() != null) {
            byte[] bytes = Base64.decode(pref.getProfileBase(), Base64.DEFAULT);
            // Initialize bitmap
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            // set bitmap on imageView
            binding.profileImage.setImageBitmap(bitmap);
        }
    }

    //checks for storage permission
    private boolean checkAndRequestPermissions() {
        int cameraPermissions = ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA);
        if (cameraPermissions == PackageManager.PERMISSION_DENIED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, 20);
            return false;
        }
        return true;
    }

    private void pickImage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.profile_alert_dialog, null);
        builder.setTitle("Choose Upload Option");
        builder.setCancelable(false);
        builder.setView(dialogView);

        ImageView cameraIcon = dialogView.findViewById(R.id.cameraIcon);
        ImageView galleryIcon = dialogView.findViewById(R.id.galleryIcon);
        TextView cancel = dialogView.findViewById(R.id.cancel);

        AlertDialog alertDialogProfilePicture = builder.create();
        alertDialogProfilePicture.show();

        cancel.setOnClickListener(view -> alertDialogProfilePicture.dismiss());

        cameraIcon.setOnClickListener(view -> {
            if (checkAndRequestPermissions()) {
                Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePicture.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivityForResult(takePicture, 1001);
                    alertDialogProfilePicture.cancel();
                }
            }
        });

        galleryIcon.setOnClickListener(view -> {
            if (checkAndRequestPermissions()) {
                Intent pickPhoto = new Intent(Intent.ACTION_PICK);
                pickPhoto.setType("image/*");
//                pickPhoto.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto, 1000);
                alertDialogProfilePicture.cancel();
            }
        });
    }

    private void supremeLoanEligibility(String url) {
        RequestQueue queue = Volley.newRequestQueue(getContext());
        JSONObject jsonObject = new JSONObject();
        try {

            jsonObject.put("ClientID", pref.getClientID());
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
                    {
                        if (object.getString("msg").equals("Success")) {
                            JSONObject data = new JSONObject(object.getString("data"));
                            String obj = data.getString("eligibleAmount");
                            String loan = data.getString("arrearAmount");

                            DecimalFormat formatter = new DecimalFormat("#,###,###");
                            String yourFormattedString = formatter.format(Integer.parseInt(obj));
                            String loanarrears = formatter.format(Integer.parseInt(loan));

                            binding.loanLimit.setText("KES " + yourFormattedString);
                            binding.loanArrears.setText("KES " + loanarrears);
                        }
                    }
                }
            } catch (Exception e) {
                Toast.makeText(getContext(), Log.getStackTraceString(e) + "", Toast.LENGTH_LONG).show();
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

    private void fetchClientPortFolio(String url) {
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false); // set cancelable to false
        progressDialog.setMessage("Fetching Statements..."); // set message
        progressDialog.show();

        RequestQueue queue = Volley.newRequestQueue(getContext());
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("OurBranchID", pref.getOurBranchID());
            jsonObject.put("ClientID", pref.getClientID());
            jsonObject.put("TokenCode", pref.getAuthToken());

            Log.d("VERIFYCODE", "CC" + jsonObject);
        } catch (final JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(com.android.volley.Request.Method.POST, url, jsonObject, response -> {
            try {
                Log.d("LLL", response.toString());

                JSONObject object = new JSONObject(response.toString());
                if (object.getString("code").equals("300")) {
                    progressDialog.dismiss();
                    final Dialog dialog = new Dialog(getContext());
                    dialog.setContentView(R.layout.dialog_alert);
                    dialog.setCancelable(false);
                    Button okbtn = dialog.findViewById(R.id.dialogButtonOK);

                    TextView txtBody = dialog.findViewById(R.id.tv_response_id);
                    txtBody.setText("Your session has expired. Kindly relogin to continue");
                    okbtn.setOnClickListener(v -> {
                        dialog.dismiss();
                        Intent i = new Intent(getContext(), LoginActivity.class);
                        startActivity(i);
                    });
                    dialog.getWindow().getAttributes().windowAnimations = R.anim.dialog_in;
                    dialog.show();
                } else {
                    String s = object.getString("data");
                    if (object.getString("code").equals("200")) {
                        progressDialog.dismiss();

                        final Dialog dialog = new Dialog(getContext());
                        dialog.setContentView(R.layout.dialog_list);
                        dialog.setCancelable(false);

                        Button cancelBtn = dialog.findViewById(R.id.dialogBtnCancel);
                        TextView noLoans = dialog.findViewById(R.id.noLoans);
                        ListView lv_account_d = dialog.findViewById(R.id.d_lv_account);
                        try {
                            JSONArray array = new JSONArray(s);
                            Log.i(TAG, "ResponsLoans " + s);
                            if (array.length() == 0) {
                                noLoans.setVisibility(View.VISIBLE);
                                lv_account_d.setVisibility(View.GONE);
                            } else {
                                noLoans.setVisibility(View.GONE);
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

                                    portfolios.add(portfolio);
                                    Log.i(TAG, s);
                                }

                                adapter = new Accounts_Adapter((Activity) getContext(), product, accountID, accountBal);

                                lv_account_d.setAdapter(adapter);

                                lv_account_d.setOnItemClickListener((parent, view, position, id) -> {
                                    portfolio = portfolios.get(position);
                                    selectedAccountID = portfolio.getAccountID() + "";
                                    if (selectedAccountID.isEmpty()) {
                                    } else
                                        pref.setAccountTypeId(portfolio.getAccountTypeID());
                                    String name = portfolio.getName();
                                    String balance = portfolio.Balance;
                                    pref.setAccID(selectedAccountID);
                                    pref.setAccountDetails(selectedAccountID, name, balance);
                                    dialog.dismiss();
                                    Intent inte = new Intent(getContext(), Ministatement.class);
                                    startActivity(inte);
                                });
                            }
                            cancelBtn.setOnClickListener(v -> dialog.dismiss());

                            dialog.getWindow().getAttributes().windowAnimations = R.anim.dialog_in;
                            dialog.show();
                        } catch (Exception e) {
//                            Toast.makeText(AccountBalances.this, Log.getStackTraceString(e) + "", Toast.LENGTH_LONG).show();
                            Log.i(TAG, Log.getStackTraceString(e));
                        }
                    } else {
                        progressDialog.dismiss();
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == GALLERY_REQ_CODE) {
                if (data != null) {
                    binding.profileImage.setImageURI(data.getData());
                    Uri uri = data.getData();
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                        // initialize byte stream
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        // compress Bitmap
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                        // Initialize byte array
                        byte[] bytes = stream.toByteArray();
                        // get base64 encoded string
                        String sImage = Base64.encodeToString(bytes, Base64.DEFAULT);

                        pref.setProfileBase(sImage);
                        // set encoded text on textview
                        Log.d(TAG, "Image: " + pref.getProfileBase());

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    // image path declaration
                    Log.d(TAG, "Image: " + uri);
                }
            } else if (requestCode == CAMERA_REQ_CODE) {
                if (data != null) {
                    Bitmap img = (Bitmap) (data.getExtras().get("data"));
                    binding.profileImage.setImageBitmap(img);
                }
            }
        }
    }
}