package com.example.mohamedmabrouk.photogallery;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

/**
 * Created by moham on 03/09/2016.
 */
public class PhotoPageFragment extends VisibleFragment {
    private static final String ARG_URI = "photo_page_url";
   private WebView mWebView;
   private Uri mUri;
    private ProgressBar mProgressBar;


    public static PhotoPageFragment newInstance(Uri uri){
        Bundle bundle=new Bundle();
        bundle.putParcelable(ARG_URI, uri);
        PhotoPageFragment  fragment=new PhotoPageFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUri=getArguments().getParcelable(ARG_URI);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_photo_page,container,false);
        mWebView=(WebView)view.findViewById(R.id.fragment_photo_page_wep_view);
        mProgressBar=(ProgressBar)view.findViewById(R.id.fragment_photo_progress_par);
        mProgressBar.setMax(100);
        mWebView.getSettings().setJavaScriptEnabled(true);

        mWebView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
               if (newProgress==100){
                   mProgressBar.setVisibility(View.GONE);
               }else{
                   mProgressBar.setVisibility(View.VISIBLE);
                   mProgressBar.setProgress(newProgress);
               }
            }


            @Override
            public void onReceivedTitle(WebView view, String title) {
                AppCompatActivity activity=(AppCompatActivity) getActivity();
                activity.getSupportActionBar().setTitle("Photo Gallery");

            }
        });
        mWebView.setWebViewClient(new WebViewClient() {
            /******************   determines what will happen when a
             new URL is loaded in the WebView *******************/
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }
        });
        mWebView.loadUrl(String.valueOf(mUri));
        return view;
    }
}
