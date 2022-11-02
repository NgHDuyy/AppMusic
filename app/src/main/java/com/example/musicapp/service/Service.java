package com.example.musicapp.service;

import static com.example.musicapp.application.Application.CHANNEL_MUSIC_ID;
import static com.example.musicapp.receive.DataReceive.ACTION;
import static com.example.musicapp.receive.DataReceive.ACTION_RECEIVE;
import static com.example.musicapp.receive.DataReceive.POSITION;
import static com.example.musicapp.receive.DataReceive.POSITION_RECEIVE;
import static com.example.musicapp.ui.home.tab_home.TabFavoriteFragment.mListSongFavorite;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.musicapp.R;
import com.example.musicapp.data_local.DataLocalManager;
import com.example.musicapp.receive.DataReceive;
import com.example.musicapp.ui.home.tab_home.song.Song;

import java.util.List;

public class Service extends android.app.Service {

    public static final String SEND_DATA_TO_ACTIVITY = "SEND_DATA_TO_ACTIVITY";
    public static final String SEND_POSITION = "SEND_POSITION";
    public static final String SEND_STATUS_PLAYING = "SEND_STATUS_PLAYING";
    public static final String SEND_ACTION = "SEND_ACTION";
    public static final String SEND_LIST = "SEND_LIST";

    public static final int LIST_HOME = 1;
    public static final int LIST_FAVORITE = 2;


    public static final int ACTION_START = 1;
    public static final int ACTION_PAUSE = 2;
    public static final int ACTION_RESUME = 3;
    public static final int ACTION_PREVIOUS = 4;
    public static final int ACTION_NEXT = 5;
    public static final int ACTION_CLEAR = 6;

    public static MediaPlayer mediaPlayer;
    private List<Song> mListSong;
    private Song song;
    private int mPosition;
    private int list;
    private int checked = -1;

