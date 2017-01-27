package com.example.mohamedmabrouk.photogallery.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.mohamedmabrouk.photogallery.dataBase.QueryPreferences;
import com.example.mohamedmabrouk.photogallery.services.PollService;

/**
 * Created by mohamed on 02/09/2016.
 */
public class StartupReceiver extends BroadcastReceiver {
    public static final String TAG="StartupReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.v(TAG,"Received broadcast intent: " + intent.getAction());
        boolean isOn= QueryPreferences.isAlarmOn(context);
        PollService.setServiceAlarm(context,isOn);

    }
}
