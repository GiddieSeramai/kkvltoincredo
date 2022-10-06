package com.extrainch.kkvl.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.extrainch.kkvl.R;

import java.util.ArrayList;

public class NotificationAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final ArrayList<String> notification_id;
    private final ArrayList<String> description;
    private final ArrayList<String> status;
    private final ArrayList<String> title;

    public NotificationAdapter(Activity context, ArrayList<String> notification_id, ArrayList<String> description, ArrayList<String> status, ArrayList<String> title) {
        super(context, R.layout.notification, notification_id);
        // TODO Auto-generated constructor stub
        this.context = context;
        this.notification_id = notification_id;
        this.description = description;
        this.status = status;
        this.title = title;

    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.notification, null, true);
        TextView message = rowView.findViewById(R.id.message);
        TextView txttitle = rowView.findViewById(R.id.title);
        ImageView imStatus = rowView.findViewById(R.id.im_status);

        if (status.get(position).equals("Unread")) {
            imStatus.setVisibility(View.VISIBLE);
        }
        message.setText(description.get(position));
        txttitle.setText(title.get(position));

        return rowView;
    }
}
