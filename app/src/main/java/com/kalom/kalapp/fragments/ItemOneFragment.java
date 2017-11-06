package com.kalom.kalapp.fragments;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

import com.kalom.kalapp.MainActivity;
import com.kalom.kalapp.R;
import com.kalom.kalapp.classes.Config;
import com.kalom.kalapp.classes.Duyuru;
import com.kalom.kalapp.classes.DuyuruAdapter;
import com.kalom.kalapp.classes.JSONParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class ItemOneFragment extends Fragment {

    private DuyuruAdapter adapter=null;
    private DuyuruInfo us;
    private ListView listemiz;
    private View loader_view;
    private int loader_ind;
    private int str=0;
    private int fnsh=Config.duyuru_load_one_time;

    final List<Duyuru> duyurular= new ArrayList<>();

    public static  ItemOneFragment newInstance() {
        return new  ItemOneFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

         final View rootView = inflater.inflate(R.layout.fragement_item_one,
                container, false);
          listemiz=rootView.findViewById(R.id.listView1);
          loader_view = ((LayoutInflater) getContext().getSystemService(MainActivity.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.footer_layout, null, false);
          listemiz.setSmoothScrollbarEnabled(true);

        adapter=new DuyuruAdapter(getActivity(),duyurular);
        listemiz.setAdapter(adapter);


        us=new DuyuruInfo();
        us.execute();

       final SwipeRefreshLayout swip=rootView.findViewById(R.id.swiperefresh);

        swip.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        duyurular.clear();
                        adapter.notifyDataSetChanged();

                        us=new DuyuruInfo();
                        str=0;
                        fnsh=Config.duyuru_load_one_time;
                        us.execute();
                        swip.setRefreshing(false);
                    }
                }
        );




        listemiz.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {


                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE
                        && (listemiz.getLastVisiblePosition() - listemiz.getHeaderViewsCount() -
                        listemiz.getFooterViewsCount()) >= (adapter.getCount()-1)) {

                    us=new DuyuruInfo();
                    us.execute();

                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });


        return rootView;

    }

    protected void showloader(){
        listemiz.addFooterView(loader_view);
        listemiz.setSelection(listemiz.getLastVisiblePosition());
        listemiz.setEnabled(false);

    }

    protected void hideloader(){
        listemiz.removeFooterView(loader_view);
        listemiz.setEnabled(true);

    }



    private class DuyuruInfo extends AsyncTask<Void, String,String> {

        @Override
        protected void onPreExecute(){

            showloader();

        }

        @Override
        protected void onPostExecute(String result){

            try {

                JSONArray ar = new JSONArray(result);


                for(int i = 0; i< ar.length(); ++i){
                    try {
                        JSONObject obj = (JSONObject) ar.get(i);
                        duyurular.add(new Duyuru(
                                obj.get("baslik").toString(),
                                obj.get("prebaslik").toString(),
                                obj.get("content").toString(),
                                "http://10.0.2.2/logo.jpg"
                        ));


                    } catch (JSONException e) {
                        e.printStackTrace();
                        return;
                    }

                }

                adapter.notifyDataSetChanged();


            } catch (JSONException e) {
                e.printStackTrace();
            }


            hideloader();
        }


        @Override
        protected String doInBackground(Void... params) {


            // Simulate network access.
            JSONParser js=new JSONParser();

            try{

                String api_call= Config.api_server+"include/duyuru.php?s="+str +"&f="+fnsh;
                str=fnsh;
                fnsh+=Config.duyuru_load_one_time;
                return js.JsonString(api_call);

            }catch(IOException | JSONException e){
                e.getMessage();
            }

            return null;
        }



    }

}