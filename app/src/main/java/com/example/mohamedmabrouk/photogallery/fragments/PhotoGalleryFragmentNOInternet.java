package com.example.mohamedmabrouk.photogallery.fragments;

import android.app.WallpaperManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.mohamedmabrouk.photogallery.R;
import com.example.mohamedmabrouk.photogallery.dataBase.DBOpretion;
import com.example.mohamedmabrouk.photogallery.services.ThumbnailDownloader;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.UUID;

/**
 * Created by moham on 25/01/2017.
 */

public class PhotoGalleryFragmentNOInternet  extends Fragment{
    private RecyclerView mRecyclerView;
    private DBOpretion dbOpretion;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.photo_gallery,container,false);
        mRecyclerView= (RecyclerView) view.findViewById(R.id.photo_gallery_recycle_view);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(),3));
        dbOpretion=new DBOpretion(getActivity());
        upadateUi();
        return view;
    }

    private void upadateUi(){
        List<String>  list=dbOpretion.getList();
        adapter adapter=new adapter(list);
        mRecyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
    SearchView searchView;

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu,menu);
        MenuItem SearchItem=menu.findItem(R.id.menu_item_search);
        searchView=(SearchView) SearchItem.getActionView();
    }

    class Holder extends RecyclerView.ViewHolder{
        private String mUrl;
        private ImageView imageView;
        public Holder(View itemView) {
            super(itemView);
            imageView= (ImageView) itemView.findViewById(R.id.fargment_photo_image_view);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FragmentManager fragmentManager=getFragmentManager();
                    ShowImageDailgoFragment fragment=ShowImageDailgoFragment.newInstance(mUrl);
                    fragment.show(fragmentManager,"mohamed");
                }
            });
            imageView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view){
                    PopupMenu popupMenu=new PopupMenu(getActivity(),imageView);
                    popupMenu.getMenuInflater().inflate(R.menu.popmenu,popupMenu.getMenu());

                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            Bitmap bitmap= ThumbnailDownloader.getBitmap(mUrl);
                            switch (item.getItemId()){
                                case R.id.show:
                                    FragmentManager fragmentManager=getFragmentManager();
                                    ShowImageDailgoFragment fragment=ShowImageDailgoFragment.newInstance(mUrl);
                                    fragment.show(fragmentManager,"mohamed");
                                    return true;
                                case R.id.delete:
                                    dbOpretion.deleteItem(mUrl);
                                    upadateUi();
                                    return true;
                                case R.id.dowanload:
                                    try {
                                        SaveImage(bitmap);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    return true;
                                case R.id.setas:
                                    WallpaperManager manager=WallpaperManager.getInstance(getActivity());
                                    try {
                                        manager.setBitmap(bitmap);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    return true;
                                case R.id.share:
                                    Intent intent=new Intent(Intent.ACTION_SEND);
                                    intent.setType("image/*");
                                    try {
                                        intent.putExtra(Intent.EXTRA_STREAM,Uri.parse(SaveImage(bitmap)));
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    startActivity(Intent.createChooser(intent,"Choose App"));
                                    return true;
                            }
                            return true;
                        }
                    });
                    popupMenu.show();
                    return true;
                }
            });
        }
        public void bindIamge(String url){
            mUrl=url;
           // imageView.setImageResource(R.drawable.pp);
           Picasso.with(getActivity()).load(Uri.parse(url)).into(imageView);
        }
    }
    private  String SaveImage(Bitmap bitmap) throws  Exception{
        OutputStream outputStream;
        File filePath= Environment.getExternalStorageDirectory();
        File  file=new File(filePath.getAbsolutePath()+"/Download", UUID.randomUUID()+".png");
        Toast.makeText(getActivity(), file+"", Toast.LENGTH_SHORT).show();
        outputStream=new FileOutputStream(file);
        bitmap.compress(Bitmap.CompressFormat.PNG,100,outputStream);
        outputStream.flush();
        outputStream.close();
        return  String.valueOf(file);
    }

    class adapter extends RecyclerView.Adapter<Holder> {
        private List<String> list;

        public adapter(List<String> mList){
            list=mList;
        }
        @Override
        public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view=LayoutInflater.from(getActivity()).inflate(R.layout.gallery_item,parent,false);
            return new Holder(view);
        }

        @Override
        public void onBindViewHolder(Holder holder, int position) {
        String s=list.get(position);
            holder.bindIamge(s);
        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }
}
