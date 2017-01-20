package com.example.mohamedmabrouk.photogallery;

import android.app.Activity;
import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationManagerCompat;

/**
 * Created by mohamed on 02/09/2016.
 */
public class NotificationReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
if (getResultCode()!= Activity.RESULT_OK){
    return;
}
        int reqestCode=intent.getIntExtra(PollService.REQUEST_CODE,0);
        Notification notification=intent.getParcelableExtra(PollService.NOTIFICATION);
        NotificationManagerCompat notificationManagerCompat=NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(reqestCode,notification);
    }


}
