package com.kalom.kalapp.classes;

import android.app.Activity;
import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kalom.kalapp.R;
import com.koushikdutta.ion.Ion;

import java.util.List;


public class DuyuruAdapter extends BaseAdapter {

    private final LayoutInflater mInflater;
    private final List<Duyuru> mDuyurular;
    private boolean isopened;

    public DuyuruAdapter(Activity activity,List<Duyuru> duyurular){

        mInflater=(LayoutInflater) activity.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);

        mDuyurular=duyurular;

    }

    @Override
    public int getCount() {
        return mDuyurular.size();
    }

    @Override
    public Duyuru getItem(int i) {
        return mDuyurular.get(i);
    }



    @Override
    public long getItemId(int i) {
        return i;
    }




    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View satir;

        satir=mInflater.inflate(R.layout.duyurulist_layout, null);

        Duyuru duy=mDuyurular.get(i);


        TextView baslik= satir.findViewById(R.id.title);
        baslik.setText(duy.getBaslik());

        TextView prebaslik= satir.findViewById(R.id.yazar);
        prebaslik.setText(duy.getYazar());

        final TextView icerik= satir.findViewById(R.id.content);
        final String str_icerik=duy.getIcerik();

        /*

        if(str_icerik.length()>Config.duyuru_max_uzunluk){
            SpannableString ss = new SpannableString(str_icerik.substring(0,Config.duyuru_max_uzunluk) + ".... Devamını Oku");
            ClickableSpan span1 = new ClickableSpan() {
                @Override
                public void onClick(View content) {
                    // do some thing
                    icerik.setText(str_icerik);
                }
            };


            ss.setSpan(span1, Config.duyuru_max_uzunluk+4, Config.duyuru_max_uzunluk+17, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            icerik.setText(ss);
            icerik.setMovementMethod(LinkMovementMethod.getInstance());
           // icerik.setText(str_icerik.substring(0,10) + "....");
        }else{
            icerik.setText(str_icerik);
        }*/

        icerik.setText(str_icerik);


        ImageView imageView = satir.findViewById(R.id.list_image);

        Ion.with(imageView)
                .load(duy.getImg());

        return satir;
    }


}


