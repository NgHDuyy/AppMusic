package com.example.musicapp.data_local;

import android.content.Context;

import com.example.musicapp.ui.home.tab_home.song.Song;
import com.google.gson.Gson;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DataLocalManager {
    private static final String LIST_USER = "LIST_USER";
    private static DataLocalManager instance;
    private SharePreference sharePreference;

    public static void init(Context context) {
        instance = new DataLocalManager();
        instance.sharePreference = new SharePreference(context);
    }

    public static DataLocalManager getInstance() {
        if (instance == null) {
            instance = new DataLocalManager();
        }
        return instance;
    }

    public static void setListSong(List<Song> listSong) {
        Gson gson = new Gson();
        JsonArray jsonArray = gson.toJsonTree(listSong).getAsJsonArray();
        String strJsonArray = jsonArray.toString();
        DataLocalManager.getInstance().sharePreference.putStringValue(LIST_USER, strJsonArray);
    }

    public static List<Song> getListSong() {
        String strJsonSong = DataLocalManager.getInstance().sharePreference.getStringValue(LIST_USER);
        List<Song> listSong = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(strJsonSong);
            JSONObject jsonObject;
            Song song;
            Gson gson = new Gson();
            for (int i = 0; i < jsonArray.length(); i++) {
                jsonObject = jsonArray.getJSONObject(i);
                song = gson.fromJson(jsonObject.toString(),Song.class);
                listSong.add(song);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return listSong;
    }
}
