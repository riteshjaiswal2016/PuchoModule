package com.example.vaio.pucho;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

//Used to print the Secret Hash Key on Log so that this key can
//be used to make facebook developer account so to get sdk for FB login previledges

public class MyApplication extends Application {
    final static String TAG ="tag";

    @Override
    public void onCreate() {
        super.onCreate();
        printHash();
    }

    public void printHash(){
        try {

            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.example.vaio.pucho",
                    PackageManager.GET_SIGNATURES);

            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.i(TAG, Base64.encodeToString(md.digest(), Base64.DEFAULT));
                //Show the Hash key
            }
        } catch (PackageManager.NameNotFoundException e) {
        } catch (NoSuchAlgorithmException e) {
        }

    }

}
