package com.example.musicapp.activity;

import static com.example.musicapp.receive.DataReceive.ACTION_RECEIVE;
import static com.example.musicapp.receive.DataReceive.POSITION_RECEIVE;
import static com.example.musicapp.service.Service.*;
import static com.example.musicapp.service.Service.ACTION_NEXT;
import static com.example.musicapp.service.Service.ACTION_PREVIOUS;
import static com.example.musicapp.service.Service.ACTION_START;
import static com.example.musicapp.service.Service.SEND_ACTION;
import static com.example.musicapp.service.Service.SEND_DATA_TO_ACTIVITY;
import static com.example.musicapp.service.Service.SEND_POSITION;
import static com.example.musicapp.service.Service.SEND_STATUS_PLAYING;
import static com.example.musicapp.service.Service.mediaPlayer;
import static com.example.musicapp.ui.home.tab_home.TabFavoriteFragment.mListSongFavorite;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.musicapp.R;
import com.example.musicapp.data_local.DataLocalManager;
import com.example.musicapp.service.Service;
import com.example.musicapp.ui.home.tab_home.song.Song;

import java.text.SimpleDateFormat;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class RunActivity extends AppCompatActivity {

    private ImageView imgBack, imgPrevious, imgPlayOrPause, imgNext;
    private TextView tvTitle, tvSinger, tvTimeCurrent;
    private  TextView tvTimeDuration;
    private CircleImageView diskSong;
    private SeekBar seekBar;

    private List<Song> mListSong;

    private boolean isPlaying;
    private int position;
    private int list;

    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss");

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle == null) {
                return;
            }
            list = bundle.getInt(SEND_LIST);
            int actionMusic;
            if (list == LIST_HOME){
                mListSong = DataLocalManager.getListSong();
                position = bundle.getInt(SEND_POSITION);
                isPlaying = bundle.getBoolean(SEND_STATUS_PLAYING);
                actionMusic = bundle.getInt(SEND_ACTION);
                handlerLayoutMusic(actionMusic, position);
            } else if (list == LIST_FAVORITE){
                mListSong = mListSongFavorite;
                position = bundle.getInt(SEND_POSITION);
                isPlaying = bundle.getBoolean(SEND_STATUS_PLAYING);
                actionMusic = bundle.getInt(SEND_ACTION);
                handlerLayoutMusic(actionMusic, position);
            }
        }
    };

    private void handlerLayoutMusic(int action, int hPosition) {
        switch (action) {
            case ACTION_START:
                showInfoSong(hPosition);
                setStatusButtonPlayOrPause();
                break;
            case ACTION_PAUSE:
                setStatusButtonPlayOrPause();
                break;
            case ACTION_RESUME:
                setStatusButtonPlayOrPause();
                break;
            case ACTION_CLEAR:
                isPlaying = false;
                setStatusButtonPlayOrPause();
                stopAnimation();
                tvTimeCurrent.setText("00:00");
                seekBar.setProgress(0);
                break;
            case ACTION_PREVIOUS:
                showInfoSong(hPosition);
                startAnimation();
                break;
            case ACTION_NEXT:
                showInfoSong(hPosition);
                startAnimation();
                break;
        }
    }

    private void showInfoSong(int sPosition) {
        diskSong.setImageResource(mListSong.get(sPosition).getImageSong());
        tvTitle.setText(mListSong.get(sPosition).getSongName());
        tvSinger.setText(mListSong.get(sPosition).getSingerName());
        setTimeTotal();
        updateTimeCurrent();
        imgPlayOrPause.setOnClickListener(view -> {
            if (isPlaying) {
                sendActionToService(ACTION_PAUSE, sPosition);
                stopAnimation();
                isPlaying = false;
            } else {
                sendActionToService(ACTION_RESUME, sPosition);
                startAnimation();
                isPlaying = true;
            }
        });
        imgPrevious.setOnClickListener(view -> previous(sPosition));
        imgNext.setOnClickListener(view -> next(sPosition));
        imgBack.setOnClickListener(view -> finish());
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());
            }
        });
        mediaPlayer.setOnCompletionListener(mp -> {
            Intent intent = new Intent(this, Service.class);
            intent.putExtra(ACTION_RECEIVE, ACTION_NEXT);
            intent.putExtra(POSITION_RECEIVE, nextPosition(position));
            startService(intent);
        });
    }

    private int nextPosition(int nextPosition) {
        if (nextPosition == mListSong.size() - 1) {
            nextPosition = 0;
        } else {
            nextPosition++;
        }
        return nextPosition;
    }

    private void previous(int previousPosition) {
        if (previousPosition == 0) {
            previousPosition = mListSong.size() - 1;
        } else {
            previousPosition--;
        }
        sendActionToService(ACTION_PREVIOUS, previousPosition);
    }

    private void next(int nextPosition) {
        if (nextPosition == mListSong.size() - 1) {
            nextPosition = 0;
        } else {
            nextPosition++;
        }
        sendActionToService(ACTION_NEXT, nextPosition);
    }

    private void setStatusButtonPlayOrPause() {
        if (isPlaying) {
            imgPlayOrPause.setImageResource(R.drawable.ic_pause);
        } else {
            imgPlayOrPause.setImageResource(R.drawable.ic_play);
        }
    }

    private void sendActionToService(int action, int position) {
        Intent intent = new Intent(this, Service.class);
        intent.putExtra(ACTION_RECEIVE, action);
        intent.putExtra(SEND_LIST, list);
        intent.putExtra(POSITION_RECEIVE,  position);
        startService(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run);

        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter(SEND_DATA_TO_ACTIVITY));

        initUi();
        getData();

    }

    private void getData() {
        Intent intent = getIntent();
        position = intent.getIntExtra(SEND_POSITION, 0);
        isPlaying = intent.getBooleanExtra(SEND_STATUS_PLAYING, false);
        list = intent.getIntExtra(SEND_LIST, LIST_HOME);
        if (list == LIST_HOME){
            mListSong = DataLocalManager.getListSong();
        }else if (list == LIST_FAVORITE){
            mListSong = mListSongFavorite;
        }
        showInfoSong(position);
        if (isPlaying){
            startAnimation();
        }else {
            stopAnimation();
        }
        setStatusButtonPlayOrPause();
    }

    private void initUi() {
        tvTitle = findViewById(R.id.tv_title);
        tvSinger = findViewById(R.id.tv_singer);
        tvTimeCurrent = findViewById(R.id.tv_time_current);
        tvTimeDuration = findViewById(R.id.tv_time_total);
        imgBack = findViewById(R.id.img_back);
        imgPrevious = findViewById(R.id.previous);
        imgPlayOrPause = findViewById(R.id.run_pause);
        imgNext = findViewById(R.id.next);
        diskSong = findViewById(R.id.img_disc_song);
        seekBar = findViewById(R.id.seekbar);
    }

    private void startAnimation() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                diskSong.animate()
                        .rotationBy(360)
                        .withEndAction(this)
                        .setDuration(10000)
                        .setInterpolator(new LinearInterpolator())
                        .start();
            }
        };
        diskSong.animate()
                .rotationBy(360)
                .withEndAction(runnable)
                .setDuration(10000)
                .setInterpolator(new LinearInterpolator())
                .start();
    }

    private void stopAnimation() {
        diskSong.animate().cancel();
    }

    private void setTimeTotal() {
        tvTimeDuration.setText(simpleDateFormat.format(mediaPlayer.getDuration()));
        seekBar.setMax(mediaPlayer.getDuration());
    }

    private void updateTimeCurrent() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isPlaying){
                    tvTimeCurrent.setText(simpleDateFormat.format(mediaPlayer.getCurrentPosition()));
                    seekBar.setProgress(mediaPlayer.getCurrentPosition());
                    handler.postDelayed(this, 100);
                }
            }
        }, 100);
    }
}