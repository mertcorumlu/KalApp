package com.kalom.kalapp.classes;

import android.app.Activity;
import android.content.ContentProvider;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kalom.kalapp.AnketActivity;
import com.kalom.kalapp.Login_Activity;
import com.kalom.kalapp.MainActivity;
import com.kalom.kalapp.MainPage;
import com.kalom.kalapp.R;

import java.util.List;

import static android.support.v4.app.ActivityCompat.startActivityForResult;
import static android.support.v4.content.ContextCompat.startActivity;


public class AnketAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private List<Anket> mAnketler;
    private Context mCon;
    private boolean isopened;

    public AnketAdapter(Activity activity, List<Anket> anketler, Context con){

        mCon=con;

        mInflater=(LayoutInflater) activity.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);

        mAnketler =anketler;

    }

    @Override
    public int getCount() {
        return mAnketler.size();
    }

    @Override
    public Anket getItem(int i) {
        return mAnketler.get(i);
    }



    @Override
    public long getItemId(int i) {
        return i;
    }




    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View satir;

        satir=mInflater.inflate(R.layout.anketlist_layout, null);

        Anket duy= mAnketler.get(i);


        TextView baslik= satir.findViewById(R.id.title);
        baslik.setText(duy.getBaslik());

        TextView prebaslik= satir.findViewById(R.id.yazar);
        prebaslik.setText(duy.getYazar());

        RelativeLayout rel=satir.findViewById(R.id.relativeLayout);
        rel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(mCon, AnketActivity.class);
                mCon.startActivity(intent);
            }
        });

        /*
        final TextView icerik= satir.findViewById(R.id.content);
        final String str_icerik=duy.getIcerik();
        icerik.setText(str_icerik);*/
        return satir;
    }


}


