package com.kalom.kalapp;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;

import com.kalom.kalapp.classes.Config;
import com.kalom.kalapp.fragments.DuyuruFragment;

import android.view.Menu;
import android.view.MenuItem;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;


public  class MainActivity extends AppCompatActivity {

    private JSONObject userInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userInfo = Config.check_login(this);

        //API 19 İçin Vector Background Eklentisi
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        setContentView(R.layout.mainactivity_layout);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Fragment frag1 = DuyuruFragment.newInstance();
        transaction.replace(R.id.frame_layout, frag1);
        transaction.commit();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);

        try {
            if(!userInfo.get("img_url").toString().equals("null")){

                final MenuItem item =menu.findItem(R.id.profil);

                LoadImage load = new LoadImage();
                Bitmap bitmap = load.execute(userInfo.get("img_url").toString()).get();
                RoundedBitmapDrawable dr =
                        RoundedBitmapDrawableFactory.create(getApplicationContext().getResources(), Bitmap.createScaledBitmap(bitmap, 150, 150, false));
                dr.setCircular(true);

                item.setIcon(dr);



            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.anketler:
                Intent intentAnket = new Intent(this, AnketActivity.class);
                this.startActivity(intentAnket);
                break;

            case R.id.profil:
                Intent intentProfil = new Intent(this, ProfilActivity.class);
                this.startActivity(intentProfil);
                break;





        }
        return true;
    }

    @Override
    protected void onResume(){
        super.onResume();
        Config.check_login(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();


    }

    private static class LoadImage  extends AsyncTask<String, String, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap bitmap = null;
            try {
                URL url = new URL(params[0]);
                bitmap = BitmapFactory.decodeStream((InputStream)url.getContent());
            } catch (IOException e) {
               e.printStackTrace();
            }
            return bitmap;
        }

    }
}
