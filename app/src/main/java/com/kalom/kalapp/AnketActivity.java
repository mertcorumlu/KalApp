package com.kalom.kalapp;



import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.kalom.kalapp.classes.Anket;
import com.kalom.kalapp.classes.AnketAdapter;
import com.kalom.kalapp.classes.Config;
import com.kalom.kalapp.classes.JSONParser;
import com.kalom.kalapp.classes.OnLoadMoreListener;
import com.kalom.kalapp.classes.SessionManager;
import com.koushikdutta.ion.Ion;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;


public class AnketActivity extends AppCompatActivity {

    private AnketAdapter adapter=null;
    private AnketInfo us;
    private RecyclerView listemiz;
    private SwipeRefreshLayout swip;
    private JSONArray yazarlar;
    private String searchQuery;

    private SessionManager session;

    private int str=0;
    private int fnsh=Config.anket_load_one_time;

    final List<Anket> anketler = new ArrayList<>();

    @SuppressLint("StaticFieldLeak")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.anket_layout);
        session = new SessionManager(getApplicationContext());


        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);

        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Anketler");

        if(!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this);
        }


            listemiz=findViewById(R.id.listView1);
            swip=findViewById(R.id.swiperefresh);




            listemiz.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            adapter=new AnketAdapter(anketler, listemiz);
            listemiz.setAdapter(adapter);



            us=new AnketInfo();
            us.execute();



        adapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {

                listemiz.post(new Runnable() {
                    @Override
                    public void run() {

                        us=new AnketInfo();
                        us.execute();

                    }
                });

            }
        });



            swip.setOnRefreshListener(
                    new SwipeRefreshLayout.OnRefreshListener() {
                        @Override
                        public void onRefresh() {
                            normale_don();
                            swip.setRefreshing(false);

                        }
                    }
            );




        new AsyncTask<Void,String,String>() {

            @Override
            protected String doInBackground(Void... params) {
                // Simulate network access.
                JSONParser js=new JSONParser();

                try{

                    String api_call= Config.api_server+"?action=yazarlar&hash="+session.getToken();
                    return js.JsonString(api_call);

                }catch(IOException e){
                    e.getMessage();
                    return null;
                }

            }

            @Override
            protected void onPostExecute(String result){

                if(result==null){

                    Toast.makeText(getApplicationContext(),"INTERNET YOK",Toast.LENGTH_LONG).show();
                        yazarlar=null;
                    return;
                }

                try {


                    yazarlar = new JSONArray(result);



                } catch (JSONException e) {
                    e.printStackTrace();
                }



            }


        }.execute();



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_anket, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.filter:

                PopupMenu popupMenu = new PopupMenu(this, this.findViewById(R.id.filter));
                popupMenu.getMenu().add("Tümü");


                if(yazarlar.length()!=0){
                    for(int i = 0; i< yazarlar.length(); ++i){
                        try {
                            JSONObject obj = yazarlar.getJSONObject(i);
                            popupMenu.getMenu().add(obj.get("yazar").toString());

                        } catch (JSONException e) {
                            e.printStackTrace();

                        }
                    }
                }

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if(item.getTitle().toString().equals("Tümü")){
                            normale_don();
                            return false;
                        }
                        searchQuery = item.getTitle().toString();
                        anketler.clear();
                        adapter.notifyDataSetChanged();

                        AnketSearch duyuruSearch= new AnketSearch();
                        str=0;
                        fnsh=Config.duyuru_load_one_time;
                        duyuruSearch.execute();

                        adapter.setOnLoadMoreListener(new OnLoadMoreListener() {
                            @Override
                            public void onLoadMore() {

                                listemiz.post(new Runnable() {
                                    @Override
                                    public void run() {

                                        AnketSearch duyuruSearch= new AnketSearch();
                                        duyuruSearch.execute();

                                    }
                                });
                            }
                        });
                        return false;
                    }
                });

                   popupMenu.show();


                break;

            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return true;
    }

    public void refresh(){
        anketler.clear();
        adapter.notifyDataSetChanged();
        us=new AnketInfo();
        str=0;
        fnsh=Config.anket_load_one_time;
        us.execute();
    }

    public void normale_don(){
        refresh();
        adapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {

                listemiz.post(new Runnable() {
                    @Override
                    public void run() {

                        us=new AnketInfo();
                        us.execute();

                    }
                });
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();

        new UserInfo(this).execute();
    }

    @Subscribe
    public void onEvent(String event) {
        if(event.equals("ANKETTEN_GERI_DONULDU")){
                refresh();
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class AnketInfo extends AsyncTask<Void, String,String> {

        @Override
        protected void onPreExecute(){

            anketler.add(null);
            adapter.notifyItemInserted(anketler.size()-1);
            adapter.setLoading();

        }

        @Override
        protected String doInBackground(Void... params) {


            // Simulate network access.
            JSONParser js=new JSONParser();

            try{
                String api_call= Config.api_server+"?action=anket&do=anketleri_getir&hash="+session.getToken()+"&s="+ str +"&f="+fnsh;
                str=fnsh;
                fnsh+=Config.anket_load_one_time;
                return js.JsonString(api_call);



            }catch(IOException e){
                e.getMessage();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result){
            anketler.remove(anketler.size()-1);


            if(result==null){

                Toast.makeText(getApplicationContext(),"INTERNET YOK",Toast.LENGTH_LONG).show();

                return;
            }

            try {

                JSONArray ar = new JSONArray(result);

                if(ar.length()!=0) {
                    for (int i = 0; i < ar.length(); ++i) {
                        try {
                            JSONObject obj = (JSONObject) ar.get(i);
                            anketler.add(new Anket(
                                    obj.get("yazar").toString(),
                                    obj.get("title").toString(),
                                    obj.get("img_url").toString(),
                                    Integer.parseInt(obj.get("id").toString()),
                                    (Integer.parseInt(obj.get("voted").toString()) > 0)
                            ));


                        } catch (JSONException e) {
                            e.printStackTrace();

                            return;
                        }

                    }

                    adapter.setLoaded();;

                }

                adapter.notifyDataSetChanged();


            } catch (JSONException e) {
                e.printStackTrace();
            }



        }


    }

    @SuppressLint("StaticFieldLeak")
    private class AnketSearch extends AsyncTask<Void, String,String> {

        @Override
        protected void onPreExecute(){
            anketler.add(null);
            adapter.notifyItemInserted(anketler.size()-1);
            adapter.setLoading();
        }

        @Override
        protected String doInBackground(Void... params) {



            // Simulate network access.
            JSONParser js=new JSONParser();

            try{

                String api_call= Config.api_server+"?action=search&do=anket&hash="+session.getToken()+"&s="+str +"&f="+fnsh+"&q="+ URLEncoder.encode(searchQuery, "UTF-8");;
                str=fnsh;
                fnsh+=Config.duyuru_load_one_time;
                Log.d("API",api_call);
                return js.JsonString(api_call);

            }catch(IOException e){
                e.getMessage();
                return null;
            }

        }

        @Override
        protected void onPostExecute(String result){

            anketler.remove(anketler.size()-1);

            if(result==null){

                Toast.makeText(getApplicationContext(),"INTERNET YOK",Toast.LENGTH_LONG).show();

                return;
            }

            try {


                JSONArray ar = new JSONArray(result);

                if(ar.length()!=0) {

                    for (int i = 0; i < ar.length(); ++i) {
                        try {
                            JSONObject obj = (JSONObject) ar.get(i);
                            anketler.add(new Anket(
                                    obj.get("yazar").toString(),
                                    obj.get("title").toString(),
                                    obj.get("img_url").toString(),
                                    Integer.parseInt(obj.get("id").toString()),
                                    (Integer.parseInt(obj.get("voted").toString()) > 0)
                            ));


                        } catch (JSONException e) {
                            e.printStackTrace();

                            return;
                        }

                    }

                    adapter.setLoaded();;

                }

                adapter.notifyDataSetChanged();


            } catch (JSONException e) {
                e.printStackTrace();
            }



        }






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




                }


            } catch (JSONException e) {
                e.printStackTrace();
            }



        }



    }




}