package com.example.mohamedmabrouk.photogallery.fragments;

import android.app.WallpaperManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.support.v7.widget.SearchView;
import android.widget.Toast;

import com.example.mohamedmabrouk.photogallery.activity.PhotoPageActivity;
import com.example.mohamedmabrouk.photogallery.R;
import com.example.mohamedmabrouk.photogallery.dataBase.DBOpretion;
import com.example.mohamedmabrouk.photogallery.dataBase.QueryPreferences;
import com.example.mohamedmabrouk.photogallery.model.FlickrFetchr;
import com.example.mohamedmabrouk.photogallery.model.GalleryItem;
import com.example.mohamedmabrouk.photogallery.services.PollService;
import com.example.mohamedmabrouk.photogallery.services.ThumbnailDownloader;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Mohamed Mabrouk on 21/08/2016.
 */
public class PhotoGalleryFragment extends VisibleFragment {
    private RecyclerView mPhotoRecyclerView;
    private static final String TAG = "PhotoGalleryFragment";
    public static List<GalleryItem> mItems = new ArrayList<>();
    private ThumbnailDownloader<PhotoHolder> mthumbnailDownloader;
    public String myquery=null;
    private DBOpretion dbOpretion;


    public static PhotoGalleryFragment newInstance(){
        return new PhotoGalleryFragment();
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      dbOpretion=new DBOpretion(getActivity());
       UpdateItems();
        setRetainInstance(true);
        Handler responsHandler=new Handler();
        mthumbnailDownloader=new ThumbnailDownloader<PhotoHolder>(responsHandler);
        mthumbnailDownloader.setThumbnailDownloadListener(new ThumbnailDownloader.ThumbnailDownloadListener<PhotoHolder>() {
            @Override
            public void onThumbnailDownloaded(PhotoHolder target, Bitmap thumbnail) {
                Drawable drawable = new BitmapDrawable(getResources(), thumbnail);
                target.bindDrawable(drawable);
            }
        });
        setHasOptionsMenu(true);
        mthumbnailDownloader.start();
        mthumbnailDownloader.getLooper();
        Log.i(TAG, "Background thread started");

    }
     SearchView searchView;
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu, menu);
        MenuItem SearchItem=menu.findItem(R.id.menu_item_search);
         searchView=(SearchView) SearchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d(TAG, "QueryTextSubmit: " + query);
                myquery=query;
                QueryPreferences.setQuery(getActivity(), query);
                UpdateItems();
                searchView.onActionViewCollapsed();
                searchView.setQuery("", false);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d(TAG, "QueryTextChange: " + newText);
