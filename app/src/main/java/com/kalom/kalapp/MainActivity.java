package com.kalom.kalapp;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.kalom.kalapp.classes.Config;
import com.kalom.kalapp.classes.JSONParser;
import com.kalom.kalapp.classes.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutionException;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


  SessionManager session = new SessionManager(getApplicationContext());

        try{
            UserInfo us=new UserInfo(session.getToken());

            JSONObject info = us.execute().get();


            try{

                assert session.getToken()!=null;


                if(info==null){
                    System.out.println("Sunucuya Ulaşılamadı.");
                    /*
                    Context context = getApplicationContext();
                    CharSequence text = "Sunucuya Ulaşılamıyor Lütfen Daha Sonra Tekrar Deneyin!";
                    int duration = Toast.LENGTH_LONG;
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                    */

                    AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
                    builder1.setMessage("Sunucuya Şu Anda Erişilemiyor.");
                    builder1.setCancelable(true);

                    builder1.setPositiveButton(
                            "Tamam",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    finish();
                                }
                            });



                    AlertDialog alert11 = builder1.create();
                    alert11.show();


                    return;
                }

                if(info.get("valid").equals(true)){

                    System.out.println("Giriş yapılmış ana ekrana yönlendiriliyor");

                    Intent intent = new Intent(MainActivity.this, MainPage.class);
                    intent.putExtra("UserData", info.toString());
                    startActivity(intent);
                    finish();

                }else{
                    System.out.println("Giriş yapılmamış logine yönlendiriyor.");
                    Intent intent = new Intent(MainActivity.this, Login_Activity.class);
                    startActivity(intent);
                    finish();

                }


            }catch(JSONException | NullPointerException e){
                e.getMessage();
            }



        }catch (InterruptedException | ExecutionException e){
            e.getMessage();
            System.out.println("Başarısız");
        }


    }



    private class UserInfo extends AsyncTask<Void, String,JSONObject> {

        private String hash;

        UserInfo(String token) {
            hash=token;
        }

        @Override
        protected JSONObject doInBackground(Void... params) {

            // Simulate network access.
            JSONParser js=new JSONParser();

            try{


                System.out.println(hash);
                String api_call= Config.api_server+"?action=user_info&hash="+hash;

                return js.readJson(api_call);

            }catch(IOException | JSONException e){
                e.getMessage();
                System.out.println("ALINAMADI");
                return null;
            }
        }



    }


}





