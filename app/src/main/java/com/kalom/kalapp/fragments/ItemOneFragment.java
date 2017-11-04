package com.kalom.kalapp.fragments;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;


import com.kalom.kalapp.R;
import com.kalom.kalapp.classes.Duyuru;
import com.kalom.kalapp.classes.DuyuruAdapter;
import com.kalom.kalapp.classes.JSONParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ItemOneFragment extends Fragment {

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

        DuyuruInfo us=new DuyuruInfo();
        JSONArray ar ;
        try {

           ar =new JSONArray(us.execute().get());


        } catch (InterruptedException | ExecutionException | JSONException e) {
            e.printStackTrace();
            return null;
        }


        for(int i=0;i<ar.length();++i){
            try {
                JSONObject obj=(JSONObject) ar.get(i);
                duyurular.add(new Duyuru(
                        obj.get("baslik").toString(),
                        obj.get("prebaslik").toString(),
                        obj.get("content").toString(),
                        "http://10.0.2.2/logo.jpg"
                ));


            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }

        }

        final ListView listemiz=rootView.findViewById(R.id.listView1);

        final DuyuruAdapter adapter=new DuyuruAdapter(getActivity(),duyurular);

        listemiz.setAdapter(adapter);

        listemiz.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE
                        && (listemiz.getLastVisiblePosition() - listemiz.getHeaderViewsCount() -
                        listemiz.getFooterViewsCount()) >= (adapter.getCount() - 1)) {

                    duyurular.add(new Duyuru(
                            "asdasdsa",
                            "asdasda",
                            "asdasdasd",
                            "http://10.0.2.2/logo.jpg"
                    ));

                    System.out.println("asdasdasd");
                    //duyurular.clear();
                    duyurular.add(new Duyuru(
                            "asdasdsa",
                            "asdasda",
                            "asdasdasd",
                            "http://10.0.2.2/logo.jpg"
                    ));

                    adapter.notifyDataSetChanged();


                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });


        return rootView;




    }



    private class DuyuruInfo extends AsyncTask<Void, String,String> {

        private String hash;

        DuyuruInfo() {
        }

        @Override
        protected String doInBackground(Void... params) {


            // Simulate network access.
            JSONParser js=new JSONParser();

            try{

                String api_call="http://10.0.2.2/include/duyuru.php?s=0&f=10";

                return js.JsonString(api_call);

            }catch(IOException | JSONException e){
                e.getMessage();
            }

            return null;
        }


    }

}