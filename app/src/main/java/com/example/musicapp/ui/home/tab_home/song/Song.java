package com.example.musicapp.ui.home.tab_home.song;

import java.io.Serializable;

public class Song implements Serializable {
    private String songName;
    private String singerName;
    private int imageSong;
    private int resource;
    private boolean isAddToFavorite;

    public Song(String songName, String singerName, int imageSong, int resource) {
        this.songName = songName;
        this.singerName = singerName;
        this.imageSong = imageSong;
        this.resource = resource;
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public String getSingerName() {
        return singerName;
    }

    public void setSingerName(String singerName) {
        this.singerName = singerName;
    }

    public int getImageSong() {
        return imageSong;
    }

    public void setImageSong(int imageSong) {
        this.imageSong = imageSong;
    }

    public int getResource() {
        return resource;
    }

    public void setResource(int resource) {
        this.resource = resource;
    }

    public boolean isAddToFavorite() {
        return isAddToFavorite;
    }

    public void setAddToFavorite(boolean addToFavorite) {
        isAddToFavorite = addToFavorite;
    }
}
