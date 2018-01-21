package com.kalom.kalapp;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.google.firebase.iid.FirebaseInstanceId;
import com.kalom.kalapp.classes.Config;
import com.kalom.kalapp.classes.JSONParser;
import com.kalom.kalapp.classes.SessionManager;
import com.kalom.kalapp.fragments.*;

import android.util.Log;
import android.view.MenuItem;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public  class MainPage extends AppCompatActivity {

    private Fragment frag1;
    private Fragment frag2;
    public BottomNavigationView bottomNavigationView;

    @Subscribe
    public void onEvent(String d) {
        Log.d("MESAJ","BOTTOM ŞEYSİ DEĞİŞTİ");

        switch(d){

                case "YENİ DUYURU VAR" :

                    bottomNavigationView.post(new Runnable() {
                        @Override
                        public void run() {
                            bottomNavigationView.getMenu().findItem(R.id.action_item1).setIcon(R.mipmap.marti);
                            ((DuyuruFragment) frag1).refreshed=true;
                        }
                    });

                    break;

        }



    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainpage_layout);

       EventBus.getDefault().register(this);

       Log.d("FIREE", FirebaseInstanceId.getInstance().getToken());


        bottomNavigationView = findViewById(R.id.navigation);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);

        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {

                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        Fragment selectedFragment = null;


                        switch (item.getItemId()) {

                            case R.id.action_item1:
                                if(frag1!=null){

                                    selectedFragment = frag1;
                                    bottomNavigationView.getMenu().findItem(R.id.action_item1).setIcon(R.drawable.ic_account_box_black_24dp);
                                    ((DuyuruFragment) selectedFragment).scrolltoTop();

                                }else{
                                    frag1= DuyuruFragment.newInstance();
                                    selectedFragment = frag1;
                                }
                                break;


                            case R.id.action_item2:
                                if(frag2!=null){
                                    selectedFragment = frag2;
                                    ((AnketFragment) selectedFragment).scrolltoTop();

                                }else{
                                    frag2= AnketFragment.newInstance();
                                    selectedFragment = frag2;

                                }
                                break;


                            case R.id.action_item3:
                                selectedFragment = DuyuruFragment.newInstance();
                                break;


                        }


                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.frame_layout, selectedFragment);
                        transaction.commit();

                        check_login();
                        return true;

                    }
                });

        //Manually displaying the first fragment - one time only

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        frag1= DuyuruFragment.newInstance();
        //frag2=AnketFragment.newInstance();
        transaction.replace(R.id.frame_layout,frag1);
        transaction.commit();

        //Used to select an item programmatically
        //bottomNavigationView.getMenu().getItem(2).setChecked(true);
    }

    @Override
    protected void onResume(){
        super.onResume();

        Log.d("MESAJ","Uygulama Yeniden Başlamış");

        check_login();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // sistemden çıkma
        EventBus.getDefault().unregister(this);
    }

    private void check_login(){
        //Shared Preferences İçinde Tutulacak Olan Login Hash İçin Kütüphane
        SessionManager session = new SessionManager(getApplicationContext());

        try{

            //Login Hash İle Sunucuya Sorgu Yolla.
            UserInfo us=new UserInfo(session.getToken());

            //Suncudan Gelen Cevabı JSONObject Olarak Değişkene Aktar
            JSONObject info = us.execute().get();


            try{

                assert session.getToken()!=null;

                /*
                 Eğer Sunucuya Ulaşılamadıysa, Bir Alert Dialog Oluştur ve Kullanıcıyı Bilgilendir.
                 */
                if(info==null) {

                    AlertDialog.Builder builder1 = new AlertDialog.Builder(MainPage.this);
                    builder1.setMessage("Sunucuya Şu Anda Erişilemiyor.");
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


                    return;
                }


                /*
                 Suncudan Gelen Verileri Kontrol Et.
                 Eğer Sunucu Login Hashi Onaylamışsa Ana Ekrana Yönlendir.
                  Eğer Login Hashin Süresi Dolmuşsa Oturumu Zaman Aşımına Uğrat.Logine Yönlendir.
                 */
                if(!info.get("valid").equals(true)){

                    Log.d("MESAJ","Oturum Sona Ermiş.");
                    session.editor.clear().commit();


                    AlertDialog.Builder builder1 = new AlertDialog.Builder(MainPage.this);
                    builder1.setMessage("Başka Bir Cihazdan Giriş Yaptığın İçin Bu Oturumuna Kısa Bir Ara Veriyoruz :)");
                    builder1.setCancelable(true);

                    builder1.setPositiveButton(
                            "Tamam",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Intent intent = new Intent(MainPage.this, Login_Activity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            });



                    AlertDialog alert11 = builder1.create();
                    alert11.show();

                }


            }catch(JSONException | NullPointerException e){
                e.getMessage();
            }


        }catch (InterruptedException | ExecutionException e){
            /*
             Sunucyla Bağlantı Sağlanamdığını Konsola Yaz
             */
            e.getMessage();
            Log.d("MESAJ","Sunucudan Alınan Bilgiler Alınırken Bir Hata Oluştu..");
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class UserInfo extends AsyncTask<Void, String,JSONObject> {
        /**
         * Bu class sunucudan verileri çeker.Async Task olarak kodlanmıştır.
         * Parametre olarak login hash alması gerekir.
         * bağlantı kuramazsa null döndürür
         */

        private final String hash;

        UserInfo(String token) {
            hash=token;
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

                //eğer sunucuyla bağlantı sağlanamazsa AsyncTask null döndürür.
                e.getMessage();
                Log.d("MESAJ","Kullanıcı Bilgileri Sunucudan Alınamadı.");
                return null;

            }
        }



    }
}
