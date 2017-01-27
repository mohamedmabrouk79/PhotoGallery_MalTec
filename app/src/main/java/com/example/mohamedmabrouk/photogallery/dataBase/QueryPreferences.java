package com.example.mohamedmabrouk.photogallery.dataBase;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
/**
 * Created by Mohamed Mabrouk on 26/08/2016.
 */
public class QueryPreferences {

    private static final String PREF_SEARCH_QUERY = "searchQuery";
    private static final String PREF_LAST_RESULT_ID = "lastResultId";
    private static final String PREF_IS_ALARM_ON = "isAlarmOn";
/********* to get query that stored in shared prefrances  *********/
    public static String getStoredQuery(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(PREF_SEARCH_QUERY, null);
    }

    /********* to set query  in shared prefrances  *********/
    public static void setQuery(Context context,String query){
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(PREF_SEARCH_QUERY, query).apply();
    }


    public static String getLastResultId(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(PREF_LAST_RESULT_ID, null);
    }
    public static void setLastResultId(Context context, String lastResultId) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(PREF_LAST_RESULT_ID, lastResultId)
                .apply();
    }

    public static boolean isAlarmOn(Context context){
        return  PreferenceManager.getDefaultSharedPreferences(context).getBoolean(PREF_IS_ALARM_ON,false);
    }

    public static void setAlarmOn(Context context, boolean b){
        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(PREF_IS_ALARM_ON,b).apply();
    }

}
