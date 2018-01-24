package com.kalom.kalapp;



import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.kalom.kalapp.classes.Anket;
import com.kalom.kalapp.classes.AnketAdapter;
import com.kalom.kalapp.classes.Config;
import com.kalom.kalapp.classes.JSONParser;
import com.kalom.kalapp.classes.OnLoadMoreListener;
import com.kalom.kalapp.classes.SessionManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class AnketActivity extends AppCompatActivity {

    private AnketAdapter adapter=null;
    private AnketInfo us;
    private RecyclerView listemiz;
    private SwipeRefreshLayout swip;

    private SessionManager session;

    private int str=0;
    private int fnsh=Config.anket_load_one_time;

    final List<Anket> anketler = new ArrayList<>();

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
                            refresh();
                            swip.setRefreshing(false);

                        }
                    }
            );





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


    @RequiresApi(api = Build.VERSION_CODES.ECLAIR)
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
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




}