package com.kalom.kalapp;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.Loader;
import android.database.Cursor;
import android.os.AsyncTask;

import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.view.WindowManager;

import java.io.IOException;
import java.util.List;

import com.google.firebase.iid.FirebaseInstanceId;
import com.kalom.kalapp.classes.Config;
import com.kalom.kalapp.classes.JSONParser;

import org.json.JSONException;
import org.json.JSONObject;

import com.kalom.kalapp.classes.SessionManager;

import	android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

/**
 * A login screen that offers login via email/password.
 */
public class Login_Activity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    /*
      Id to identity READ_CONTACTS permission request.
     */
   // private static final int REQUEST_READ_CONTACTS = 0;


    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE|WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);




        // Set up the login form.
        mEmailView = findViewById(R.id.okul_no);
        populateAutoComplete();

        mPasswordView = findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                hideSoftKeyboard();
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    private void populateAutoComplete() {
        getLoaderManager().initLoader(0, null, this);
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_okul_no));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(email, password);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
       return android.text.TextUtils.isDigitsOnly(email);
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 6;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
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
        mProgressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        /*
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");*/
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        /*
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);*/
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(Login_Activity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }


    /*
    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
       // int IS_PRIMARY = 1;
    }*/

    private void hideSoftKeyboard() {
        if(getCurrentFocus()!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            if (inputMethodManager != null) {
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
        }
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
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

                    System.out.println("Giriş Yapıldı Yönlendiriliyor.");
                    Intent intent = new Intent(Login_Activity.this, MainActivity.class);
                    startActivity(intent);
                    finish();


                } else {
                /*
                 Daha Önce @error değişkenine aktarılan hata yazısını @mPasswordView değişkeni üzerinden kullanıcıya göster
                 */

                    //loaderı gizle
                    showProgress(false);
                    mEmailView.setError("");
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

