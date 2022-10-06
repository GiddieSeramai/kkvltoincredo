package com.extrainch.kkvl.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.EditText;

import com.extrainch.kkvl.utils.MyPreferences;

public class ReceiveSMS extends BroadcastReceiver {
    private static final String SMS_SENDER = "Karibu_MFI";
    private static EditText editText;
    MyPreferences pref;

    public void setEditText(EditText editText) {
        ReceiveSMS.editText = editText;
    }

    // OnReceive will keep trace when sms is been received in mobile
    @Override
    public void onReceive(Context context, Intent intent) {
        //message will be holding complete sms that is received
        SmsMessage[] messages = new SmsMessage[0];
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            messages = Telephony.Sms.Intents.getMessagesFromIntent(intent);
        }
        for (SmsMessage sms : messages) {
            String sms_from = messages[0].getDisplayOriginatingAddress();

            // if (sms_from.equalsIgnoreCase(SMS_SENDER)) {
            StringBuilder bodyText = new StringBuilder();


            String msg = sms.getMessageBody();
            // here we are spliting the sms using " : " symbol
            // String otp = msg.split(": ")[1];
            String first4char = msg.substring(0, 4);
            Log.d("HERE", sms.getMessageBody());
            pref = new MyPreferences(context);
            pref.setOtp(first4char);

//                editText.setText(first4char);
            //    }
        }
    }

}