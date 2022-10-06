package com.extrainch.kkvl.loans;


import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.extrainch.kkvl.R;
import com.extrainch.kkvl.mpesa.MpesaWithdrawal;
import com.extrainch.kkvl.ui.dashboard.Dashboard_increado;
import com.extrainch.kkvl.utils.Constants;
import com.extrainch.kkvl.utils.MyPreferences;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONObject;

import java.io.IOException;


public class LoanEligibility extends AppCompatActivity {

    final Context context = this;
    TextView im_continue;
    EditText et_loan_amount, et_loan_term, et_loan_purpose;
    String applicationID;
    float netAmount, netTerm;
    String loanAmount;
    MyPreferences pref;
    ImageView im_back;
    String TAG = LoanEligibility.class.getSimpleName();
    private ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loan_eligibility);

//        FontChangeCrawler fontChanger = new FontChangeCrawler(getAssets(), "fonts/timeless.ttf");
//        fontChanger.replaceFonts((ViewGroup)this.findViewById(android.R.id.content));

        progress = new ProgressDialog(LoanEligibility.this);
        pref = new MyPreferences(LoanEligibility.this);

        // Toast.makeText(getApplicationContext(),pref.getPhoneNumber(),Toast.LENGTH_LONG).show();

        im_back = findViewById(R.id.im_back);
        im_continue = findViewById(R.id.txtContinue);
        et_loan_amount = findViewById(R.id.ed_loan_amount);
        et_loan_term = findViewById(R.id.et_loan_term);
        et_loan_purpose = findViewById(R.id.et_loan_purpose);
        im_continue.setOnClickListener(v -> {
            if (TextUtils.isEmpty(et_loan_amount.getText().toString())) {
                et_loan_amount.setError("Loan Amount is Required");
            } else if (TextUtils.isEmpty(et_loan_term.getText().toString())) {
                et_loan_term.setError("Loan Term is Required");
            } else if (Float.parseFloat(et_loan_amount.getText().toString()) > 200000) {
                et_loan_amount.setError("Loan Amount cannot be greater than 20,000 KSH");
            } else if (Float.parseFloat(et_loan_term.getText().toString()) > 6) {
                et_loan_term.setError("Loan Term cannot be greater than three months");
            } else if (Float.parseFloat(et_loan_term.getText().toString()) < 1) {
                et_loan_term.setError("Loan Term cannot be less than one months");

            } else if (Float.parseFloat(et_loan_amount.getText().toString()) < 1000) {
                et_loan_amount.setError("Loan Amount cannot be lower than 1000KSH");
            } else {
                loanAmount = et_loan_amount.getText().toString();

                pref.setNotification("Click ok to proceed");

                notificationDialog(R.style.DialogAnimation_1, "Left - Right Animation!");

            }
        });
        im_back.setOnClickListener(v -> startActivity(new Intent(this, Dashboard_increado.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)));
    }

    private void notificationDialog(int animationSource, String type) {
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_logout);
        dialog.setCancelable(false);
        Button okbtn = dialog.findViewById(R.id.dialogBtnOK);
        Button calcelBtn = dialog.findViewById(R.id.dialogBtnCancel);
        TextView tv_response_id = dialog.findViewById(R.id.tv_response_id);
        tv_response_id.setText(pref.getNotification());
        // if button is clicked, close the custom dialog

        okbtn.setOnClickListener(v -> {
            dialog.dismiss();

            new apply().execute(Constants.LOAN_URL + "LoanProcessing");

        });
        calcelBtn.setOnClickListener(v -> dialog.dismiss());
        dialog.getWindow().getAttributes().windowAnimations = animationSource;
        dialog.show();
    }

    private void buildclientdetails(int animationSource, String type) {
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_alert);
        dialog.setCancelable(false);
        Button okbtn = (Button) dialog.findViewById(R.id.dialogButtonOK);
        TextView remarks = (TextView) dialog.findViewById(R.id.tv_response_id);
        remarks.setText("" + applicationID);


        // if button is clicked, close the custom dialog
        okbtn.setOnClickListener(v -> {
            dialog.dismiss();
            Intent i = new Intent(LoanEligibility.this, MpesaWithdrawal.class);
            startActivity(i);
        });

        dialog.getWindow().getAttributes().windowAnimations = animationSource;
        dialog.show();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, Dashboard_increado.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
    }

    public class apply extends AsyncTask<String, Void, String> {
        OkHttpClient client = new OkHttpClient();
        boolean connected = false;
        String result = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress.show();
            progress.setMessage("Please wait");
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progress.dismiss();
            if (connected) {
                Toast.makeText(getApplicationContext(), "Error: Could not reach the server ", Toast.LENGTH_LONG).show();
                // Toast.makeText(getApplicationContext(),"User Name is"+userName+password,Toast.LENGTH_LONG).show();
            } else {
                try {
                    JSONObject object = new JSONObject(s);
                    if (object.getString("Status").equals("SUCCESS")) {

                        applicationID = "Your application has been received.Kindly wait as we process your loan.";
                        buildclientdetails(R.style.DialogAnimation_1, "Left - Right Animation!");
                        pref.setLoanAmount(object.getString("NetAmount"));

                        NotificationManager notif = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                        Notification notify = null;
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                            notify = new Notification.Builder
                                    (getApplicationContext()).setContentTitle(applicationID).setContentText(applicationID).
                                    setContentTitle("Loan Chap Chap").build();
                            //  Toast.makeText(getApplicationContext(),applicationID,Toast.LENGTH_LONG).show();
                        }

                        notify.flags |= Notification.FLAG_AUTO_CANCEL;
                        notif.notify(0, notify);

                    } else {

                        final Animation animShake = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake);

                        applicationID = object.getString("Remarks");

                        final Dialog dialog = new Dialog(context);
                        dialog.setContentView(R.layout.dialog_alert);
                        dialog.setCancelable(false);
                        Button okbtn = (Button) dialog.findViewById(R.id.dialogButtonOK);
                        TextView remarks = (TextView) dialog.findViewById(R.id.tv_response_id);
                        remarks.setText("" + applicationID);

                        okbtn.setOnClickListener(v -> dialog.dismiss());

                        dialog.getWindow().getAttributes().windowAnimations = R.anim.dialog_in;
                        dialog.show();
                    }

                    //
                } catch (Exception e) {
                    Log.i(TAG, Log.getStackTraceString(e));
                }
            }
        }

        @Override
        protected String doInBackground(String... params) {
            RequestBody
                    formBody = new FormEncodingBuilder()
                    .add("TokenCode", pref.getAuthToken())
                    .add("ClientID", pref.getClientID())
                    .add("ProductID", "CHAP")
                    .add("LoanAmount", loanAmount)
                    .add("LoanTerm", et_loan_term.getText().toString())
                    .add("InterestRate", "10")
                    .add("LoanPeriodID", "M")
                    .add("PurposeCodeID", "QUERY")
                    .add("CreditOfficerID", "MOBILE")
                    .add("Remarks", et_loan_purpose.getText().toString())
                    .build();
            Request request = new Request.Builder()
                    .url(params[0]).post(formBody)
                    .build();
            try {
                Response response = client.newCall(request).execute();
                if (!response.isSuccessful())
                    throw new IOException("Unexpected code " + response);

                result = response.body().string();

                Log.i(TAG, result);
//                Toast.makeText(getApplicationContext(),result+"",Toast.LENGTH_LONG).show();

            } catch (Exception e) {
                connected = true;
                Log.i(TAG, "" + Log.getStackTraceString(e));
                return null;
            }
            return result;
        }
    }
}



