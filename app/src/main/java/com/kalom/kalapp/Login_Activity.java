package com.kalom.kalapp;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;

import android.os.AsyncTask;

import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.view.WindowManager;

import java.io.IOException;

import com.google.firebase.iid.FirebaseInstanceId;
import com.kalom.kalapp.classes.Config;
import com.kalom.kalapp.classes.JSONParser;

import org.json.JSONException;
import org.json.JSONObject;

import com.kalom.kalapp.classes.SessionManager;

import	android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

public class Login_Activity extends AppCompatActivity {


    private UserLoginTask mAuthTask = null;

    // UI
    private AutoCompleteTextView mOkulnoView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE|WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        // Set up the login form.
        mOkulnoView = findViewById(R.id.okul_no);

        mPasswordView = findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == 1 || id == EditorInfo.IME_ACTION_DONE) {
                    giris_yap();
                    return true;
                }
                return false;
            }
        });

        Button mGirisYapButon = findViewById(R.id.giris_yap_buton);
        mGirisYapButon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                giris_yap();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    private void giris_yap() {
        hideSoftKeyboard();

        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mOkulnoView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String okulno = mOkulnoView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;




        // Check for a valid okulno
        if (TextUtils.isEmpty(okulno)) {
            mOkulnoView.setError(getString(R.string.error_field_required));
            focusView = mOkulnoView;
            cancel = true;
        } else if (!isOkulnoValid(okulno)) {
            mOkulnoView.setError(getString(R.string.error_invalid_okul_no));
            focusView = mOkulnoView;
            cancel = true;
        }

        // Check for a valid Password
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        } else if (!isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        if (cancel) {

            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(okulno, password);
            mAuthTask.execute();
        }
    }

    private boolean isOkulnoValid(String okulno) {
       return android.text.TextUtils.isDigitsOnly(okulno);
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 6;
    }

    private void showProgress(final boolean show) {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgressView.setBackgroundResource(R.drawable.loader);
        AnimationDrawable draw=(AnimationDrawable) mProgressView.getBackground();
        draw.start();
        mProgressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

    private void hideSoftKeyboard() {
        if(getCurrentFocus()!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            if (inputMethodManager != null) {
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        /**
         *Giriş yaptırmak için yazılan AsyncTask Sınıfı.
         */

        private final String mEmail;
        private final String mPassword;
        private  String error;
        private boolean isInternetDown;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }



        @Override
        protected Boolean doInBackground(Void... params) {


            //sunucyla bağlantı kuramazsa true döndürür
            isInternetDown=false;

            //veri almak için JSONParser kütüphanesi
            JSONParser js=new JSONParser();

            try{
                //giriş yapmak için sunucuya yollanan sorgu
                String api_call= Config.api_server+"?action=login&okul_no="+mEmail+"&pass="+mPassword+"&fcms_token="+ FirebaseInstanceId.getInstance().getToken();
                System.out.println(api_call);

                //sunucudan gelen veriyi JSONObject e aktardık
               JSONObject LoginCallback = js.readJson(api_call);


               //Eğer sunucudan cevap olarak error=false gelirse,kullanıcın sağladığı bilgiler doğrudur.Giriş Yaptırılır.
                if(LoginCallback.get("error").equals("false")){

                    //Kullanıcın Login Hash i Shared Preferences e kaydedilir.
                    SessionManager session = new SessionManager(getApplicationContext());
                    session.createLoginSession( (String) LoginCallback.get("hash") );

                    return true;
                }else{
                    /*
                     Eğer Error=true dönmüşse kullanıcın verdiği bilgiler yanlıştır.Sunucudan gelen error mesajını @error değişkenine aktar.
                     */
                    error=(String) LoginCallback.get("message");
                    return false;
                }


            }catch(IOException | JSONException e){
                e.getMessage();

                //Sunucuya ulaşılamamıştır.@isInternetDown değişkenini true olarak değiştir.
                isInternetDown=true;

                Log.d("MESAJ","Kullanıcı Bilgileri Sunucudan Alınamadı.");

            }

            return false;


        }

        @Override
        protected void onPostExecute(final Boolean success) {

            /*
             İşlem çalıştırıldıktan sonra gelen cevapları kontrol Eder.
              Gelen cevaplara göre giriş işlemini gerkeçkleştirir.
             */

            //form sıfırlanır
            mAuthTask = null;


            /*
             Eğer sunucuya ulaşılamamışsa Toast mesajı ile kullanıcı bilgilendirilir.
              Herhangi bir işlem Yapılmaz.
             */
            if(isInternetDown){
                showProgress(false);
                Toast tost= Toast.makeText(getApplicationContext(),"Sunucularımız Şu Anda Hizmet Veremiyor. Lütfen Birkaç Dakika Sonra Tekrar Deneyin.",Toast.LENGTH_LONG);
                tost.show();
                return;
            }



                if (success) {

                /*
                 Eğer Sunucudan Error=false dönmüşse giriş yapılmış demektir.
                  Ana Ekrana Yönlendir.
                 */

                    Intent intent = new Intent(Login_Activity.this, MainActivity.class);
                    startActivity(intent);
                    finish();


                } else {
                /*
                 Daha Önce @error değişkenine aktarılan hata yazısını @mPasswordView değişkeni üzerinden kullanıcıya göster
                 */

                    //loaderı gizle
                    showProgress(false);
                    mOkulnoView.setError("");
                    mPasswordView.setError(error);
                    mPasswordView.setText("");
                    mPasswordView.requestFocus();
                }

        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }

}

