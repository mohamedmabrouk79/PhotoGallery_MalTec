package com.example.mohamedmabrouk.photogallery;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.support.v4.app.Fragment;

import com.example.mohamedmabrouk.photogallery.dataBase.DBOpretion;
import com.example.mohamedmabrouk.photogallery.fragments.PhotoGalleryFragment;
import com.example.mohamedmabrouk.photogallery.fragments.PhotoGalleryFragmentNOInternet;
import com.example.mohamedmabrouk.photogallery.fragments.SingleActivityFragment;

public class PhotoGalleryActivity extends SingleActivityFragment {
    DBOpretion dbOpretion;
    public static Intent newIntent(Context context) {
        return new Intent(context, PhotoGalleryActivity.class);
    }

    @Override
    public Fragment CreateFragment() {
        if(isNetworkAvailableAndConnected()) {
            dbOpretion=new DBOpretion(this);
            dbOpretion.delete();
         //   Toast.makeText(this, "is connected", Toast.LENGTH_SHORT).show();
            return new PhotoGalleryFragment().newInstance();
        }else{
          //  Toast.makeText(this, "is not connected", Toast.LENGTH_SHORT).show();
           return new PhotoGalleryFragmentNOInternet();
        }

    }
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }
    public boolean isNetworkAvailableAndConnected(){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

        boolean isAvailable=cm.getActiveNetworkInfo()!=null;
        boolean isConnected=isAvailable && cm.getActiveNetworkInfo().isConnected();
        return isConnected;
    }
}
