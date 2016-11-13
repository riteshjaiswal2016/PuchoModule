package com.example.vaio.pucho;

import android.annotation.TargetApi;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class MainActivity extends AppCompatActivity  {
    final String TAG = "tag";
    MediaRecorder mediaRecorder;
    File tempFile;
    TextView testText;
    InputStream inputStream;
    BufferedReader bufferedReader;
    String sent;
    TextView nameText;

    Button recordButton,playButton,stopButton,submitButton;

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        nameText =(TextView)findViewById(R.id.nameText);

        testText =(TextView)findViewById(R.id.testText);
        recordButton =(Button)findViewById(R.id.recordButton);
        playButton =(Button)findViewById(R.id.playButton);
        stopButton =(Button)findViewById(R.id.stopButton);
        submitButton =(Button)findViewById(R.id.submitButton);

        playButton.setClickable(false);
        stopButton.setClickable(false);
        submitButton.setClickable(false);

        try {
            inputStream = getAssets().open("one.txt");
            InputStreamReader isr = new InputStreamReader(inputStream);
            bufferedReader = new BufferedReader(isr);

        } catch (IOException e) {
            e.printStackTrace();
        }

        testText.setText(textGenerator());

    }


    private String textGenerator(){
        try {
            sent = bufferedReader.readLine();
            Log.i(TAG,sent);
            return sent;
        } catch (IOException e) {
            Log.i(TAG,"exc catch");
            return "Error";
        }
    }

    public void recordClicked(View view) {
        try {
            recordButton.setClickable(false);
            playButton.setClickable(false);
            stopButton.setClickable(true);
            submitButton.setClickable(false);

            tempFile = File.createTempFile("tempFile", "mp3", view.getContext().getCacheDir());

            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
            mediaRecorder.setOutputFile(tempFile.getPath());

            mediaRecorder.prepare();
            mediaRecorder.start();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void playClicked(View view) {
        recordButton.setClickable(true);
        playButton.setClickable(true);
        stopButton.setClickable(true);

        MediaPlayer mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(tempFile.getPath());

            mediaPlayer.prepare();
            mediaPlayer.start();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void onLogin(View view){
        Intent intent = new Intent(MainActivity.this,FBLogin.class);
        startActivity(intent);

    }


    public void stopClicked(View view) {
        stopButton.setClickable(false);
        playButton.setClickable(true);
        recordButton.setClickable(true);
        submitButton.setClickable(true);

        mediaRecorder.stop();
        mediaRecorder.release();
    }

    public void submitClicked(View view) {
        HttpURLConnection conn ;
        DataOutputStream dos;
        //DataInputStream inStream = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        String responseFromServer = "";
        String urlString = "http://puchoserver.96.lt/pucho.php";

        try {
            testText.setText(textGenerator());
        }catch (Exception e){
            Log.i(TAG,"hahaha Caught it");
        }
        try {
            //------------------ CLIENT REQUEST

            FileInputStream fileInputStream = new FileInputStream(tempFile);

            // open a URL connection to the Servlet
            URL url = new URL(urlString);
            // Open a HTTP connection to the URL

            conn = (HttpURLConnection) url.openConnection();

            conn.setConnectTimeout(40*1000);

            //conn.connect();

            // Allow Inputs
            conn.setDoInput(true);
            // Allow Outputs
            conn.setDoOutput(true);
            // Don't use a cached copy.
            conn.setUseCaches(false);
            // Use a post method.
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary="+boundary);

            dos = new DataOutputStream( conn.getOutputStream() );
            Log.i(TAG,"yo done : ");
            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\"tempafile.mp3\""+ lineEnd);


            dos.writeBytes(lineEnd);

            // create a buffer of maximum size
            bytesAvailable = fileInputStream.available();
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];

            bytesRead = fileInputStream.read(buffer, 0, bufferSize);

            Log.i(TAG,"cooooool : "+bytesRead);
            dos.write(buffer);

            dos.writeBytes(lineEnd);

            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            int res = conn.getResponseCode();
            if(res==200)
                Toast.makeText(MainActivity.this,"File Uploaded!",Toast.LENGTH_SHORT).show();

            Log.i(TAG,"yo done : "+res);
            fileInputStream.close();
            dos.flush();
            dos.close();

            tempFile=null;

        }   catch (FileNotFoundException e) {
            e.printStackTrace();
        }   catch (ProtocolException e) {
            e.printStackTrace();
        }   catch (MalformedURLException e) {
            e.printStackTrace();
        }   catch (IOException e) {
            e.printStackTrace();
        }

    }

}
