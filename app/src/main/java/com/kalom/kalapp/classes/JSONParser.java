package com.kalom.kalapp.classes;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;

public class JSONParser {

    public String getKey(String URL,String Key) throws IOException, JSONException{

        JSONParser jsr = new JSONParser();

        JSONObject json =     jsr.readJson(URL);
       // System.out.println(json.getString("zip_code"));

        return (String) json.get(Key);
    }

    public JSONObject readJson(String url) throws IOException, JSONException {


        URL ur = new URL(url);

        HttpURLConnection huc = (HttpURLConnection) ur.openConnection();
        HttpURLConnection.setFollowRedirects(false);
        huc.setConnectTimeout(60);
        huc.connect();
        InputStream is = huc.getInputStream();
        InputStreamReader isr = new InputStreamReader(is, Charset.forName("UTF-8"));
        BufferedReader rd = new BufferedReader(isr);
        String jsonText = readAll(rd);
        JSONObject json = new JSONObject(jsonText);
        is.close();
        return json;


    }

    public String JsonString(String url) throws IOException, JSONException {
        InputStream is = new URL (url).openStream();
        InputStreamReader isr = new InputStreamReader(is, Charset.forName("UTF-8"));
        BufferedReader rd = new BufferedReader(isr);
        String jsonText = readAll(rd);
        is.close();
        return jsonText;
    }

    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }




}