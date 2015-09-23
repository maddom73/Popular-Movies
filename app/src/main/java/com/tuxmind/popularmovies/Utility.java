package com.tuxmind.popularmovies;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.format.Time;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by maddom73 on 30/08/15.
 */
public class Utility {
    public static String getPreferredSort(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_sort_key),
                context.getString(R.string.pref_sort_default));
    }

    public static boolean getFavorite(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String displayFavoriteKey = context.getString(R.string.pref_enable_favorite_key);
        boolean displayFavorite = prefs.getBoolean(displayFavoriteKey,
                Boolean.parseBoolean(context.getString(R.string.pref_enable_favorite_default)));

        return displayFavorite;
    }

}

