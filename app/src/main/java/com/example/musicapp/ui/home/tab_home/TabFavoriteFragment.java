package com.example.musicapp.ui.home.tab_home;

import static com.example.musicapp.receive.DataReceive.ACTION_RECEIVE;
import static com.example.musicapp.receive.DataReceive.POSITION_RECEIVE;
import static com.example.musicapp.service.Service.ACTION_START;
import static com.example.musicapp.service.Service.LIST_FAVORITE;
import static com.example.musicapp.service.Service.SEND_LIST;
import static com.example.musicapp.service.Service.SEND_POSITION;
import static com.example.musicapp.service.Service.SEND_STATUS_PLAYING;
import static com.example.musicapp.ui.home.tab_home.TabListSongFragment.SEND_SONG_TO_FAVORITE;
import static com.example.musicapp.ui.home.tab_home.TabListSongFragment.SEND_TO_FAVORITE;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicapp.R;
import com.example.musicapp.activity.RunActivity;
import com.example.musicapp.service.Service;
import com.example.musicapp.ui.home.tab_home.song.Song;
import com.example.musicapp.ui.home.tab_home.song.SongAdapter;

import java.util.ArrayList;
import java.util.List;

public class TabFavoriteFragment extends Fragment {

    private View view;

    public static List<Song> mListSongFavorite;

    private int sendPosition;

    public TabFavoriteFragment() {
    }

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle == null){
                return;
            }
            Song song = (Song) bundle.getSerializable(SEND_SONG_TO_FAVORITE);
            for (int i =0; i<mListSongFavorite.size();i++){
                if (song.getSongName().equals(mListSongFavorite.get(i).getSongName())){
                    mListSongFavorite.remove(song);
                    setLayout();
                    return;
                }
            }
            mListSongFavorite.add(song);
            setLayout();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_tab_favorite, container, false);

        mListSongFavorite = new ArrayList<>();
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(broadcastReceiver, new IntentFilter(SEND_TO_FAVORITE));

        return view;
    }

    private void setLayout() {
        RecyclerView rcvSong = view.findViewById(R.id.rcv_favorite_song);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rcvSong.setLayoutManager(linearLayoutManager);
        SongAdapter songAdapter = new SongAdapter(mListSongFavorite, pos -> {
            sendPosition = pos;
            startRunActivity();
            startService();
        }, (pos, imgAddToFavorite) -> {

        });
        rcvSong.setAdapter(songAdapter);

    }

    private void startService() {
        Intent intent = new Intent(getContext(), Service.class);
        intent.putExtra(POSITION_RECEIVE, sendPosition);
        intent.putExtra(SEND_LIST, LIST_FAVORITE);
        intent.putExtra(ACTION_RECEIVE, ACTION_START);
        requireActivity().startService(intent);
    }

    private void startRunActivity(){
        Intent intent = new Intent(getContext(), RunActivity.class);
        intent.putExtra(SEND_POSITION, sendPosition);
        intent.putExtra(SEND_LIST, LIST_FAVORITE);
        intent.putExtra(SEND_STATUS_PLAYING, true);
        requireActivity().startActivity(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(broadcastReceiver);
    }
}