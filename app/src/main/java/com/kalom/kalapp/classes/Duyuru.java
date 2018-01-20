package com.kalom.kalapp.classes;

public class Duyuru {

    private String Yazar;
    private String Baslik;
    private String Icerik;
    private final String Image;
    private int ID;

    public Duyuru(int mID,String mYazar,String mBaslik,String mIcerik,String Image_url){
        ID=mID;
        Yazar =mYazar;
        Baslik=mBaslik;
        Icerik=mIcerik;
        Image=Image_url;
    }


    public String getYazar() {
        return Yazar;
    }

    public void setYazar(String yazar) {
        Yazar = yazar;
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

    public String getImg() {
        return Image;
    }



}


