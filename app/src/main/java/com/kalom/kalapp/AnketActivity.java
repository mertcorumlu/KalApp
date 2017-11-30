package com.kalom.kalapp;

import android.annotation.SuppressLint;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.kalom.kalapp.classes.Config;
import com.kalom.kalapp.classes.JSONParser;
import com.kalom.kalapp.classes.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.ExecutionException;


public class AnketActivity extends AppCompatActivity {

    private JSONObject anket_json;
    private String anket_title;
    private String anket_yazar;
    private JSONArray anket_content;
    private LinearLayout ln;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.anket_layout);

        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);


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


            anket_title=(String) anket_json.get("title");
            anket_yazar=(String) anket_json.get("yazar");
            anket_content=(JSONArray) anket_json.get("content");


            ln = findViewById(R.id.anket_linear);
            addAnketView();

        } catch (InterruptedException | ExecutionException | JSONException e) {
            e.printStackTrace();
        }





    }


    private View addAnketView() throws JSONException{


       for(int i=0;i<anket_content.length();i++){

           JSONObject soru =(JSONObject) anket_content.get(i);

           /*
           TextView İçin Burada View Oluşturuldu.
            */
            TextView soru_text=new TextView(this);
            soru_text.setText((String) soru.get("soru"));
            soru_text.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            soru_text.setTextSize(18);
            soru_text.setTextColor(Color.BLACK);
                ln.addView(soru_text);

               JSONArray options=(JSONArray) soru.get("options");

           if(soru.get("type").equals("radio") ){

               RadioGroup rg=new RadioGroup(this);
               rg.setId(i);


                   for (int j=0;j<options.length();j++){

                       JSONObject opt_cont = (JSONObject) options.get(j);

                       RadioButton rb = new RadioButton(this);
                       rb.setText((String) opt_cont.get("opt_content"));
                       //rb.setid

                       rg.addView(rb);

                   }

                   ln.addView(rg);

           }




       }
    return null;

    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
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
