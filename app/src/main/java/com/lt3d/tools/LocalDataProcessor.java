package com.lt3d.tools;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lt3d.data.Setting;

import static android.content.Context.MODE_PRIVATE;

public class LocalDataProcessor {
    public static void savePreference(Setting setting, Context activity) {
        SharedPreferences preferences = activity.getSharedPreferences("setting", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        final GsonBuilder builder = new GsonBuilder();
        final Gson gson = builder.create();
        String setting_serialized = gson.toJson(setting);
        editor.putString("setting", setting_serialized);

        editor.apply();
        editor.commit();
    }

    public static Setting readPreference(Context activity) {
        SharedPreferences preferences = activity.getSharedPreferences("setting", MODE_PRIVATE);

        final GsonBuilder builder = new GsonBuilder();
        final Gson gson = builder.create();
        String setting_serialized = preferences.getString("setting", gson.toJson(new Setting()));
        return gson.fromJson(setting_serialized, Setting.class);
    }
}
