package com.example.musicapp.receive;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.musicapp.service.Service;

public class DataReceive extends BroadcastReceiver {
    public static final String ACTION = "ACTION";
    public static final String POSITION = "POSITION";
    public static final String ACTION_RECEIVE = "ACTION_RECEIVE";
    public static final String POSITION_RECEIVE = "POSITION_RECEIVE";

    @Override
    public void onReceive(Context context, Intent intent) {
        int action = intent.getIntExtra(ACTION, 0);
        int position = intent.getIntExtra(POSITION,0);
        Intent intentReceive = new Intent(context, Service.class);
        intentReceive.putExtra(ACTION_RECEIVE, action);
        intentReceive.putExtra(POSITION_RECEIVE, position);
        context.startService(intentReceive);
    }
}
