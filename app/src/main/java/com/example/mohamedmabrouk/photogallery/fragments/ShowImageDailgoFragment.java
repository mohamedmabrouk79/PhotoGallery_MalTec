package com.example.mohamedmabrouk.photogallery.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.WallpaperManager;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.mohamedmabrouk.photogallery.R;
import com.example.mohamedmabrouk.photogallery.services.ThumbnailDownloader;
import com.squareup.picasso.Picasso;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;
/**
 * Created by moham on 20/01/2017.
 */

public class ShowImageDailgoFragment extends android.support.v4.app.DialogFragment {
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
        final String iamge=getArguments().getString(SHOW_IMAGE);
        final Bitmap bitmap = ThumbnailDownloader.getBitmap(iamge);

        Picasso.with(getActivity()).load(Uri.parse(iamge)).into(mImageView);
        return new AlertDialog.Builder(getActivity()).setView(view)
                .setPositiveButton(R.string.dowanload, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        try {
                            SaveImage(bitmap);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }}).setNegativeButton(R.string.set_as, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        WallpaperManager manager=WallpaperManager.getInstance(getActivity());
                        try {
                            manager.setBitmap(bitmap);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).show();
    }

private  void SaveImage(Bitmap bitmap) throws  Exception{
    OutputStream outputStream;
    File filePath= Environment.getExternalStorageDirectory();
    File  file=new File(filePath.getAbsolutePath()+"/Download", UUID.randomUUID()+".png");
    Toast.makeText(getActivity(), file+"", Toast.LENGTH_SHORT).show();
    outputStream=new FileOutputStream(file);
    bitmap.compress(Bitmap.CompressFormat.PNG,100,outputStream);
    outputStream.flush();
    outputStream.close();
}

//    private  void SaveImage(Bitmap finalBitmap) {
//
//        String root = Environment.getExternalStorageDirectory().getAbsolutePath();
//        Toast.makeText(getActivity(), root, Toast.LENGTH_SHORT).show();
//        File myDir = new File(root + "/saved_images");
//        myDir.mkdirs();
//
//        String fname = "Image-"+ UUID.randomUUID() +".jpg";
//        File file = new File (myDir, fname);
//        if (file.exists ()) file.delete ();
//        try {
//            FileOutputStream out = new FileOutputStream(file);
//            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
//            out.flush();
//            out.close();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
}
