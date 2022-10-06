package com.extrainch.kkvl.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.extrainch.kkvl.R;
import com.extrainch.kkvl.other.NotificationActivity;
import com.extrainch.kkvl.utils.MyPreferences;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    MyPreferences pref;

    public static String getToken(Context context) {
        return context.getSharedPreferences("_", MODE_PRIVATE).getString("fb", "empty");
    }

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        Log.d("newToken", token);
        getSharedPreferences("_", MODE_PRIVATE).edit().putString("fb", token).apply();

        pref = new MyPreferences(MyFirebaseMessagingService.this);

        pref.setFirebaseId(token);

    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        showNotification(remoteMessage + "");
    }

    private void showNotification(String message) {

        NotificationCompat.BigTextStyle stil = new NotificationCompat.BigTextStyle();
        stil.setBigContentTitle("Başıl");
        stil.bigText(message);
        Intent i = new Intent(this, NotificationActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setAutoCancel(true);
        builder.setSmallIcon(R.mipmap.kkvl);
        builder.setTicker("selam dost.mesaj geldi");
        builder.setDefaults(Notification.DEFAULT_ALL);
        builder.setContentIntent(pendingIntent);
        builder.setContentTitle("Bildirim");
        builder.setContentText(message);
        builder.setStyle(stil);
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(0, builder.build());


    }

}