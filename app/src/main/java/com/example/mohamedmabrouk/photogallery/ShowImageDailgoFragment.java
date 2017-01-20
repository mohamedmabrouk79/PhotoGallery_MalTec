package com.example.mohamedmabrouk.photogallery;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * Created by moham on 20/01/2017.
 */

public class ShowImageDailgoFragment extends DialogFragment {
    public static final String SHOW_IMAGE="showImage";
    private ImageView  mImageView;
    public static ShowImageDailgoFragment newInstance(String drawable){
        Bundle bundle=new Bundle();
        bundle.putSerializable(SHOW_IMAGE,  drawable);
        ShowImageDailgoFragment fragment=new ShowImageDailgoFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view= LayoutInflater.from(getActivity()).inflate(R.layout.show_photo,null);
        mImageView= (ImageView) view.findViewById(R.id.download_show_image);
        String iamge=getArguments().getString(SHOW_IMAGE);
        Picasso.with(getActivity()).load(Uri.parse(iamge)).into(mImageView);
        return new AlertDialog.Builder(getActivity()).setView(view)
                .setPositiveButton(R.string.dowanload, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).show();
    }
}
