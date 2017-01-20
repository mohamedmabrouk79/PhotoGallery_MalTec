package com.example.mohamedmabrouk.photogallery;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

public class PhotoGalleryActivity extends  SingleActivityFragment {

    public static Intent newIntent(Context context) {
        return new Intent(context, PhotoGalleryActivity.class);
    }

    @Override
    public Fragment CreateFragment() {
        return new PhotoGalleryFragment().newInstance();
    }
}
