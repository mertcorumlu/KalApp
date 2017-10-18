package com.kalom.kalapp;

import android.content.Context;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.kalom.kalapp.classes.JSONParser;
import com.kalom.kalapp.classes.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
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
            }catch(JSONException e){
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
                String api_call="http://10.0.2.2/?action=user_info&hash="+hash;

                return js.readJson(api_call);

            }catch(IOException | JSONException e){
                e.getMessage();
                System.out.println("ALINAMADI");
                return null;
            }
        }



    }


    public static boolean isConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager)context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnected()) {
            try {
                URL url = new URL("http://www.google.com/");
                HttpURLConnection urlc = (HttpURLConnection)url.openConnection();
                urlc.setRequestProperty("User-Agent", "test");
                urlc.setRequestProperty("Connection", "close");
                urlc.setConnectTimeout(1000); // mTimeout is in seconds
                urlc.connect();
                if (urlc.getResponseCode() == 200) {
                    return true;
                } else {
                    return false;
                }
            } catch (IOException e) {
                Log.i("warning", "Error checking internet connection", e);
                return false;
            }
        }

        return false;

    }

}





