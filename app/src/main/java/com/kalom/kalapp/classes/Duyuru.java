package com.kalom.kalapp.classes;

import android.graphics.drawable.Drawable;

import java.io.InputStream;
import java.net.URL;

public class Duyuru {

    private String Category;
    private String Baslik;
    private String Icerik;
    private Drawable Image;

    public Duyuru(String mCategory,String mBaslik,String mIcerik,String Image_url){
        Category=mCategory;
        Baslik=mBaslik;
        Icerik=mIcerik;
       /* Image=LoadImageFromWebOperations(Image_url);*/
    }


    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        Category = category;
    }

    public String getBaslik() {
        return Baslik;
    }

    public void setBaslik(String baslik) {
        Baslik = baslik;
    }

    public String getIcerik() {
        return Icerik;
    }

    public void setIcerik(String icerik) {
        Icerik = icerik;
    }

    public Drawable getImg() {
        return Image;
    }

    public void setImg(Drawable img) {
        this.Image = img;
    }


    public static Drawable LoadImageFromWebOperations(String url) {
        try {
            InputStream is = (InputStream) new URL(url).getContent();
            Drawable d = Drawable.createFromStream(is, "src name");
            return d;
        } catch (Exception e) {
            return null;
        }
    }


}


