package net.validcat.popularmoviesapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Dobrunov on 19.08.2015.
 */
public class Utility {

    public static String getPreferredSortOrder(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_order_key),
                context.getString(R.string.pref_order_popular));
    }
}
