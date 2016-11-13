package com.example.vaio.pucho;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

public class FBLogin extends Activity {

    final static String TAG="tag";
    CallbackManager callbackManager;
    FacebookCallback<LoginResult> facebookCallback= new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            startActivity(new Intent(FBLogin.this,MainActivity.class));

        }

        @Override
        public void onCancel() {

        }

        @Override
        public void onError(FacebookException error) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_fblogin);

        callbackManager= CallbackManager.Factory.create();

        LoginButton loginButton =(LoginButton)findViewById(R.id.login_button);
        loginButton.registerCallback(callbackManager,facebookCallback);
    }




    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //Profile profile =Profile.getCurrentProfile();
        //textView.setText(profile.getName());
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode,resultCode,data);
    }
}
