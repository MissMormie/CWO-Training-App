package nl.multimedia_engineer.cwo_app.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import nl.multimedia_engineer.cwo_app.R;

public class PreferenceUtil {


    public static String getPreferenceString(Context context, String preferenceName, String defaultValue) {
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString(preferenceName, defaultValue);
    }

}
