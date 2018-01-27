package com.kalom.kalapp.classes;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;

import com.kalom.kalapp.Login_Activity;

import org.json.JSONObject;

public class Config {

    public static final String api_server= "http://192.168.1.148/";

        /*
         * DUYURU AYAR
         */
    public static final int duyuru_load_one_time =5;
    public static int duyuru_max_uzunluk=50;

    /*
     * ANKET AYAR
     */
    public static final int anket_load_one_time =10;


    public static JSONObject check_login(final Activity activity){

        //Shared Preferences İçinde Tutulacak Olan Login Hash İçin Kütüphane
        SessionManager session = new SessionManager(activity);

        /*
         Eğer Login Hash Kayıtlı Değilse Daha Önce Hiç Giriş Yapılmamıştır.
          Bu Durumda Login Ekranına Yönlendir.
          ------------------------------------------------------------------
         */
        if(session.getToken()==null){
            Intent intent = new Intent(activity, Login_Activity.class);
            activity.startActivity(intent);
            activity.finish();
            return null;
        }
        //--------------------------------------------------------------


        try{

            //Login Hash İle Sunucuya Sorgu Yolla.
            UserInfo us=new UserInfo(session.getToken());

            //Suncudan Gelen Cevabı JSONObject Olarak Değişkene Aktar
            JSONObject info = us.execute().get();


            /*
            Eğer Sunucuya Ulaşılamadıysa, Bir Alert Dialog Oluştur ve Kullanıcıyı Bilgilendir.
            */
            if(info==null) {

                AlertDialog.Builder builder1 = new AlertDialog.Builder(activity);
                builder1.setMessage("Sunuculara Erişilemiyor.Lütfen Bağlantınızı Kontrol Edip Tekrar Deneyiniz.");
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "Tamam",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                activity.finish();
                                System.exit(0);
                            }
                        });



                AlertDialog alert11 = builder1.create();
                alert11.show();


                return null;

            }


                /*
                 Suncudan Gelen Verileri Kontrol Et.
                 Eğer Sunucu Login Hashi Onaylamışsa Ana Ekrana Yönlendir.
                  Eğer Login Hashin Süresi Dolmuşsa Oturumu Zaman Aşımına Uğrat.Logine Yönlendir.
                 */
            if(!info.get("valid").equals(true)){
                session.editor.clear().commit();


                AlertDialog.Builder builder1 = new AlertDialog.Builder(activity);
                builder1.setMessage("Başka Bir Cihazdan Giriş Yaptığın İçin Bu Oturumuna Kısa Bir Ara Veriyoruz :)");
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "Tamam",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent intent = new Intent(activity, Login_Activity.class);
                                activity.startActivity(intent);
                                activity.finish();
                            }
                        });



                AlertDialog alert11 = builder1.create();
                alert11.show();

            return null;
            }

            return info;


        }catch (Exception e){
                /*
                 Sunucyla Bağlantı Sağlanamdığını Konsola Yaz
                 */
            e.printStackTrace();
            return null;
        }
    }



}
