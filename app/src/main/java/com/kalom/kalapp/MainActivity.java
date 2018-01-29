package com.kalom.kalapp;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;

import com.kalom.kalapp.classes.Config;
import com.kalom.kalapp.classes.JSONParser;
import com.kalom.kalapp.classes.SessionManager;
import com.kalom.kalapp.fragments.DuyuruFragment;
import com.koushikdutta.ion.Ion;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.ExecutionException;


public  class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private JSONObject userInfo;
    private ImageView profileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //API 19 İçin Vector Background Eklentisi
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);


        userInfo = Config.check_login(this);



        setContentView(R.layout.mainactivity_layout);

        ImageView anketImage = findViewById(R.id.anket_image);
        profileImage = findViewById(R.id.profile_img);

        anketImage.setOnClickListener(this);
        profileImage.setOnClickListener(this);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Fragment frag1 = DuyuruFragment.newInstance();
        transaction.replace(R.id.frame_layout, frag1);
        transaction.commit();

    }

    public void load_profile_img(){
        try {
            Ion.with(profileImage)
                    .load(userInfo.get("img_url").toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.anket_image:
                Intent intentAnket = new Intent(this, AnketActivity.class);
                intentAnket.putExtra("user_info",userInfo.toString());
                this.startActivity(intentAnket);
                break;

            case R.id.profile_img:
                Intent intentProfil = new Intent(this, ProfilActivity.class);
                intentProfil.putExtra("user_info",userInfo.toString());
                this.startActivity(intentProfil);
                break;

        }

    }

    @Override
    protected void onResume(){
        super.onResume();
        userInfo = Config.check_login(this);
        load_profile_img();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();


    }

}

