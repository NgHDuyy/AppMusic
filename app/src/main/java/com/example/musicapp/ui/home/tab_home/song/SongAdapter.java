package com.example.musicapp.ui.home.tab_home.song;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicapp.R;
import com.example.musicapp.my_interface.IAddToFavorite;
import com.example.musicapp.my_interface.IClickItemSongListener;

import java.util.List;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongViewHolder> {

    private final List<Song> mListSong;

    private final IClickItemSongListener iClickItemSongListener;
    private final IAddToFavorite iAddToFavorite;

    public SongAdapter(List<Song> mListSong, IClickItemSongListener listener1, IAddToFavorite listener2) {
        this.mListSong = mListSong;
        this.iClickItemSongListener = listener1;
        this.iAddToFavorite = listener2;
    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_song, parent, false);
        return new SongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
        Song song = mListSong.get(position);

        if (song == null) {
            return;
        }

        holder.tvSongName.setText(song.getSongName());
        holder.tvSingerName.setText(song.getSingerName());
        holder.imgSong.setImageResource(song.getImageSong());

        holder.layoutClickToRun.setOnClickListener(view -> iClickItemSongListener.onClickItemSong(position));

        holder.imgAddToFavorite.setOnClickListener(view -> iAddToFavorite.addToFavorite(position, holder.imgAddToFavorite));

    }

    @Override
    public int getItemCount() {
        if (mListSong != null) {
            return mListSong.size();
        }
        return 0;
    }

    public static class SongViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvSongName;
        private final TextView tvSingerName;
        private final ImageView imgSong;
        private final ImageView imgAddToFavorite;
        private final LinearLayout layoutClickToRun;

        public SongViewHolder(@NonNull View itemView) {
            super(itemView);

            tvSongName = itemView.findViewById(R.id.tv_song_name);
            tvSingerName = itemView.findViewById(R.id.tv_singer_name);
            imgSong = itemView.findViewById(R.id.img_song);
            imgAddToFavorite = itemView.findViewById(R.id.img_add_to_favorite);
            layoutClickToRun = itemView.findViewById(R.id.layout_click_to_run);
        }
    }
}
