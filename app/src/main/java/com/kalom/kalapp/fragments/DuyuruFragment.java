package com.kalom.kalapp.fragments;


import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.kalom.kalapp.R;
import com.kalom.kalapp.classes.Config;
import com.kalom.kalapp.classes.Duyuru;
import com.kalom.kalapp.classes.DuyuruAdapter;
import com.kalom.kalapp.classes.JSONParser;
import com.kalom.kalapp.classes.SessionManager;
import com.kalom.kalapp.classes.OnLoadMoreListener;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;


public class DuyuruFragment extends Fragment {

    private DuyuruAdapter adapter;
    private DuyuruInfo us;
    private RecyclerView listemiz;
    private ImageView list_footer_view;
    private CoordinatorLayout coordinatorLayout;
    private SwipeRefreshLayout swip;
    private FloatingSearchView searchView;
    private String searchQuery;
    private PopupMenu popupMenu;

    private SessionManager session;

    private int str=0;
    private int fnsh=Config.duyuru_load_one_time;
    public boolean refreshed=false;

    final List<Duyuru> duyurular= new ArrayList<>();

    public static DuyuruFragment newInstance() {
        return new DuyuruFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = new SessionManager(getContext());
    }



    @SuppressLint("StaticFieldLeak")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

         final View rootView = inflater.inflate(R.layout.duyurufragment_layout,
                container, false);

        searchView = rootView.findViewById(R.id.floating_search_view);

        CardView card=rootView.findViewById(R.id.search_query_section);
        card.setBackgroundResource(R.drawable.searchbar_radius_background);
        card.setUseCompatPadding(false);

        listemiz=rootView.findViewById(R.id.listView1);
        swip=rootView.findViewById(R.id.swiperefresh);
        ImageView back_button=rootView.findViewById(R.id.left_action);

        listemiz.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter=new DuyuruAdapter(duyurular,listemiz);
        listemiz.setAdapter(adapter);

        us=new DuyuruInfo();
        us.execute();

        adapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {

                listemiz.post(new Runnable() {
                    @Override
                    public void run() {

                       us=new DuyuruInfo();
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
                            searchView.clearSearchFocus();
                            searchView.setSearchText("");
                            swip.setRefreshing(false);
                        }
                    }
            );


        searchView.setOnSearchListener(new FloatingSearchView.OnSearchListener() {

            @Override
            public void onSuggestionClicked(SearchSuggestion searchSuggestion) {

            }

            @Override
            public void onSearchAction(String query) {

                if(!query.isEmpty()){

                    searchQuery = query;
                    duyurular.clear();
                    adapter.notifyDataSetChanged();

                    DuyuruSearch duyuruSearch= new DuyuruSearch();
                    str=0;
                    fnsh=Config.duyuru_load_one_time;
                    duyuruSearch.execute();

                    adapter.setOnLoadMoreListener(new OnLoadMoreListener() {
                        @Override
                        public void onLoadMore() {

                            listemiz.post(new Runnable() {
                                @Override
                                public void run() {

                                    DuyuruSearch duyuruSearch= new DuyuruSearch();
                                    duyuruSearch.execute();

                                }
                            });
                        }
                    });

                }else{
                    normale_don();
                }


            }
        });


        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.clearFocus();
                searchView.setSearchText("");
                normale_don();
            }
        });

        listemiz.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if(!adapter.isLoading){
                   searchView.clearSearchFocus();
                }
            }
        });


        popupMenu = new PopupMenu(getActivity(),rootView.findViewById(R.id.menu_view));
        popupMenu.getMenu().add("Tümü");

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

                    Toast.makeText(getContext(),"INTERNET YOK",Toast.LENGTH_LONG).show();

                    return;
                }

                try {


                    JSONArray ar = new JSONArray(result);

                    if(ar.length()!=0){



                        for(int i = 0; i< ar.length(); ++i){
                            try {
                                JSONObject obj = ar.getJSONObject(i);
                                popupMenu.getMenu().add(obj.get("yazar").toString());

                            } catch (JSONException e) {

                                e.printStackTrace();

                                return;
                            }

                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }



            }


        }.execute();


        searchView.setOnMenuItemClickListener(new FloatingSearchView.OnMenuItemClickListener() {
            @Override
            public void onActionMenuItemSelected(MenuItem item) {

                switch(item.getItemId()){

                    case R.id.menu_search_filter:

                        popupMenu.show();

                        break;
                }

            }
        });

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getTitle().toString().equals("Tümü")){
                    normale_don();
                    return false;
                }
                searchQuery = item.getTitle().toString();
                duyurular.clear();
                adapter.notifyDataSetChanged();

                DuyuruSearch duyuruSearch= new DuyuruSearch();
                str=0;
                fnsh=Config.duyuru_load_one_time;
                duyuruSearch.execute();

                adapter.setOnLoadMoreListener(new OnLoadMoreListener() {
                    @Override
                    public void onLoadMore() {

                        listemiz.post(new Runnable() {
                            @Override
                            public void run() {

                                DuyuruSearch duyuruSearch= new DuyuruSearch();
                                duyuruSearch.execute();

                            }
                        });
                    }
                });
                return false;
            }
        });





        return rootView;

    }

    public void refresh(){
        duyurular.clear();
        adapter.notifyDataSetChanged();
        us=new DuyuruInfo();
        str=0;
        fnsh=Config.duyuru_load_one_time;
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

                        us=new DuyuruInfo();
                        us.execute();

                    }
                });
            }
        });

    }


    @SuppressLint("StaticFieldLeak")
    private class DuyuruInfo extends AsyncTask<Void, String,String> {

        @Override
        protected void onPreExecute(){
            duyurular.add(null);
            adapter.notifyItemInserted(duyurular.size()-1);
            adapter.setLoading();
        }

        @Override
        protected String doInBackground(Void... params) {



            // Simulate network access.
            JSONParser js=new JSONParser();

            try{

                String api_call= Config.api_server+"?action=duyuru&hash="+session.getToken()+"&s="+str +"&f="+fnsh;
                str=fnsh;
                fnsh+=Config.duyuru_load_one_time;
                return js.JsonString(api_call);

            }catch(IOException e){
                e.getMessage();
                return null;
            }

        }

        @Override
        protected void onPostExecute(String result){
            duyurular.remove(duyurular.size()-1);

            if(result==null){

                Toast.makeText(getContext(),"INTERNET YOK",Toast.LENGTH_LONG).show();

                return;
            }

            try {


                JSONArray ar = new JSONArray(result);

                if(ar.length()!=0){



                    for(int i = 0; i< ar.length(); ++i){
                        try {
                            JSONObject obj = ar.getJSONObject(i);
                            duyurular.add(new Duyuru(
                                    Integer.parseInt(obj.get("id").toString()),
                                    obj.get("yazar").toString(),
                                    obj.get("title").toString(),
                                    obj.get("content").toString(),
                                    obj.get("img_url").toString(),
                                    obj.get("content_img").toString(),
                                    obj.get("date").toString()
                            ));


                        } catch (JSONException e) {

                            e.printStackTrace();

                            return;
                        }

                    }



                    adapter.setLoaded();
                }

                adapter.notifyDataSetChanged();


            } catch (JSONException e) {
                e.printStackTrace();
            }



        }

    }

    @SuppressLint("StaticFieldLeak")
    private class DuyuruSearch extends AsyncTask<Void, String,String> {

        @Override
        protected void onPreExecute(){
            searchView.showProgress();
            duyurular.add(null);
            adapter.notifyItemInserted(duyurular.size()-1);
            adapter.setLoading();
        }

        @Override
        protected String doInBackground(Void... params) {



            // Simulate network access.
            JSONParser js=new JSONParser();

            try{

                String api_call= Config.api_server+"?action=search&do=duyuru&hash="+session.getToken()+"&s="+str +"&f="+fnsh+"&q="+ URLEncoder.encode(searchQuery, "UTF-8");;
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

            searchView.hideProgress();
            duyurular.remove(duyurular.size()-1);

            if(result==null){

                Toast.makeText(getContext(),"INTERNET YOK",Toast.LENGTH_LONG).show();

                return;
            }

            try {


                JSONArray ar = new JSONArray(result);

                if(ar.length()!=0){



                    for(int i = 0; i< ar.length(); ++i){
                        try {
                            JSONObject obj = ar.getJSONObject(i);
                            duyurular.add(new Duyuru(
                                    Integer.parseInt(obj.get("id").toString()),
                                    obj.get("yazar").toString(),
                                    obj.get("title").toString(),
                                    obj.get("content").toString(),
                                    obj.get("img_url").toString(),
                                    obj.get("content_img").toString(),
                                    obj.get("date").toString()
                            ));


                        } catch (JSONException e) {

                            e.printStackTrace();

                            return;
                        }

                    }



                    adapter.setLoaded();
                }

                adapter.notifyDataSetChanged();


            } catch (JSONException e) {
                e.printStackTrace();
            }



        }






    }



}