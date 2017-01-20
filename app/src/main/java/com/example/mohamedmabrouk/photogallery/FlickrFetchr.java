package com.example.mohamedmabrouk.photogallery;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mohamed Mabrouk on 21/08/2016.
 */
public class FlickrFetchr {
    private static final String TAG = "FlickrFetchr";
    private static final String API_KEY = "a3a200228dd4120b9bdefb9a9af13fae";
    private static final String FETCH_RECENTS_METHOD = "flickr.photos.getRecent";
    private static final String SEARCH_METHOD = "flickr.photos.search";
    private static final Uri ENDPOINT = Uri
            .parse("https://api.flickr.com/services/rest/")
            .buildUpon()
            .appendQueryParameter("api_key", API_KEY)
            .appendQueryParameter("format", "json")
            .appendQueryParameter("nojsoncallback", "1")
            .appendQueryParameter("extras", "url_s")
            .build();
 /********** for  method fetches raw data from a URL and
  returns it as an array of bytes
  pramter is a url for photo
  ********/
    public  static  byte[] getUrlBytes(String urlSpec) throws IOException {
        URL url = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException(connection.getResponseMessage() +
                        ": with " +
                        urlSpec);
            }
            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while ((bytesRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);
            }

            in.close();
            return out.toByteArray();
        } catch (Exception e) {
return null;

        } finally {
            connection.disconnect();
        }

    }

    /******** for get url **********/

    public String bildUrl(String method,String Query){
        Uri.Builder uribuilder=ENDPOINT.buildUpon().appendQueryParameter("method",method);

        if (method.equals(SEARCH_METHOD)){
          uribuilder.appendQueryParameter("text",Query);
        }
        return uribuilder.build().toString();
    }

    /********* to fetch public items  **********/
      public List<GalleryItem>  FecthRecentPhotos(){
          String url=bildUrl(FETCH_RECENTS_METHOD, null);
          return downloadGalleryItems(url);
      }

    /**** to fetch  searched items *******/
    public List<GalleryItem> SearchItems(String query){
        String url=bildUrl(SEARCH_METHOD,query);
        return downloadGalleryItems(url);
    }

    /******** for transform arry of bytes to String ********/
    public String getUrlString(String urlSepc) throws IOException {
return new String(getUrlBytes(urlSepc));
    }

    /** for fetch url with data for each photo from site *********/
    public List<GalleryItem> downloadGalleryItems(String url) {
        List<GalleryItem> galleryItems=new ArrayList<>();
        try {

            String jsonString = getUrlString(url);
            Log.i(TAG, "Received JSON: " + jsonString);
            JSONObject jsonbody= new JSONObject(jsonString);
            parseItems(galleryItems,jsonbody);
        } catch (JSONException je){
            Log.e(TAG, "Failed to parse JSON", je);
        } catch (IOException ioe) {
            Log.e(TAG, "Failed to fetch items", ioe);
        }
        return galleryItems;
    }
    /********* for pull information from photo  *******/
    private void parseItems(List<GalleryItem> items, JSONObject jsonBody)
            throws IOException, JSONException {
        JSONObject photosJsonObject = jsonBody.getJSONObject("photos");
        JSONArray photoJsonArray = photosJsonObject.getJSONArray("photo");
        for (int i = 0; i < photoJsonArray.length(); i++) {
            JSONObject photoJsonObject = photoJsonArray.getJSONObject(i);
            GalleryItem item = new GalleryItem();
            item.setId(photoJsonObject.getString("id"));
            item.setCaption(photoJsonObject.getString("title"));
            if (!photoJsonObject.has("url_s")) {
                continue;
            }
            item.setUrl(photoJsonObject.getString("url_s"));
            item.setOwner(photoJsonObject.getString("owner"));
            items.add(item);
        }
    }
}
