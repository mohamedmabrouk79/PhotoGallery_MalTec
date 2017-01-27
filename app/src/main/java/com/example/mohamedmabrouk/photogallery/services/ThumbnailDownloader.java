package com.example.mohamedmabrouk.photogallery.services;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.v4.util.LruCache;
import android.util.Log;

import com.example.mohamedmabrouk.photogallery.model.FlickrFetchr;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by Mohamed Mabrouk on 23/08/2016.
 */
public class ThumbnailDownloader<T> extends HandlerThread {
    private static final String TAG = "ThumbnailDownloader";
    private static final int MESSAGE_DOWNLOAD = 0;
    private ConcurrentMap<T,String> mRequestMap=new ConcurrentHashMap<>();
    private Handler mRequestHandler;
    private Handler mResponseHandler;
    private ThumbnailDownloadListener<T> mThumbnailDownloadListener;
    private static final int MESSAGE_PRELOAD = 1;
    private static final int CACHE_SIZE = 400;
    LruCache<String, Bitmap> mCache;
    public interface ThumbnailDownloadListener<T> {
        void onThumbnailDownloaded(T target, Bitmap thumbnail);
    }
    public void setThumbnailDownloadListener(ThumbnailDownloadListener<T> listener) {
        mThumbnailDownloadListener = listener;
    }
    public ThumbnailDownloader(Handler  handler) {
        super(TAG);
        mResponseHandler=handler;
        mCache = new LruCache<String, Bitmap>(CACHE_SIZE);
    }

    private void preload(final T token) {
        String url = mRequestMap.get(token);
        preload(url);
    }

    public void queuePreload(String url) {
        if (mCache.get(url) != null)return;

        mRequestHandler
                .obtainMessage(MESSAGE_PRELOAD, url)
                .sendToTarget();
    }

    public static Bitmap getBitmap(String url) {

        try {
            byte[] bitmapBytes = new FlickrFetchr().getUrlBytes(url);
            Bitmap bitmapDecode = BitmapFactory
                    .decodeByteArray(bitmapBytes, 0, bitmapBytes.length);
            Log.i(TAG, "bitmap created");
            return bitmapDecode;
        } catch (IOException ioe) {
            Log.e(TAG, "Error downloading image", ioe);
        }
        return null;
    }

    private void preload(String url) {
        if(url == null)
            return;
        if (mCache.get(url) != null)
            return;
        Bitmap bitmap = getBitmap(url);
        if (bitmap != null)
            mCache.put(url, bitmap);
    }

    public Bitmap checkCache(String url) {
        return mCache.get(url);
    }

        @Override
    protected void onLooperPrepared() {
        mRequestHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == MESSAGE_DOWNLOAD) {
                    T target = (T) msg.obj;
                    Log.i(TAG, "Got a request for URL: " + mRequestMap.get(target));
                    handleRequest(target);
                }else  if(msg.what == MESSAGE_PRELOAD) {
                    String url = (String)msg.obj;
                    preload(url);
                }
            }
        };
    }

    /******** for remove message whaen you rotate devie **********/
    public void ClearQueue(){
        mRequestHandler.removeMessages(MESSAGE_DOWNLOAD);
    }
/***** transform all photo bytes to Bitmap ***************/
    private void handleRequest(final T target) {
        try {
            final String url = mRequestMap.get(target);
            if (url == null) {
                return;
            }
            if (mCache.get(url) == null)
                preload(target);
            final Bitmap bitmap = mCache.get(url);
            mResponseHandler.post(new Runnable() {
                @Override
                public void run() {
                   if (mRequestMap.get(target) != url){
                       return;
                   }
                    mRequestMap.remove(target);
                    mThumbnailDownloadListener.onThumbnailDownloaded(target,bitmap);
                }
            });
        }catch (Exception e){
            Log.e(TAG, "Error downloading image", e);
        }
    }


    public void queuThumbnail(T target, String url) {
        Log.v(TAG,"Got A Url  "+url);
        if (url==null){
            mRequestMap.remove(url);
        }else{
            mRequestMap.put(target,url);
            mRequestHandler.obtainMessage(MESSAGE_DOWNLOAD,target).sendToTarget();
        }
    }
}
