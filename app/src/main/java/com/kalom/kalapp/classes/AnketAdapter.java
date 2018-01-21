package com.kalom.kalapp.classes;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kalom.kalapp.AnketActivity;
import com.kalom.kalapp.R;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.List;


public class AnketAdapter extends BaseAdapter {

    private final LayoutInflater mInflater;
    private final List<Anket> mAnketler;
    private final Context mCon;
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


        final Anket anket= mAnketler.get(i);


        TextView baslik= satir.findViewById(R.id.title);
        baslik.setText(anket.getBaslik());

        TextView prebaslik= satir.findViewById(R.id.yazar);
        prebaslik.setText(anket.getYazar());

        RelativeLayout rel=satir.findViewById(R.id.relativeLayout);
        rel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(mCon, AnketActivity.class);
                intent.putExtra("anket_id",anket.getID());
                mCon.startActivity(intent);
            }
        });

        final ImageView imageView = satir.findViewById(R.id.list_image);

        AnimationDrawable animationDrawable;
        imageView.setBackgroundResource(R.drawable.spin_loader);
        animationDrawable = (AnimationDrawable)imageView.getBackground();
        animationDrawable.start();

        Ion.with(imageView)
                .error(R.drawable.danger)
                .load(anket.getImg())
                .setCallback(new FutureCallback<ImageView>() {
                    @Override
                    public void onCompleted(Exception e, ImageView result) {
                        imageView.setBackground(null);
                    }

                });

        if(anket.isVoted()){
            ImageView voted_image=satir.findViewById(R.id.imageView);
            voted_image.setImageResource(R.drawable.check_icon);
        }



        /*
        final TextView icerik= satir.findViewById(R.id.content);
        final String str_icerik=anket.getIcerik();
        icerik.setText(str_icerik);*/
        return satir;
    }


}


