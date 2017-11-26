package com.kalom.kalapp;


import android.annotation.SuppressLint;
import android.content.DialogInterface;


import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.content.Intent;

import android.support.annotation.RequiresApi;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;

import android.util.Log;

import com.kalom.kalapp.classes.Config;
import com.kalom.kalapp.classes.JSONParser;
import com.kalom.kalapp.classes.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.ExecutionException;


public class MainActivity extends AppCompatActivity {


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //API 19 İçin Vector Background Eklentisi
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        //Shared Preferences İçinde Tutulacak Olan Login Hash İçin Kütüphane
        SessionManager session = new SessionManager(getApplicationContext());

        /**
         *Eğer Login Hash Kayıtlı Değilse Daha Önce Hiç Giriş Yapılmamıştır.
         * Bu Durumda Login Ekranına Yönlendir.
         */
            if(session.getToken()==null){
                Log.d("MESAJ","Giriş Yapılmamış. " + session.getToken());

                Intent intent = new Intent(MainActivity.this, Login_Activity.class);
                startActivity(intent);
                finish();
                return;
            }

        try{

            //Login Hash İle Sunucuya Sorgu Yolla.
            UserInfo us=new UserInfo(session.getToken());

            //Suncudan Gelen Cevabı JSONObject Olarak Değişkene Aktar
            JSONObject info = us.execute().get();


            try{

                assert session.getToken()!=null;

                /**
                 *Eğer Sunucuya Ulaşılamadıysa, Bir Alert Dialog Oluştur ve Kullanıcıyı Bilgilendir.
                 */
                if(info==null) {

                    AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
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


                /**
                 *Suncudan Gelen Verileri Kontrol Et.
                 *Eğer Sunucu Login Hashi Onaylamışsa Ana Ekrana Yönlendir.
                 * Eğer Login Hashin Süresi Dolmuşsa Oturumu Zaman Aşımına Uğrat.Logine Yönlendir.
                 */
                if(info.get("valid").equals(true)){

                    Log.d("MESAJ","Giriş Yapılmış.Ana Ekrana Yönlendiriyor");

                    Intent intent = new Intent(MainActivity.this, MainPage.class);
                    intent.putExtra("UserData", info.toString());
                    startActivity(intent);
                    finish();

                }else{
                    Log.d("MESAJ","Oturum Sona Ermiş.");
                    session.editor.clear().commit();


                    AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
                    builder1.setMessage("Başka Bir Cihazdan Giriş Yaptığın İçin Bu Oturumuna Kısa Bir Ara Veriyoruz :)");
                    builder1.setCancelable(true);

                    builder1.setPositiveButton(
                            "Tamam",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Intent intent = new Intent(MainActivity.this, Login_Activity.class);
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
                /**
                 *Sunucyla Bağlantı Sağlanamdığını Konsola Yaz
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

        private String hash;

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





