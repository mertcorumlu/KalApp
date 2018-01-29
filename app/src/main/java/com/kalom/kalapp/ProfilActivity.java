package com.kalom.kalapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.kalom.kalapp.classes.Config;
import com.kalom.kalapp.classes.JSONParser;
import com.kalom.kalapp.classes.SessionManager;
import com.koushikdutta.ion.Ion;
import com.makeramen.roundedimageview.RoundedImageView;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.ybs.passwordstrengthmeter.PasswordStrength;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;



public class ProfilActivity extends AppCompatActivity implements View.OnClickListener {

    private RoundedImageView profileImage;
    private TextView changeProfileImage;
    private JSONObject userInfo;
    private LinearLayout buttonSifre;
    boolean isCurrentOK = false,
            isNewOK = false,
            isRepeatOK= false;
    private EditText email,telefon;
    private String hash;
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profil_layout);

        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        dialog = new Dialog(this);


        profileImage = findViewById(R.id.profile_img);
        changeProfileImage = findViewById(R.id.change_profile_img);
        buttonSifre = findViewById(R.id.button_sifre);


        profileImage.setOnClickListener(this);
        changeProfileImage.setOnClickListener(this);
        buttonSifre.setOnClickListener(this);


        email = findViewById(R.id.change_email);
        telefon = findViewById(R.id.change_telefon);

        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void afterTextChanged(Editable s) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                View emailSeperator = findViewById(R.id.email_seperator);
                if(isValidEmail(s)){
                    emailSeperator.setBackgroundColor(Color.GREEN);
                }else{
                    emailSeperator.setBackgroundColor(Color.RED);
                }

            }


        });

        email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {

                    View emailSeperator = findViewById(R.id.email_seperator);
                    CharSequence s = email.getText();

                    if (isValidEmail(s)) {
                        emailSeperator.setBackgroundColor(Color.parseColor("#7b7a7a"));

                        ProgressBar emailProgress = findViewById(R.id.change_email_progress);
                        ImageView emailImage = findViewById(R.id.change_email_icon);

                        Object[] objects = new Object[]{emailProgress,emailImage,emailSeperator};

                        new UpdateUser("email", s.toString(), 2, objects).execute();


                    } else {
                        emailSeperator.setBackgroundColor(Color.RED);
                        email.setError("Lütfen Geçerli Bir Email Adresi Girin.");
                    }

                }
            }
        });

        telefon.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void afterTextChanged(Editable s) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                View telefonSeperator = findViewById(R.id.telefon_seperator);
                if(isValidTelefon(s)){
                    telefonSeperator.setBackgroundColor(Color.GREEN);
                }else{
                    telefonSeperator.setBackgroundColor(Color.RED);
                }

            }


        });

        telefon.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {

                    View telefonSeperator = findViewById(R.id.telefon_seperator);
                    CharSequence s = telefon.getText();

                    if (isValidTelefon(s)) {
                        telefonSeperator.setBackgroundColor(Color.parseColor("#7b7a7a"));

                        ProgressBar telefonProgress = findViewById(R.id.change_telefon_progress);
                        ImageView telefonImage = findViewById(R.id.change_telefon_icon);

                        Object[] objects = new Object[]{telefonProgress,telefonImage,telefonSeperator};

                        new UpdateUser("telefon", s.toString(), 3, objects).execute();


                    } else {
                        telefonSeperator.setBackgroundColor(Color.RED);
                        telefon.setError("Lütfen Geçerli Bir Telefon Girin.");
                    }

                }
            }
        });

        Intent intent = getIntent();
        try {
            userInfo = new JSONObject(intent.getStringExtra("user_info"));
            hash = userInfo.get("hash").toString();
            if(!userInfo.get("img_url").toString().equals("null")) {
                Ion.with(profileImage)
                        .load(userInfo.get("img_url").toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }




    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.profile_img :
            case R.id.change_profile_img:
                CropImage.activity()
                        .setCropShape(CropImageView.CropShape.OVAL)
                        .setAspectRatio(100,100)
                        .setMinCropResultSize(50,50)
                        .setMaxCropResultSize(1200,1200)
                        .setInitialCropWindowPaddingRatio(0)
                        .setAllowFlipping(false)
                        .setAllowRotation(false)
                        .setCropMenuCropButtonTitle("İleri")
                        .start(this);
                break;


            case R.id.button_sifre:
                sifre_dialog();
                break;


        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();

                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri);
                    new UploadImage(bitmap).execute();

                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(getApplicationContext(),result.getError().toString(), Toast.LENGTH_LONG).show();
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch(item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                break;

        }
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public void sifre_dialog(){

        isCurrentOK = false;
        isNewOK =  false;
        isRepeatOK = false;


        dialog.setTitle("Şifre Değiştir");
        dialog.setContentView(R.layout.sifre_degistir);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        final EditText currentPass,newPass,newPassRepeat;
        final Button cancelButton,doneButton;
        final ProgressBar currentProgress;
        final View seperatorCurrent,seperatorRepeat;

        currentProgress = dialog.findViewById(R.id.current_progress);

        seperatorCurrent = dialog.findViewById(R.id.seperator_current);
        seperatorRepeat = dialog.findViewById(R.id.seperator_repeat);

        currentPass = dialog.findViewById(R.id.current_pass);
        newPass = dialog.findViewById(R.id.new_pass);
        newPassRepeat = dialog.findViewById(R.id.new_pass_repeat);

        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void afterTextChanged(Editable s) {

            }


            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                String text = s.toString();

                currentPass.setError(null);
                newPass.setError(null);
                newPassRepeat.setError(null);

                PasswordStrength str = PasswordStrength.calculateStrength(text);

                if(currentPass.hasFocus()){

                    if(text.length()>=8){
                        seperatorCurrent.setBackgroundColor(Color.GREEN);
                        isCurrentOK = true;
                    }else{
                        seperatorCurrent.setBackgroundColor(Color.RED);
                        isCurrentOK = false;
                    }

                }else if(newPass.hasFocus()){

                    if(!newPassRepeat.getText().toString().isEmpty() &&  newPassRepeat.getText().toString().length() >= 8){

                        if(newPassRepeat.getText().toString().equals(text)){

                            seperatorRepeat.setBackgroundColor(Color.GREEN);
                            isRepeatOK = true;

                        }else{

                            seperatorRepeat.setBackgroundColor(Color.RED);
                            isRepeatOK = false;
                        }

                    }


                    if(text.isEmpty()){

                        currentProgress.setProgress(0);
                        isNewOK = false;

                    } else if (str.getText(getApplicationContext()).equals("Weak")) {

                        currentProgress.setProgress(25);
                        currentProgress.getProgressDrawable().setColorFilter(str.getColor(), PorterDuff.Mode.SRC_IN);
                        isNewOK = false;

                    } else if (str.getText(getApplicationContext()).equals("Medium")) {

                        currentProgress.setProgress(50);
                        currentProgress.getProgressDrawable().setColorFilter(str.getColor(), PorterDuff.Mode.SRC_IN);
                        isNewOK = true;


                    } else if (str.getText(getApplicationContext()).equals("Strong")) {

                        currentProgress.setProgress(75);
                        currentProgress.getProgressDrawable().setColorFilter(Color.BLUE, PorterDuff.Mode.SRC_IN);
                        isNewOK = true;

                    } else {

                        currentProgress.getProgressDrawable().setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN);
                        currentProgress.setProgress(100);
                        isNewOK = true;

                    }



                }else if(newPassRepeat.hasFocus()) {

                    if(!newPass.getText().toString().isEmpty() && text.length() >= 8){

                        if(newPass.getText().toString().equals(text)){

                            seperatorRepeat.setBackgroundColor(Color.GREEN);
                            isRepeatOK = true;

                        }else{

                            seperatorRepeat.setBackgroundColor(Color.RED);
                            isRepeatOK = false;
                        }

                    }else{
                        seperatorRepeat.setBackgroundColor(Color.RED);
                        isRepeatOK = false;
                    }

                }



            }


        };

        currentPass.addTextChangedListener(watcher);
        newPass.addTextChangedListener(watcher);
        newPassRepeat.addTextChangedListener(watcher);


        cancelButton = dialog.findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        doneButton = dialog.findViewById(R.id.done_button);
        doneButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                currentPass.clearFocus();
                newPass.clearFocus();
                newPassRepeat.clearFocus();

                if(isCurrentOK & isNewOK & isRepeatOK){

                    ProgressBar sifreProgress = dialog.findViewById(R.id.sifre_progress);
                    TextView errorText = dialog.findViewById(R.id.error_text);


                    final Object[] objects = new Object[]{sifreProgress,doneButton,errorText};



                                JSONArray ar = new JSONArray();
                                ar.put(currentPass.getText().toString());
                                ar.put(newPass.getText().toString());
                                ar.put(newPassRepeat.getText().toString());

                                new UpdateUser("password",ar.toString(),1,objects).execute();

                }else{



                    if(!isRepeatOK){
                        newPassRepeat.requestFocus();
                        newPassRepeat.setError("Şifreler Uyuşmuyor.");
                    }

                    if(!isNewOK){
                        newPass.requestFocus();
                        newPass.setError("Lütfen Daha Güçlü Bir Şifre Seçin.");
                    }

                    if(!isCurrentOK){
                        currentPass.requestFocus();
                        currentPass.setError("Şifreniz En Az 8 Karakter Olmalıdır.");
                    }


                }


            }
        });

        dialog.show();

    }

    public boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    public boolean isValidTelefon(CharSequence target) {
        return target.toString().matches("^05[0-9]{9}$");
    }


    @SuppressLint("StaticFieldLeak")
    private class UploadImage extends AsyncTask<Void,Void,String>{

        Bitmap image;
        public UploadImage(Bitmap image){
            this.image=image;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //profileImage.setImageDrawable(null);
            //profileImage.setBackgroundResource(R.drawable.test);
        }

        @Override
        protected String doInBackground(Void... voids) {
            SessionManager session = new SessionManager(getApplicationContext());

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
            String encodedImage = Base64.encodeToString(byteArrayOutputStream.toByteArray(),Base64.DEFAULT);

            ArrayList<NameValuePair> dataToSend = new ArrayList<>();
            dataToSend.add(new BasicNameValuePair("image",encodedImage));


            HttpClient client = new DefaultHttpClient(getHttpRequestParams());
            HttpPost post = new HttpPost(Config.api_server + "?action=upload_profile_image&hash=" + session.getToken());

            try {
                post.setEntity(new UrlEncodedFormEntity(dataToSend));
                HttpResponse response = client.execute(post);
                HttpEntity httpEntity = response.getEntity();
                return EntityUtils.toString(httpEntity);

            } catch (Exception e){
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                JSONObject response=new JSONObject(result);
                if(response.get("error").equals(false)){
                    profileImage.setImageBitmap(image);
                }else{
                    Toast.makeText(getApplicationContext(),response.get("message").toString(),Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        private HttpParams getHttpRequestParams(){
            HttpParams httpRequestParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpRequestParams,1000*30);
            HttpConnectionParams.setSoTimeout(httpRequestParams,1000*30);
            return httpRequestParams;
        }

    }


    @SuppressLint("StaticFieldLeak")
    private class UpdateUser extends AsyncTask<Void, Void, String> {
        //Type 1 == UPDATE PASSWORD
        //Type 2 == UPDATE EMAIL
        //Type 3 == UPDATE TELEFON

        public int type;
        private Object[] objects;
        private String key,value;


        public UpdateUser(String key,String value,int type,Object[] objects){
            this.type = type;
            this.objects = objects;
            this.key = key;
            this.value = value;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();


            if(type==1){
                ((ProgressBar) objects[0]).setVisibility(View.VISIBLE);
                ((Button) objects[1]).setVisibility(View.GONE);
                ((TextView) objects[2]).setText("");
            }

            if(type==2 || type==3){
                ((ProgressBar) objects[0]).setVisibility(View.VISIBLE);
                ((ImageView) objects[1]).setVisibility(View.GONE);
            }


        }

        @Override
        protected String doInBackground(Void... voids) {

            JSONParser js=new JSONParser();

            try{
                //login kontrol etmek için sunucuya yapılması gereken istek
                String api_call= Config.api_server + "?action=update_user&hash="+ hash + "&key=" + key + "&value=" + value;
                //JSONParser kütüphanesi ile sunucuya istek yollanır.
                //Yanıt olarak JSONObject döner

                Log.d("TEST",api_call);

                return js.JsonString(api_call);

            }catch(IOException e){
                e.printStackTrace();
                return null;

            }
        }

        @Override
        protected void onPostExecute(String res) {
            super.onPostExecute(res);
            JSONObject result;
            try {
                result = new JSONObject(res);


            if(type==1) {
                ((ProgressBar) objects[0]).setVisibility(View.GONE);
                ((Button) objects[1]).setVisibility(View.VISIBLE);

                if(result.get("error").toString().equals("false")){
                    dialog.dismiss();
                }else{
                    ((TextView) objects[2]).setText(result.get("message").toString());
                }

            }

            if(type==2 || type==3) {
                ((ProgressBar) objects[0]).setVisibility(View.GONE);
                ((ImageView) objects[1]).setVisibility(View.VISIBLE);

                if (result.get("error").toString().equals("true")) {
                    email.setError(result.get("message").toString());
                    ((View) objects[2]).setBackgroundColor(Color.RED);
                }


            }



            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }








}
