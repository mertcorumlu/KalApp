package com.kalom.kalapp;



import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.widget.SwipeRefreshLayout;
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
import com.kalom.kalapp.fragments.DuyuruFragment;

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
    private PopupMenu popupMenu;
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
            adapter=new AnketAdapter(anketler, listemiz,getApplicationContext());
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

                popupMenu = new PopupMenu(this,this.findViewById(R.id.filter));
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


    @RequiresApi(api = Build.VERSION_CODES.ECLAIR)
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
        finish();
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




}