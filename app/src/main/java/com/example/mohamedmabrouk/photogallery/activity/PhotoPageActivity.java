package com.example.mohamedmabrouk.photogallery.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;

import com.example.mohamedmabrouk.photogallery.fragments.PhotoPageFragment;
import com.example.mohamedmabrouk.photogallery.fragments.SingleActivityFragment;

/**
 * Created by moham on 03/09/2016.
 */
public class PhotoPageActivity extends SingleActivityFragment {

    public static Intent newIntent(Context context, Uri photPageUri){
        Intent intent=new Intent(context,PhotoPageActivity.class);
        intent.setData(photPageUri);
        return intent;
    }
    @Override
    public Fragment CreateFragment() {
        return  PhotoPageFragment.newInstance(getIntent().getData());
    }
}