/*
                QueryPreferences.setQuery(getActivity(), newText);
                UpdateItems();*/
                return false;
            }
        });

        MenuItem toggleTitle=menu.findItem(R.id.menu_item_toggle_polling);
        if (PollService.isServiceAlarmOn(getActivity())){
            toggleTitle.setTitle(R.string.stop_polling);
        }else{
            toggleTitle.setTitle(R.string.start_polling);
        }


        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = QueryPreferences.getStoredQuery(getActivity());

            }
        });

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_clear:
                QueryPreferences.setQuery(getActivity(), null);
                searchView.setQuery(null,false);
                UpdateItems();
                return true;
            case R.id.menu_item_toggle_polling:
                boolean shouldStartAlarm=!PollService.isServiceAlarmOn(getActivity());
                PollService.setServiceAlarm(getActivity(),shouldStartAlarm);
                getActivity().invalidateOptionsMenu();
                return true;
            default:
            return super.onOptionsItemSelected(item);
        }
    }


    public void UpdateItems(){
      String query=  QueryPreferences.getStoredQuery(getActivity());
        new FetchItemsTask(query).execute();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mthumbnailDownloader.quit();
        Log.i(TAG, "Background thread started");
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.photo_gallery,container,false);
        mPhotoRecyclerView=(RecyclerView)view.findViewById(R.id.photo_gallery_recycle_view);
      /*  mPhotoRecyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                int width = mPhotoRecyclerView.getWidth(); // width - activity's field
              //  mPhotoRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), width/120));
                Log.d("myLogs", width + ""); //return rigth value
            }
        });*/

        mPhotoRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        setupAdapter();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mthumbnailDownloader.ClearQueue();
    }

    public void setupAdapter(){
        if (isAdded()){
            mPhotoRecyclerView.setAdapter(new PhotoAdapter(mItems));
        }
    }

    /**********  this class for fetch url and data from flickr site ***********/
    private class  FetchItemsTask extends AsyncTask<Void,Integer,List<GalleryItem>>{
        String mQuery;
        public FetchItemsTask(String query) {
            this.mQuery=query;
        }

        @Override
        protected List<GalleryItem> doInBackground(Void... params) {
     // String Query="ropot";/**this  Query come from Search View ********/
            if(mQuery==null){
            return     new FlickrFetchr().FecthRecentPhotos();
            }else{
             return    new FlickrFetchr().SearchItems(mQuery);
            }
        }

        @Override
        protected void onPostExecute(List<GalleryItem> items) {
           mItems=items;
            setupAdapter();
        }
    }


    /*******    class for view holder     *********/
    private class PhotoHolder extends RecyclerView.ViewHolder {
        private GalleryItem mItem;
        private ImageView mPhotoView;
        public PhotoHolder(View itemView) {
            super(itemView);
            mPhotoView = (ImageView) itemView.findViewById(R.id.fargment_photo_image_view);

        }

        public void bindDrawable(Drawable drawable){
            mPhotoView.setImageDrawable(drawable);
        }
        private   String SaveImage(Bitmap bitmap) throws  Exception{
            OutputStream outputStream;
            File filePath= Environment.getExternalStorageDirectory();
            File  file=new File(filePath.getAbsolutePath()+"/Download", UUID.randomUUID()+".png");
            Toast.makeText(getActivity(), file+"", Toast.LENGTH_SHORT).show();
            outputStream=new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG,100,outputStream);
            outputStream.flush();
            outputStream.close();

            return String.valueOf(file);
        }
        public void bindGalleryItem(final GalleryItem item) {
           dbOpretion.insert(item.getUrl());
            mItem=item;
            Picasso.with(getActivity()).load(item.getUrl()).placeholder(R.drawable.pp).into(mPhotoView);
            /********************** image view action **************/

            mPhotoView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                Intent intent= PhotoPageActivity.newIntent(getActivity(),item.getPageUri());
                    startActivity(intent);
                }
            });

            mPhotoView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    PopupMenu popupMenu=new PopupMenu(getActivity(),mPhotoView);

                    popupMenu.getMenuInflater().inflate(R.menu.popmenu,popupMenu.getMenu());
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            Bitmap bitmap=ThumbnailDownloader.getBitmap(mItem.getUrl());
                            switch (item.getItemId()){
                                case R.id.show:
                                    FragmentManager fragmentManager=getFragmentManager();
                                    ShowImageDailgoFragment fragment=ShowImageDailgoFragment.newInstance(mItem.getUrl());
                                    fragment.show(fragmentManager,"mohamed");
                                    return true;
                                case R.id.delete:
                                    dbOpretion.deleteItem(mItem.getUrl());
                                    UpdateItems();
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
                                        intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(SaveImage(ThumbnailDownloader.getBitmap(mItem.getUrl()))));
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
    }


    /********* adapter class *********/
    private class PhotoAdapter extends RecyclerView.Adapter<PhotoHolder> {
        private List<GalleryItem> mGalleryItems;
        public PhotoAdapter(List<GalleryItem> galleryItems) {
            mGalleryItems = galleryItems;
        }
        @Override
        public PhotoHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            LayoutInflater inflater=LayoutInflater.from(getActivity());
            View view=inflater.inflate(R.layout.gallery_item,viewGroup,false);
            return new PhotoHolder(view);
        }
        @Override
        public void onBindViewHolder(PhotoHolder photoHolder, int position) {
            GalleryItem galleryItem = mGalleryItems.get(position);
            Drawable drawable=getResources().getDrawable(R.drawable.pp);
            photoHolder.bindGalleryItem(galleryItem);
            photoHolder.bindDrawable(drawable);
            mthumbnailDownloader.queuThumbnail(photoHolder, galleryItem.getUrl());
        }
        @Override
        public int getItemCount() {
            return mGalleryItems.size();

        }

    }


}
