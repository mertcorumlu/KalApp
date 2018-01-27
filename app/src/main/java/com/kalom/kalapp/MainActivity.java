package com.kalom.kalapp;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;

import com.kalom.kalapp.classes.Config;
import com.kalom.kalapp.fragments.DuyuruFragment;
import com.koushikdutta.ion.Ion;

import android.view.View;
import android.widget.ImageView;

import org.json.JSONException;
import org.json.JSONObject;



public  class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private JSONObject userInfo;
    private ImageView profileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userInfo = Config.check_login(this);

        //API 19 İçin Vector Background Eklentisi
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        setContentView(R.layout.mainactivity_layout);

        ImageView anketImage = findViewById(R.id.anket_image);
        profileImage = findViewById(R.id.profile_img);

        load_profile_img();

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
