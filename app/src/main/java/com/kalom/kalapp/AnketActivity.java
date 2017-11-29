package com.kalom.kalapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.kalom.kalapp.classes.Config;
import com.kalom.kalapp.classes.JSONParser;
import com.kalom.kalapp.classes.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.ExecutionException;


public class AnketActivity extends AppCompatActivity {

    private JSONObject anket_json;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.anket_layout);

       android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        SessionManager session = new SessionManager(getApplicationContext());

        AnketInfo ank=new AnketInfo(session.getToken());


        try {

           anket_json= ank.execute().get();

            if (! ((Boolean) anket_json.get("valid")) ) {
                    finish();
                Toast.makeText(getApplicationContext(),"Anket Sunucusuna Bağlanırken Bir Hata Oluştu.",Toast.LENGTH_LONG).show();
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }





    }



    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    @SuppressLint("StaticFieldLeak")
    private class AnketInfo extends AsyncTask<Void, String,JSONObject> {
        /**
         * Bu class sunucudan verileri çeker.Async Task olarak kodlanmıştır.
         * Parametre olarak login hash alması gerekir.
         * bağlantı kuramazsa null döndürür
         */

        private String hash;


        AnketInfo(String token) {
            hash=token;
        }

        @Override
        protected JSONObject doInBackground(Void... params) {

            // yeni bir json parser değişkeni
            JSONParser js=new JSONParser();

            try{

                //login kontrol etmek için sunucuya yapılması gereken istek
                String api_call= Config.api_server+"?action=anket&do=anket_getir&id=1&hash="+hash;

                //JSONParser kütüphanesi ile sunucuya istek yollanır.
                //Yanıt olarak JSONObject döner
                return js.readJson(api_call);

            }catch(IOException | JSONException e){

                //eğer sunucuyla bağlantı sağlanamazsa AsyncTask null döndürür.
                e.getMessage();
                Log.d("MESAJ","Anket Bilgileri Sunucudan Alınamadı.");
                return null;

            }
        }



    }

}
