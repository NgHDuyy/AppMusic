package com.example.musicapp.data_local;

import android.content.Context;
import android.content.SharedPreferences;

public class SharePreference {
    private static final String SHARE_PREFERENCE = "SHARE_PREFERENCE";
    private Context mContext;

    public SharePreference(Context mContext) {
        this.mContext = mContext;
    }

    public void putStringValue(String key, String value){
        SharedPreferences sharePreferences = mContext.getSharedPreferences(SHARE_PREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharePreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }
    public String getStringValue(String key){
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARE_PREFERENCE, Context.MODE_PRIVATE);
        return sharedPreferences.getString(key,"");
    }
}
