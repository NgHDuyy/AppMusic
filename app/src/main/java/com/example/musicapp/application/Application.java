package com.example.musicapp.application;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import com.example.musicapp.data_local.DataLocalManager;

public class Application extends android.app.Application {

    public static final String CHANNEL_MUSIC_ID = "CHANNEL_MUSIC_ID";

    @Override
    public void onCreate() {
        super.onCreate();

        DataLocalManager.init(getApplicationContext());
        createNotificationChannel();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_MUSIC_ID, "Channel app music", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setSound(null, null);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