    private boolean isPlaying;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        list = intent.getIntExtra(SEND_LIST, LIST_HOME);
        int action;
        if (list == LIST_HOME){
            mListSong = DataLocalManager.getListSong();
            action = intent.getIntExtra(ACTION_RECEIVE, 0);
            mPosition = intent.getIntExtra(POSITION_RECEIVE, 0);
            song = mListSong.get(mPosition);
            startMusic(song);
            handleActionMusic(action, mPosition);
        } else if (list == LIST_FAVORITE){
            mListSong = mListSongFavorite;
            action = intent.getIntExtra(ACTION_RECEIVE, 0);
            mPosition = intent.getIntExtra(POSITION_RECEIVE, 0);
            song = mListSong.get(mPosition);
            startMusic(song);
            handleActionMusic(action, mPosition);
        }
        return START_NOT_STICKY;
    }


    private void handleActionMusic(int action, int handlePosition) {
        switch (action) {
            case ACTION_PAUSE:
                pauseMusic(handlePosition);
                break;
            case ACTION_RESUME:
                resumeMusic(handlePosition);
                break;
            case ACTION_PREVIOUS:
                previousMusic(handlePosition);
                break;
            case ACTION_NEXT:
                nextMusic(handlePosition);
                break;
            case ACTION_CLEAR:
                stopSelf();
                sendActionToActivity(ACTION_CLEAR, mPosition);
                break;
        }
    }

    private void startMusic(Song song) {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(getApplicationContext(), song.getResource());
        }
        if (checked != mPosition) {
            mediaPlayer.release();
            mediaPlayer = MediaPlayer.create(getApplicationContext(), song.getResource());
        }
        checked = mPosition;
        mediaPlayer.start();
        isPlaying = true;
        sendActionToActivity(ACTION_START, mPosition);
        sendNotificationMedia(song, mPosition, 1f);
    }

    private void pauseMusic(int pausePosition) {
        if (mediaPlayer != null && isPlaying) {
            mediaPlayer.pause();
            isPlaying = false;
            sendNotificationMedia(song, pausePosition, 0f);
            sendActionToActivity(ACTION_PAUSE, pausePosition);
        }
    }

    private void resumeMusic(int resumePosition) {
        if (mediaPlayer != null && !isPlaying) {
            mediaPlayer.start();
            isPlaying = true;
            sendNotificationMedia(song, resumePosition, 1f);
            sendActionToActivity(ACTION_RESUME, resumePosition);
        }
    }

    private void previousMusic(int previousPosition) {
        sendActionToActivity(ACTION_PREVIOUS, previousPosition);
        sendNotificationMedia(song, previousPosition, 1f);
    }

    private void nextMusic(int nextPosition) {
        sendActionToActivity(ACTION_NEXT, nextPosition);
        sendNotificationMedia(song, nextPosition, 1F);
    }

    private void sendActionToActivity(int action, int sendPosition) {
        Intent intent = new Intent(SEND_DATA_TO_ACTIVITY);
        Bundle bundle = new Bundle();
        bundle.putBoolean(SEND_STATUS_PLAYING, isPlaying);
        bundle.putInt(SEND_LIST, list);
        bundle.putInt(SEND_POSITION, sendPosition);
        bundle.putInt(SEND_ACTION, action);
        intent.putExtras(bundle);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void sendNotificationMedia(Song song, int sPosition, float playbackSpeed) {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), song.getImageSong());

        MediaSessionCompat mediaSessionCompat = new MediaSessionCompat(this, "tag");

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_MUSIC_ID)
                .setSmallIcon(R.drawable.ic_music)
                .setContentTitle(song.getSongName())
                .setContentText(song.getSingerName())
                .setLargeIcon(bitmap)
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setShowActionsInCompactView(0, 1, 2)
                        .setMediaSession(mediaSessionCompat.getSessionToken()));
        mediaSessionCompat.setMetadata(new MediaMetadataCompat.Builder()
                .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, mediaPlayer.getDuration())
                .build());
        mediaSessionCompat.setPlaybackState(new PlaybackStateCompat.Builder()
                .setState(PlaybackStateCompat.STATE_PLAYING, mediaPlayer.getCurrentPosition(), playbackSpeed)
                .setActions(PlaybackStateCompat.ACTION_SEEK_TO)
                .build());

        if (isPlaying) {
            notificationBuilder
                    .addAction(R.drawable.ic_skip_previous, "previous", getPendingIntent(this, ACTION_PREVIOUS, previousPosition(sPosition)))
                    .addAction(R.drawable.ic_pause, "pause", getPendingIntent(this, ACTION_PAUSE, sPosition))
                    .addAction(R.drawable.ic_skip_next, "next", getPendingIntent(this, ACTION_NEXT, nextPosition(sPosition)))
                    .addAction(R.drawable.ic_clear, "clear", getPendingIntent(this, ACTION_CLEAR, sPosition));
        } else {
            notificationBuilder
                    .addAction(R.drawable.ic_skip_previous, "previous", getPendingIntent(this, ACTION_PREVIOUS, previousPosition(sPosition)))
                    .addAction(R.drawable.ic_play, "play", getPendingIntent(this, ACTION_RESUME, sPosition))
                    .addAction(R.drawable.ic_skip_next, "next", getPendingIntent(this, ACTION_NEXT, nextPosition(sPosition)))
                    .addAction(R.drawable.ic_clear, "clear", getPendingIntent(this, ACTION_CLEAR, sPosition));
        }
        Notification notification = notificationBuilder.build();
        startForeground(1, notification);
    }

    private PendingIntent getPendingIntent(Context context, int action, int pendingPosition) {
        Intent intent = new Intent(this, DataReceive.class);
        intent.putExtra(ACTION, action);
        intent.putExtra(POSITION, pendingPosition);
        return PendingIntent.getBroadcast(context.getApplicationContext(), action, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
    }

    private int previousPosition(int previousPosition){

        if (previousPosition == 0) {
            previousPosition = mListSong.size() - 1;
        } else {
            previousPosition--;
        }
        return previousPosition;
    }

    private int nextPosition(int nextPosition){
        if (nextPosition == mListSong.size() - 1) {
            nextPosition = 0;
        } else {
            nextPosition++;
        }
        return nextPosition;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
