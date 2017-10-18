package com.kalom.kalapp.classes;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.kalom.kalapp.R;

import org.w3c.dom.Text;

import java.util.List;


public class DuyuruAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private List<Duyuru> mDuyurular;

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

        satir=mInflater.inflate(R.layout.list_layout, null);

        Duyuru duy=mDuyurular.get(i);

        TextView baslik=(TextView) satir.findViewById(R.id.title);
        baslik.setText(duy.getBaslik());

        TextView icerik=(TextView) satir.findViewById(R.id.textView);
        icerik.setText(duy.getIcerik());





        return satir;
    }
}
