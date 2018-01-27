package com.kalom.kalapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.kalom.kalapp.classes.Config;
import com.kalom.kalapp.classes.SessionManager;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.makeramen.roundedimageview.RoundedImageView;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

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
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;


public class ProfilActivity extends AppCompatActivity implements View.OnClickListener {

    private RoundedImageView profileImage;
    private TextView changeProfileImage;
    private JSONObject UserInfo;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UserInfo = Config.check_login(this);
        setContentView(R.layout.profil_layout);

        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        profileImage = findViewById(R.id.profile_img);
        changeProfileImage = findViewById(R.id.change_profile_img);

        try {
            Ion.with(profileImage)
                    .load(UserInfo.get("img_url").toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }


        profileImage.setOnClickListener(this);
        changeProfileImage.setOnClickListener(this);

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
        UserInfo = Config.check_login(this);
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






}
