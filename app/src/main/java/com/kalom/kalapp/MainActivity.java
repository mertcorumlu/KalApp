package com.kalom.kalapp;


import android.annotation.SuppressLint;
import android.content.Context;
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


public  class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private JSONObject userInfo;
    private ImageView profileImage;
    private int ISTEK_YOLLANDI=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //API 19 İçin Vector Background Eklentisi
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        try {
            userInfo = new UserInfo(this).execute().get();
        } catch (Exception e) {
            e.printStackTrace();
        }

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
                startActivity(intentAnket);
                break;

            case R.id.profile_img:
                Intent intentProfil = new Intent(this, ProfilActivity.class);
                intentProfil.putExtra("user_info",userInfo.toString());
                startActivity(intentProfil);
                break;

        }

    }

    @Override
    protected void onResume(){
        super.onResume();

        if(ISTEK_YOLLANDI==1){
            new UserInfo(this).execute();
        }else{
            ISTEK_YOLLANDI=1;
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();


    }


    @SuppressLint("StaticFieldLeak")
    public class UserInfo extends AsyncTask<Void, String,JSONObject> {
        /**
         * Bu class sunucudan verileri çeker.Async Task olarak kodlanmıştır.
         * Parametre olarak login hash alması gerekir.
         * bağlantı kuramazsa null döndürür
         */

        private Context mContext;
        private String hash;
        private SessionManager session;

        public UserInfo(Context mContext){

            this.mContext = mContext;

        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            Log.d("TEST","ISTEK GONDERILDI");

            //Shared Preferences İçinde Tutulacak Olan Login Hash İçin Kütüphane
            session = new SessionManager(mContext);

        /*
         Eğer Login Hash Kayıtlı Değilse Daha Önce Hiç Giriş Yapılmamıştır.
          Bu Durumda Login Ekranına Yönlendir.
          ------------------------------------------------------------------
         */
            if(session.getToken()==null){
                this.cancel(true);
                Intent intent = new Intent(mContext, Login_Activity.class);
                startActivity(intent);
                finish();
            }

            hash = session.getToken();


        }

        @Override
        protected JSONObject doInBackground(Void... params) {

            // yeni bir json parser değişkeni
            JSONParser js=new JSONParser();

            try{
                //login kontrol etmek için sunucuya yapılması gereken istek
                String api_call= Config.api_server+"?action=user_info&hash="+hash;

                //JSONParser kütüphanesi ile sunucuya istek yollanır.
                //Yanıt olarak JSONObject döner
                return js.readJson(api_call);

            }catch(IOException | JSONException e){
                /*
                 Eğer Sunucuya Ulaşılamadıysa, Bir Alert Dialog Oluştur ve Kullanıcıyı Bilgilendir.
                */

                e.printStackTrace();


                    AlertDialog.Builder builder1 = new AlertDialog.Builder(mContext);
                    builder1.setMessage("Sunuculara Erişilemiyor.Lütfen Bağlantınızı Kontrol Edip Tekrar Deneyiniz.");
                    builder1.setCancelable(true);

                    builder1.setPositiveButton(
                            "Tamam",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    finish();
                                    System.exit(0);
                                }
                            });



                    AlertDialog alert11 = builder1.create();
                    alert11.show();


                return null;

            }
        }

        @Override
        protected void onPostExecute(JSONObject info) {
            super.onPostExecute(info);

            Log.d("TEST","CEVAP GELDI");


                /*
                 Suncudan Gelen Verileri Kontrol Et.
                 Eğer Sunucu Login Hashi Onaylamışsa Ana Ekrana Yönlendir.
                  Eğer Login Hashin Süresi Dolmuşsa Oturumu Zaman Aşımına Uğrat.Logine Yönlendir.
                 */
            try {
                if(!info.get("valid").equals(true)){
                    session.editor.clear().commit();


                    AlertDialog.Builder builder1 = new AlertDialog.Builder(mContext);
                    builder1.setMessage("Başka Bir Cihazdan Giriş Yaptığın İçin Bu Oturumuna Kısa Bir Ara Veriyoruz :)");
                    builder1.setCancelable(true);

                    builder1.setPositiveButton(
                            "Tamam",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Intent intent = new Intent(mContext, Login_Activity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            });



                    AlertDialog alert11 = builder1.create();
                    alert11.show();

                }else{

                    //UserInfo Kullanan Diğer Fonksiyonları Burada Çağır.
                    userInfo = info;

                    load_profile_img();


                }


            } catch (JSONException e) {
                e.printStackTrace();
            }



        }



    }

}

