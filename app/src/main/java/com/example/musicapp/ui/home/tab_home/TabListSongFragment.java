package com.example.musicapp.ui.home.tab_home;

import static com.example.musicapp.receive.DataReceive.ACTION_RECEIVE;
import static com.example.musicapp.receive.DataReceive.POSITION_RECEIVE;
import static com.example.musicapp.service.Service.ACTION_START;
import static com.example.musicapp.service.Service.LIST_HOME;
import static com.example.musicapp.service.Service.SEND_LIST;
import static com.example.musicapp.service.Service.SEND_POSITION;
import static com.example.musicapp.service.Service.SEND_STATUS_PLAYING;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicapp.R;
import com.example.musicapp.activity.RunActivity;
import com.example.musicapp.data_local.DataLocalManager;
import com.example.musicapp.service.Service;
import com.example.musicapp.ui.home.tab_home.song.Song;
import com.example.musicapp.ui.home.tab_home.song.SongAdapter;

import java.util.List;

public class TabListSongFragment extends Fragment {

    public static final String SEND_TO_FAVORITE = "SEND_TO_FAVORITE";
    public static final String SEND_SONG_TO_FAVORITE = "SEND_SONG_TO_FAVORITE";
    private View view;

    private final List<Song> mListSong = DataLocalManager.getListSong();

    private int sendPosition;


    public TabListSongFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_tab_list_song, container, false);

        setLayout();

        return view;
    }


    private void setLayout() {
        RecyclerView rcvSong = view.findViewById(R.id.rcv_list_song);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rcvSong.setLayoutManager(linearLayoutManager);
        SongAdapter songAdapter = new SongAdapter(mListSong, pos -> {
            sendPosition = pos;
            startRunActivity();
            startService();
        }, (pos, imgAddToFavorite) -> {
            if (mListSong.get(pos).isAddToFavorite()){
                imgAddToFavorite.setColorFilter(ContextCompat.getColor(imgAddToFavorite.getContext(), R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);
                mListSong.get(pos).setAddToFavorite(false);
                addToFavoriteFragment(mListSong.get(pos));
            }else {
                imgAddToFavorite.setColorFilter(ContextCompat.getColor(imgAddToFavorite.getContext(), R.color.red), android.graphics.PorterDuff.Mode.SRC_IN);
                mListSong.get(pos).setAddToFavorite(true);
                addToFavoriteFragment(mListSong.get(pos));
            }
        });
        rcvSong.setAdapter(songAdapter);
    }

    private void addToFavoriteFragment(Song song) {
        Intent intent = new Intent(SEND_TO_FAVORITE);
        Bundle bundle = new Bundle();
        bundle.putSerializable(SEND_SONG_TO_FAVORITE, song);
        intent.putExtras(bundle);
        LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(intent);
    }

    private void startService() {
        Intent intent = new Intent(getContext(), Service.class);
        intent.putExtra(POSITION_RECEIVE, sendPosition);
        intent.putExtra(ACTION_RECEIVE, ACTION_START);
        requireActivity().startService(intent);
    }
    private void startRunActivity(){
        Intent intent = new Intent(getContext(), RunActivity.class);
        intent.putExtra(SEND_POSITION, sendPosition);
        intent.putExtra(SEND_LIST, LIST_HOME);
        intent.putExtra(SEND_STATUS_PLAYING, true);
        requireActivity().startActivity(intent);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Intent intent = new Intent(getActivity(), Service.class);
        requireActivity().stopService(intent);
    }
}