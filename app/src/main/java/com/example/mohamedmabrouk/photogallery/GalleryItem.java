package com.example.mohamedmabrouk.photogallery;

import android.net.Uri;

/**
 * Created by Mohamed Mabrouk on 21/08/2016.
 */
public class GalleryItem {
private String mCaption;
    private String mUrl;
    private String mId;
    private String mOwner;

    public String getOwner() {
        return mOwner;
    }

    public void setOwner(String mOwner) {
        this.mOwner = mOwner;
    }

    public String getUrl() {
        return mUrl;

    }

    public Uri getPageUri(){
        return Uri.parse("http://www.flickr.com/photos/")
                .buildUpon()
                .appendPath(mOwner)
                .appendPath(mId)
                .build();
    }

    public void setUrl(String mUrl) {
        this.mUrl = mUrl;
    }

    public String getCaption() {
        return mCaption;
    }

    public void setCaption(String mCaption) {
        this.mCaption = mCaption;
    }

    public String getId() {
        return mId;
    }

    public void setId(String mId) {
        this.mId = mId;
    }

    @Override
    public String toString() {
        return mCaption;
    }
}
