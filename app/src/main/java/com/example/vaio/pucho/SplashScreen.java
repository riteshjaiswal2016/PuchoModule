package com.example.vaio.pucho;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

//Splash screen which shows pucho logo
public class SplashScreen extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //Showo activity in full screen

        setContentView(R.layout.activity_splash_screen);
    }

    @Override
    protected void onStart() {
        super.onStart();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    Thread.sleep(2000);
                    //Wait 2 sec to display the splash

                    startActivity(new Intent(SplashScreen.this,MainActivity.class));

                    finish();

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
    }}).start();

    }
}
