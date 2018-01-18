package com.kalom.kalapp.classes;

public class Anket {

    private String Yazar;
    private String Baslik;
    private int ID;
    private String Image;
    private boolean Voted;

    public Anket(String mYazar, String mBaslik, String Image_url,int mID,boolean mVoted){
        Yazar =mYazar;
        Baslik=mBaslik;
        setID(mID);
        Image=Image_url;
        setVoted(mVoted);
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

    public String getImg() {
        return Image;
    }

    public void setImg(String img) {
        this.Image = img;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public boolean isVoted() {
        return Voted;
    }

    public void setVoted(boolean voted) {
        Voted = voted;
    }
}


