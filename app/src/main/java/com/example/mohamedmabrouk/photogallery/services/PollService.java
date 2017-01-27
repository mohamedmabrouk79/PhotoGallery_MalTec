package com.example.mohamedmabrouk.photogallery.services;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.os.SystemClock;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.example.mohamedmabrouk.photogallery.PhotoGalleryActivity;
import com.example.mohamedmabrouk.photogallery.R;
import com.example.mohamedmabrouk.photogallery.dataBase.QueryPreferences;
import com.example.mohamedmabrouk.photogallery.model.FlickrFetchr;
import com.example.mohamedmabrouk.photogallery.model.GalleryItem;

import java.util.List;

/**
 * Created by Mohamed Mabrouk on 27/08/2016.
 */
public class PollService extends IntentService {

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public static String TAG="PollService";
    public static final String PERM_PRIVATE ="com.example.mohamedmabrouk.photogallery.PRIVATE";
    public static final String ACTION_SHOW_NOTIFICATION="com.example.mohamedmabrouk.photogallery.SHOW_NOTIFICATION";
    private static final long POLL_INTERVAL = 100*60;
    public static final String REQUEST_CODE = "REQUEST_CODE";
    public static final String NOTIFICATION = "NOTIFICATION";
    public static Intent newIntent(Context context){
        return new Intent(context,PollService.class);
    }

    public PollService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (!isNetworkAvailableAndConnected()) {
            return;
        }

        String query= QueryPreferences.getStoredQuery(this);
        String lastResultId=QueryPreferences.getLastResultId(this);
        List<GalleryItem> items;
        if (query==null){
            items=new FlickrFetchr().FecthRecentPhotos();
        }else{
            items=new FlickrFetchr().SearchItems(query);
        }

        if (items.size()==0){
            return;

        }

        String resultId=items.get(0).toString();
        if (resultId.equals(lastResultId)){
            Log.i(TAG, "Got an old result: " + resultId);
        }else{
            Log.i(TAG, "Got a new result: " + resultId);
            Resources resources = getResources();
            Intent i = PhotoGalleryActivity.newIntent(this);
            PendingIntent pi = PendingIntent.getActivity(this, 0, i, 0);
            Notification notification = new NotificationCompat.Builder(this)
                    .setTicker(resources.getString(R.string.new_pictures_title))
                    .setSmallIcon(android.R.drawable.ic_menu_report_image)
                    .setContentTitle(resources.getString(R.string.new_pictures_title))
                    .setContentText(resources.getString(R.string.new_pictures_text))
                    .setContentIntent(pi)
                    .setAutoCancel(true)
                    .build();
           /* NotificationManagerCompat notificationManager =
                    NotificationManagerCompat.from(this);
            notificationManager.notify(0, notification);
            sendBroadcast(new Intent(ACTION_SHOW_NOTIFICATION),PERM_PRIVATE);*/
            showBackgroundNotification(0,notification);

        }

        QueryPreferences.setLastResultId(this, resultId);

    }

    /******************* for check network connected and aviliable  ************/
    public boolean isNetworkAvailableAndConnected(){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

        boolean isAvailable=cm.getActiveNetworkInfo()!=null;
        boolean isConnected=isAvailable && cm.getActiveNetworkInfo().isConnected();
        return isConnected;
    }

    public static boolean isServiceAlarmOn(Context context) {
        Intent i = PollService.newIntent(context);
        PendingIntent pi = PendingIntent
                .getService(context, 0, i, PendingIntent.FLAG_NO_CREATE);
        return pi != null;
    }

    /************* for start Sending intent  ***************/
    public static void setServiceAlarm(Context context, boolean isOn){
     Intent intent=PollService.newIntent(context);
        PendingIntent pendingIntent=PendingIntent.getService(context, 0, intent, 0);
        AlarmManager alarmManager=(AlarmManager) context.getSystemService(ALARM_SERVICE);
        if (isOn){
            alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(),POLL_INTERVAL,pendingIntent);
        }else{
            alarmManager.cancel(pendingIntent);
            pendingIntent.cancel();
        }

        QueryPreferences.setAlarmOn(context,isOn);
    }

    /************** for show Notification **********************/
    public void showBackgroundNotification(int requestCode , Notification notification){
        Intent intent=new Intent(ACTION_SHOW_NOTIFICATION);
        intent.putExtra(REQUEST_CODE,requestCode);
        intent.putExtra(NOTIFICATION,notification);
        sendOrderedBroadcast(intent,PERM_PRIVATE,null,null, Activity.RESULT_OK,null,null);
    }

}
