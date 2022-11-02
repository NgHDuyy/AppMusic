package com.example.musicapp.ui.home;

import static com.example.musicapp.receive.DataReceive.ACTION_RECEIVE;
import static com.example.musicapp.receive.DataReceive.POSITION_RECEIVE;
import static com.example.musicapp.service.Service.ACTION_NEXT;
import static com.example.musicapp.service.Service.ACTION_PREVIOUS;
import static com.example.musicapp.service.Service.LIST_FAVORITE;
import static com.example.musicapp.service.Service.LIST_HOME;
import static com.example.musicapp.service.Service.SEND_ACTION;
import static com.example.musicapp.service.Service.SEND_DATA_TO_ACTIVITY;
import static com.example.musicapp.service.Service.SEND_LIST;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;

import com.example.musicapp.R;
import com.example.musicapp.activity.RunActivity;
import com.example.musicapp.data_local.DataLocalManager;
import com.example.musicapp.databinding.FragmentHomeBinding;
import com.example.musicapp.service.Service;
import com.example.musicapp.ui.home.tab_home.ViewPagerTabHomeAdapter;
import com.example.musicapp.ui.home.tab_home.song.Song;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

public class HomeFragment extends Fragment {

    private ViewPager mViewPager;
    private BottomNavigationView bottomNavigationView;

    private FragmentHomeBinding binding;

    public LinearLayout layoutBottom;
    private List<Song> mListSong;

    private TextView songName, singerName;
    private ImageView imageSong, imgPrevious, imgPlayOrPause, imgNext;
    private SeekBar seekBar;

    private boolean isPlaying;
    private int position;
    private int list;

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
            case Service.ACTION_START:
                layoutBottom.setVisibility(View.VISIBLE);
                showInfoSong(hPosition);
                setStatusButtonPlayOrPause();
                break;
            case Service.ACTION_PAUSE:
                setStatusButtonPlayOrPause();
                break;
            case Service.ACTION_RESUME:
                setStatusButtonPlayOrPause();
                break;
            case Service.ACTION_CLEAR:
                layoutBottom.setVisibility(View.GONE);
                break;
            case Service.ACTION_PREVIOUS:
                showInfoSong(hPosition);
                break;
            case Service.ACTION_NEXT:
                showInfoSong(hPosition);
                break;
        }
    }

    private void showInfoSong(int sPosition) {
        imageSong.setImageResource(mListSong.get(sPosition).getImageSong());
        songName.setText(mListSong.get(sPosition).getSongName());
        singerName.setText(mListSong.get(sPosition).getSingerName());
        setSeekbar();
        imgPlayOrPause.setOnClickListener(view -> {
            if (isPlaying) {
                sendActionToService(Service.ACTION_PAUSE, sPosition);
            } else {
                sendActionToService(Service.ACTION_RESUME, sPosition);
            }
        });
        imgPrevious.setOnClickListener(view -> previous(sPosition));
        imgNext.setOnClickListener(view -> next(sPosition));
    }

    private void previous(int previousPosition) {
        if (previousPosition == 0){
            previousPosition = mListSong.size() -1;
        } else {
            previousPosition --;
        }
        sendActionToService(ACTION_PREVIOUS, previousPosition);
    }

    private void next(int nextPosition) {
        if (nextPosition == mListSong.size() -1){
            nextPosition = 0;
        } else {
            nextPosition ++;
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
        Intent intent = new Intent(getContext(), Service.class);
        intent.putExtra(ACTION_RECEIVE, action);
        intent.putExtra(POSITION_RECEIVE, position);
        requireActivity().startService(intent);
    }

    private void setSeekbar(){
        seekBar.setMax(mediaPlayer.getDuration());
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isPlaying){
                    seekBar.setProgress(mediaPlayer.getCurrentPosition());
                    handler.postDelayed(this, 100);
                }
            }
        }, 100);
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
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(broadcastReceiver, new IntentFilter(SEND_DATA_TO_ACTIVITY));

        initUI();

        return root;
    }

    private void initUI() {
        mViewPager = binding.viewPager;
        bottomNavigationView = binding.bottomNavigation;

        ViewPagerTabHomeAdapter viewPagerTabHomeAdapter = new ViewPagerTabHomeAdapter(getChildFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        mViewPager.setAdapter(viewPagerTabHomeAdapter);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.tab_list_song:
                    mViewPager.setCurrentItem(0);
                    break;
                case R.id.tab_favorite:
                    mViewPager.setCurrentItem(1);
                    break;
            }
            return true;
        });

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        bottomNavigationView.getMenu().findItem(R.id.tab_list_song).setChecked(true);
                        break;
                    case 1:
                        bottomNavigationView.getMenu().findItem(R.id.tab_favorite).setChecked(true);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        layoutBottom = binding.layoutBottom;

        layoutBottom.setOnClickListener(view -> {
            Intent intent = new Intent(getContext(), RunActivity.class);
            intent.putExtra(SEND_POSITION, position);
            intent.putExtra(SEND_LIST, list);
            intent.putExtra(SEND_STATUS_PLAYING, isPlaying);
            startActivity(intent);
        });

        songName = binding.songName;
        singerName = binding.singerName;
        imageSong = binding.imageSong;
        imgPrevious = binding.imgPrevious;
        imgNext = binding.imgNext;
        imgPlayOrPause = binding.imgPlayOrPause;
        seekBar = binding.seekbar;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        LocalBroadcastManager.getInstance(requireActivity()).unregisterReceiver(broadcastReceiver);
        Intent intent = new Intent(getActivity(), Service.class);
        requireActivity().stopService(intent);
        layoutBottom.setVisibility(View.GONE);
    }
}