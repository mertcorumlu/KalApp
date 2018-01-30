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



}