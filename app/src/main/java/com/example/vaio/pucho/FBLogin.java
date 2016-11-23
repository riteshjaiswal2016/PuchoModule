package com.example.vaio.pucho;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;


public class FBLogin extends Activity {
    final static String TAG="tag";
    //TAG used to filter the Logs

    CallbackManager callbackManager;


    FacebookCallback<LoginResult> facebookCallback= new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {

            if(Profile.getCurrentProfile() == null) {
                new ProfileTracker() {
                    @Override
                    protected void onCurrentProfileChanged(Profile newprofile, Profile oldprofile) {
                        //oldprofile is the profile before changes and new profile is profile after changes

                        Intent intent =new Intent(FBLogin.this,MainActivity.class);

                        intent.putExtra("name",oldprofile.getName());
                        //putting oldprofile full name as intent data with key "name"

                        intent.putExtra("lastname",oldprofile.getLastName());
                        //putting oldprofile lastname as intent data with key "lastname"

                        startActivity(intent);
                    }
                };
                // no need to call startTracking() on mProfileTracker
                // because it is called by its constructor, internally.
            }

            else {
                Profile profile = Profile.getCurrentProfile();

                Intent intent =new Intent(FBLogin.this,MainActivity.class);
                intent.putExtra("name",profile.getName());
                //putting profile full name as intent data with key "name"

                intent.putExtra("lastname",profile.getLastName());
                //putting profile last name as intent data with key "lastname"

                startActivity(intent);
            }
        }

        @Override
        public void onCancel() {
        }

        @Override
        public void onError(FacebookException error) {
            Toast.makeText(FBLogin.this,"Error Occured,Try Again!",Toast.LENGTH_SHORT).show();
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //To set the full screen when this activity appeared

        setContentView(R.layout.activity_fblogin);

        callbackManager= CallbackManager.Factory.create();

        LoginButton loginButton =(LoginButton)findViewById(R.id.login_button);
        //FB login button initialization

        loginButton.registerCallback(callbackManager,facebookCallback);
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode,resultCode,data);
    }
}
