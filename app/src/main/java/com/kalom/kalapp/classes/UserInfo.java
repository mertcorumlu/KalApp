package com.kalom.kalapp.classes;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;


public class UserInfo extends AsyncTask<Void, String,JSONObject> {
    /**
     * Bu class sunucudan verileri çeker.Async Task olarak kodlanmıştır.
     * Parametre olarak login hash alması gerekir.
     * bağlantı kuramazsa null döndürür
     */

    private final String hash;

    public UserInfo(String token) {
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

