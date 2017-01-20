package com.example.mohamedmabrouk.photogallery;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;

/**
 * Created by Mohamed Mabrouk on 21/08/2016.
 */
public abstract class SingleActivityFragment extends AppCompatActivity{
   public abstract  Fragment CreateFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo__gallery);
        FragmentManager fragmentManager=getSupportFragmentManager();
        Fragment fragment=fragmentManager.findFragmentById(R.id.FragmentContaner);
        if (fragment==null){
            fragment=CreateFragment();
            fragmentManager.beginTransaction().add(R.id.FragmentContaner,fragment).commit();
        }
    }


}
