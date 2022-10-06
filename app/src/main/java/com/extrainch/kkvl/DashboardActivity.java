package com.extrainch.kkvl;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.extrainch.kkvl.adapter.Accounts_Adapter;
import com.extrainch.kkvl.decor.CurvedBottomNavigationView;
import com.extrainch.kkvl.loans.LoanApplication;
import com.extrainch.kkvl.loans.LoanCalculator;
import com.extrainch.kkvl.models.Portfolio;
import com.extrainch.kkvl.mpesa.MpesaWithdrawal;
import com.extrainch.kkvl.other.InsuranceActivity;
import com.extrainch.kkvl.other.NotificationActivity;
import com.extrainch.kkvl.other.PayLoan;
import com.extrainch.kkvl.utils.Constants;
import com.extrainch.kkvl.utils.MyPreferences;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.NetworkInterface;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class DashboardActivity extends AppCompatActivity implements
        BottomNavigationView.OnNavigationItemSelectedListener, NavigationView.OnNavigationItemSelectedListener {


    private static final int REQUEST_GALLERY = 0;
    private static final int REQUEST_CAMERA = 1;
    private static final int MY_PERMISSIONS_REQUESTS = 0;
    public static int count = 0;
    final Context context = this;
    String TAG = DashboardActivity.class.getSimpleName();
    String encoded;
    String selectedAccountID;
    String regid, url;
    ListView lv_account;
    ImageView im_close_drawer, im_drawer, im_bill_payment, im_mobile_money, im_send_money, im_withdrawal, im_migration, im_settings;
    Portfolio portfolio;
    Typeface typeface;
    ArrayList<Portfolio> portfolios = new ArrayList<Portfolio>();
    Accounts_Adapter adapter;
    DrawerLayout drawer;
    ImageView photoID;
    CircleImageView ic_profile;
    ArrayList<String> product, accountID, accountBal;
    LinearLayout lnMpesa, lnLoanStatement, lnBalance, lnLoanApplication, lnChangePIN, lnPayLoan, lnContact;
    //LinearLayout lnSendMoney, lnBranches, lnLogout, lnAboutApp, lnLoanAccountBalances, lnNewsActivity, lnContactUs, ln_calculator, ln_Ministatement, lnPromotions, lnChapChap;
    TextView txtAboutUs, txtBranches, txtContactUs, txtStatement, txtCalculator, txtPromotions, txtChapChap, txtNews, txtAccounts, txtLogout;
    TextView txtName, txtSalut, txtCompany;
    int phototype;
    ImageView imLogout, imNotifications;
    TextView txtAccountID, txtLoanArrears, txtLoanLimit;
    String unique_id;
    private Uri imageUri;
    private MyPreferences pref;

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
        }

        return "02:00:00:00:00:00";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alt_dashboard);
        CurvedBottomNavigationView mView = findViewById(R.id.customBottomBar);
        mView.inflateMenu(R.menu.main_menu);
        pref = new MyPreferences(this);

        mView.setSelectedItemId(R.id.action_schedules);
        //getting bottom navigation view and attaching the listener
        mView.setOnNavigationItemSelectedListener(DashboardActivity.this);

        im_drawer = (ImageView) findViewById(R.id.im_drawer);


        unique_id = android.provider.Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);


        lnChangePIN = findViewById(R.id.ln_settings);
        txtAccountID = findViewById(R.id.txt_account_id);
        txtName = findViewById(R.id.txt_name);

        txtName.setText(pref.getAccountName());
        txtAccountID.setText(pref.getMbdAccountid());


        lnPayLoan = findViewById(R.id.ln_pay_loan);
        txtLoanLimit = findViewById(R.id.txt_loan_limit);
        txtLoanArrears = findViewById(R.id.txt_loan_arrears);
        imNotifications = findViewById(R.id.im_notifications);
        imLogout = findViewById(R.id.im_logout);

        lnLoanApplication = findViewById(R.id.ln_loan_application);
        lnBalance = findViewById(R.id.ln_balance_enquiry);
        lnLoanStatement = findViewById(R.id.ln_loan_statement);
        lnContact = findViewById(R.id.ln_contact_us);

        supremeLoanEligibility(Constants.BASE_URL + "MobileLoan/FetchCustomerLimit");


        SharedPreferences sharedPrefs = DashboardActivity.this.getSharedPreferences("RATER", 0);
        if (sharedPrefs.getBoolean("NO THANKS", false)) {
            return;
        } else {
            SharedPreferences.Editor prefsEditor = sharedPrefs.edit();
            //YOUR CODE TO SHOW DIALOG
            long time = sharedPrefs.getLong("displayedTime", 0);
            if (time < System.currentTimeMillis() - 259200000) {
                displayDialog();
                prefsEditor.putLong("displayedTime", System.currentTimeMillis()).commit();
            }
            prefsEditor.apply();
        }


        //  final FoldingCell fc = (FoldingCell) findViewById(R.id.folding_cell);

        // attach click listener to folding cell

        lnLoanApplication.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(DashboardActivity.this, LoanApplication.class);
                startActivity(i);

            }
        });


        lnBalance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(DashboardActivity.this, AccountBalances.class);
                startActivity(i);

            }
        });

        lnLoanStatement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                fetchClientPortFolio(Constants.BASE_URL + "MobileClient/FetchClientPortfolio");
            }
        });

        imNotifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(DashboardActivity.this, NotificationActivity.class);
                startActivity(i);

            }
        });

        imLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.dialog_logout);
                dialog.setCancelable(false);
                Button okbtn = (Button) dialog.findViewById(R.id.dialogBtnOK);
                Button cancelBtn = (Button) dialog.findViewById(R.id.dialogBtnCancel);

                TextView txtBody = dialog.findViewById(R.id.tv_response_id);
                txtBody.setText("Dear " + pref.getAccountName() + " are you sure you want to logout?");
                okbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent i = new Intent(DashboardActivity.this, LoginActivity.class);
                        startActivity(i);

                    }
                });
                cancelBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        dialog.dismiss();
                    }
                });
                dialog.getWindow().getAttributes().windowAnimations = R.anim.dialog_in;
                dialog.show();

            }
        });

        lnContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(DashboardActivity.this, MpesaWithdrawal.class);
                startActivity(i);

            }
        });

        lnPayLoan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(DashboardActivity.this, PayLoan.class);
                startActivity(i);

            }
        });

        lnChangePIN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(DashboardActivity.this, ChangePinActivity.class);
                startActivity(i);

            }
        });


    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_favorites:
                Intent i = new Intent(DashboardActivity.this, LoanCalculator.class);
                startActivity(i);
                break;
            case R.id.action_schedules:
                break;
            case R.id.action_music:
                Intent b = new Intent(DashboardActivity.this, InsuranceActivity.class);
                startActivity(b);
                break;
        }
        return true;
    }

    public void fetchClientPortFolio(String url) {
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("OurBranchID", "00");
            jsonObject.put("ClientID", pref.getClientID());
            jsonObject.put("TokenCode", pref.getAuthToken());

            Log.d("VERIFYCODE", "CC" + jsonObject.toString());
        } catch (final JSONException e) {
            e.printStackTrace();

        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(com.android.volley.Request.Method.POST, url, jsonObject, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {


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
                        okbtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                dialog.dismiss();

                                Intent i = new Intent(DashboardActivity.this, LoginActivity.class);
                                startActivity(i);

                            }
                        });

                        dialog.getWindow().getAttributes().windowAnimations = R.anim.dialog_in;
                        dialog.show();
                    } else {
                        String s = object.getString("data");

                        if (object.getString("code").equals("200")) {
                            {
                                try {
                                    JSONArray array = new JSONArray(s);
                                    Log.i(TAG, "Response " + s);
                                    if (array.length() == 0) {
                                        Toast.makeText(DashboardActivity.this, "You do not have existing Loans", Toast.LENGTH_SHORT).show();
                                    } else {

                                        product = new ArrayList<String>();
                                        accountID = new ArrayList<String>();
                                        accountBal = new ArrayList<String>();

                                        for (int i = 0; i < array.length(); i++) {
                                            JSONObject data = array.getJSONObject(i);

                                            if (data.getString("productTypeID").equals("Loans")) {

                                                product.add(data.getString("productID"));
                                                accountID.add(data.getString("accountID"));
                                                accountBal.add(data.getString("balance"));

                                                portfolio = new Portfolio(pref.getClientID(), data.getString("name"),
                                                        data.getString("productID"),
                                                        data.getString("accountID"),
                                                        data.getString("productTypeID"),
                                                        data.getString("balance"));

                                                portfolios.add(portfolio);
                                                Log.i(TAG, s);
                                            }

                                        }
                                    }


                                    final Dialog dialog = new Dialog(context);
                                    dialog.setContentView(R.layout.dialog_list);
                                    dialog.setCancelable(false);

                                    Button cancelBtn = dialog.findViewById(R.id.dialogBtnCancel);


                                    cancelBtn.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                            dialog.dismiss();

                                        }
                                    });
                                    ListView lv_account_d = dialog.findViewById(R.id.d_lv_account);


                                    adapter = new Accounts_Adapter(DashboardActivity.this, product, accountID, accountBal);

//                    Toast.makeText(MainTransaction.this, adapter+"",
//                            Toast.LENGTH_LONG).show();
                                    lv_account_d.setAdapter(adapter);

                                    lv_account_d.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
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
                                            Intent i = new Intent(DashboardActivity.this, Ministatement.class);
                                            startActivity(i);


                                        }
                                    });


                                    dialog.getWindow().getAttributes().windowAnimations = R.anim.dialog_in;
                                    dialog.show();
                                } catch (Exception e) {

                                    Toast.makeText(DashboardActivity.this, Log.getStackTraceString(e) + "",
                                            Toast.LENGTH_LONG).show();
                                    Log.i(TAG, Log.getStackTraceString(e));
                                }
                            }
                        } else {

                            final Dialog dialog = new Dialog(context);
                            dialog.setContentView(R.layout.dialog_alert);
                            dialog.setCancelable(false);
                            Button okbtn = (Button) dialog.findViewById(R.id.dialogButtonOK);

                            TextView txtBody = dialog.findViewById(R.id.tv_response_id);
                            txtBody.setText("" + object.getString("msg"));
                            okbtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    dialog.dismiss();

                                }
                            });

                            dialog.getWindow().getAttributes().windowAnimations = R.anim.dialog_in;
                            dialog.show();


                        }
                    }
                } catch (Exception e) {

                    Toast.makeText(DashboardActivity.this, Log.getStackTraceString(e) + "",
                            Toast.LENGTH_LONG).show();
                    Log.i(TAG, Log.getStackTraceString(e));
                }
            }


        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error", error.toString());


            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json");
                return params;
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };
        jsonObjectRequest.setRetryPolicy(new

                DefaultRetryPolicy(
                60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(jsonObjectRequest);
    }

    public void supremeLoanEligibility(String url) {
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        JSONObject jsonObject = new JSONObject();
        try {

            jsonObject.put("ClientID", pref.getClientID());
            jsonObject.put("TokenCode", pref.getAuthToken());

            Log.d("Loan Application", jsonObject.toString());

        } catch (final JSONException e) {
            e.printStackTrace();

        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(com.android.volley.Request.Method.POST, url, jsonObject, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

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


                                txtLoanLimit.setText("KES " + yourFormattedString);
                                txtLoanArrears.setText("KES " + loanarrears);

                            }
                        }


                    }

                } catch (Exception e) {

                    Toast.makeText(DashboardActivity.this, Log.getStackTraceString(e) + "",
                            Toast.LENGTH_LONG).show();
                    Log.i(TAG, Log.getStackTraceString(e));
                }
            }


        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error", error.toString());

                //     progress.dismiss();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json");
                return params;
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };
        jsonObjectRequest.setRetryPolicy(new

                DefaultRetryPolicy(
                60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(jsonObjectRequest);
    }

    private void displayDialog() {
        // TODO Auto-generated method stub
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        //Yes button clicked
                        Intent in = new Intent(android.content.Intent.ACTION_VIEW);
                        in.setData(Uri.parse(url));
                        startActivity(in);
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        //Saving a boolean on no thanks button click

                        SharedPreferences prefs = DashboardActivity.this.getSharedPreferences("RATER", 0);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putBoolean("NO THANKS", true);
                        editor.apply();
                        break;
                }
            }
        };

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                buildReminderDialog(R.style.DialogAnimation_1, "Left - Right Animation!");
            }
        }, 1000);   //5 seconds

    }

    private void buildReminderDialog(int animationSource, String type) {
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_alert);
        dialog.setCancelable(false);
        Button okbtn = (Button) dialog.findViewById(R.id.dialogButtonOK);
        TextView txtReminder = (TextView) dialog.findViewById(R.id.tv_response_id);
        txtReminder.setText("Welcome to Karibu Kash");

        Button cancelBtn = (Button) dialog.findViewById(R.id.dialogBtnCancel);
        // if button is clicked, close the custom dialog
        ;
        okbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();

            }
        });

        dialog.getWindow().getAttributes().windowAnimations = animationSource;
        dialog.show();
    }

    public void supremeClientDetails(String url) {
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("NationalID", pref.getNationalID());
            jsonObject.put("ClientID", pref.getClientID());
            jsonObject.put("PhoneNumber", pref.getPhoneNumber());
            jsonObject.put("DeviceID", unique_id);
            jsonObject.put("DeviceName", Build.MANUFACTURER);
            jsonObject.put("Imei", getDeviceIMEI());
            jsonObject.put("MacAddress", getMacAddr());

            Log.d("VERIFYCODE", "CC" + jsonObject.toString());
        } catch (final JSONException e) {
            e.printStackTrace();

        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(com.android.volley.Request.Method.POST, url, jsonObject, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                Log.d("LLL", response.toString());
                // progress.dismiss();
                try {
                    Log.d("LLL", response.toString());

                    JSONObject object = new JSONObject(response.toString());

                    if (object.getString("code").equals("200")) {
                        {

                            JSONObject data = new JSONObject(object.getString("data"));


                            pref.setClientID(data.getString("clientID"));
                            pref.setODSAccID(data.getString("mbdAccountID"));
                            pref.setPhoneNumber(data.getString("phoneNumber"));
                            pref.setLoanAccountID(data.getString("loanAccountID"));
                            pref.setAccountName(data.getString("name"));
                            pref.setAccountReminder(data.getString("reminder"));
                            pref.setMBDAccountID(data.getString("accountID"));
                            pref.setAccID(data.getString("accountID"));
                            //   pref.setPhotoID(object.getString("Photo"));
                            pref.setPhotoID("");

                            // new updateLoginstatus().execute(Constants.LOGIN_URL + "UpdateLoginStatus");
                        }
                    } else {

                    }

                } catch (Exception e) {

                    Toast.makeText(DashboardActivity.this, Log.getStackTraceString(e) + "",
                            Toast.LENGTH_LONG).show();
                    Log.i(TAG, Log.getStackTraceString(e));
                }
            }


        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error", error.toString());
                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.dialog_alert);
                dialog.setCancelable(false);
                Button okbtn = (Button) dialog.findViewById(R.id.dialogButtonOK);
                TextView remarks = (TextView) dialog.findViewById(R.id.tv_response_id);
                remarks.setText("Your internet is off. Kindly check your connection and try again");


                // if button is clicked, close the custom dialog
                okbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();

                    }
                });

                dialog.show();

                //     progress.dismiss();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json");
                return params;
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };
        jsonObjectRequest.setRetryPolicy(new

                DefaultRetryPolicy(
                60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(jsonObjectRequest);
    }

    public String getDeviceIMEI() {
        String deviceUniqueIdentifier = null;
        TelephonyManager tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        if (null != tm) {
            deviceUniqueIdentifier = tm.getDeviceId();
        }
        if (null == deviceUniqueIdentifier || 0 == deviceUniqueIdentifier.length()) {
            deviceUniqueIdentifier = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        }
        return deviceUniqueIdentifier;
    }
}