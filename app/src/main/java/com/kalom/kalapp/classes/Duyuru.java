package com.kalom.kalapp.classes;

public class Duyuru {

    private String Yazar;
    private String Baslik;
    private String Icerik;
    private String yazarImage;
    private String contentImage;
    private String date;
    private int ID;

    public Duyuru(int mID,String mYazar,String mBaslik,String mIcerik,String myazarImage,String mcontentImage,String mDate){
        setID(mID);
        setYazar(mYazar);
        setBaslik(mBaslik);
        setIcerik(mIcerik);
        setYazarImage(myazarImage);
        setContentImage(mcontentImage);
        setDate(mDate);
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

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getContentImage() {
        return contentImage;
    }

    public void setContentImage(String contentImage) {
        this.contentImage = contentImage;
    }

    public String getYazarImage() {
        return yazarImage;
    }

    public void setYazarImage(String yazarImage) {
        this.yazarImage = yazarImage;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}


