package com.extrainch.kkvl.other;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.extrainch.kkvl.LoginActivity;
import com.extrainch.kkvl.R;
import com.extrainch.kkvl.adapter.NotificationAdapter;
import com.extrainch.kkvl.databinding.ActivityNotifictionBinding;
import com.extrainch.kkvl.models.Notification;
import com.extrainch.kkvl.ui.dashboard.Dashboard_increado;
import com.extrainch.kkvl.utils.Constants;
import com.extrainch.kkvl.utils.MyPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NotificationActivity extends AppCompatActivity {

    final Context context = this;
    ActivityNotifictionBinding binding;
    MyPreferences pref;
    String TAG = NotificationActivity.class.getSimpleName();
    String unique_id;
    Notification notification;
    ArrayList<Notification> notifications = new ArrayList<>();
    ArrayList<String> notificationID, description, status, title;
    NotificationAdapter adapter;
    ListView lv_notification;
    LinearLayout noDetails;
    ImageView im_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNotifictionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        lv_notification = (ListView) findViewById(R.id.lv_notification);

        pref = new MyPreferences(NotificationActivity.this);

        noDetails = (LinearLayout) findViewById(R.id.no_details);


        unique_id = android.provider.Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);

        fetchNotifications(Constants.BASE_URL + "MobileClient/ClientNotification");
        //https://197.232.33.44:56759/SupremeApp/api/MobileClient/ClientNotification
        //{"ClientID":"0000266","DeviceID":""}

        binding.backPressed.setOnClickListener(v ->
                startActivity(new Intent(this, Dashboard_increado.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK)));
    }

    public void fetchNotifications(String url) {
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("DeviceID", unique_id);
            jsonObject.put("ClientID", pref.getClientID());
            jsonObject.put("TokenCode", pref.getAuthToken());

            Log.d(TAG, "Notifications" + jsonObject.toString());
        } catch (final JSONException e) {
            e.printStackTrace();

        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(com.android.volley.Request.Method.POST, url, jsonObject, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {


                try {
                    Log.d("LLL", response.toString());

                    JSONObject object = new JSONObject(response.toString());
                    String s = object.getString("data");


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

                                Intent i = new Intent(NotificationActivity.this, LoginActivity.class);
                                startActivity(i);

                            }
                        });

                        dialog.getWindow().getAttributes().windowAnimations = R.anim.dialog_in;
                        dialog.show();
                    } else if (object.getString("code").equals("200")) {
                        {

                            try {
                                JSONArray array = new JSONArray(s);
                                Log.i(TAG, "Response " + s);


                                notificationID = new ArrayList<String>();
                                description = new ArrayList<String>();
                                status = new ArrayList<String>();
                                title = new ArrayList<String>();


                                if (array.length() == 0) {
                                    noDetails.setVisibility(View.VISIBLE);
                                    lv_notification.setVisibility(View.GONE);
                                } else

                                    for (int i = 0; i < array.length(); i++) {
                                        JSONObject data = array.getJSONObject(i);


                                        notificationID.add(data.getString("notificationID"));
                                        description.add(data.getString("message"));
                                        status.add(data.getString("status"));
                                        title.add(data.getString("notificationType"));

                                        notification = new Notification(
                                                data.getString("notificationID"),
                                                data.getString("message"), data.getString("status"),
                                                data.getString("notificationType"));


//                        userName.setText(object.getString("Name"));
                                        notifications.add(notification);
                                        Log.i(TAG, s);
                                    }


                                adapter = new NotificationAdapter(NotificationActivity.this, notificationID, description, status, title);


                                lv_notification.setAdapter(adapter);


                            } catch (Exception e) {

                                Log.i(TAG, Log.getStackTraceString(e));
                            }
                        }
                    }
                } catch (Exception e) {

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

}