package com.kalom.kalapp;


import android.annotation.SuppressLint;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.Toast;

import com.kalom.kalapp.classes.Config;
import com.kalom.kalapp.classes.SessionManager;

import org.greenrobot.eventbus.EventBus;


public class AnketWebviewActivity extends AppCompatActivity {


    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.anket_webview_layout);


       android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        Bundle extras=getIntent().getExtras();
        int anketID = extras.getInt("anket_id");



        WebView webview = findViewById(R.id.anket_webview);
        WebSettings settings = webview.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        settings.setDomStorageEnabled(true);
        webview.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        SessionManager session = new SessionManager(getApplicationContext());

        webview.loadUrl(Config.api_server+"?action=anket&hash="+session.getToken()+"&do=anket_getir&id="+ anketID);

            set_loader();

        webview.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                hide_loader();
            }

            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(getApplicationContext(), "Bir hata oluştu", Toast.LENGTH_SHORT).show();
                hide_loader();
            }
        });


    }



    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        EventBus.getDefault().post("ANKETTEN_GERI_DONULDU");
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void set_loader(){
        AnimationDrawable animationDrawable;
        ImageView mProgressBar=findViewById(R.id.login_progress);
        mProgressBar.setBackgroundResource(R.drawable.loader);
        animationDrawable = (AnimationDrawable)mProgressBar.getBackground();
        animationDrawable.start();

        mProgressBar=null;
    }

    public void hide_loader(){
        ImageView img= findViewById(R.id.login_progress);
        img.setVisibility(View.GONE);
        img=null;
    }



}
