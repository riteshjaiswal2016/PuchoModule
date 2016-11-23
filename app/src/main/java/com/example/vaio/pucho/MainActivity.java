package com.example.vaio.pucho;

import android.annotation.TargetApi;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.facebook.FacebookSdk;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    final String TAG = "tag";
    //TAG to filter the logs

    MediaRecorder mediaRecorder;
    MediaPlayer mediaPlayer;

    File tempFile;
    //tempFile is the recorded audio file

    TextView testText;
    //testText is the text which appear on top of screen,which should be recorded

    InputStream inputStream;
    BufferedReader bufferedReader;

    String sent;
    //Intermidiate variable used to store the Line of the document in assest
    //which should be poped when user sumbit the recorded audio file to server

    String fullname = null, lastname = null;
    //User's FB profile's full name and last name

    ImageView micSpeakerView;
    //mic and speaker pics used which pop when user click record or play button

    RelativeLayout relative4;
    //Relative layout reference

    ProgressBar progressBar;
    //Progress bar when user click submit button

    Animation animationFadein;
    Animation animationFadeout;
    //mic speaker pic will be fade in and fade out as animation

    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams
            (RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
    //parameters for micSpeakerView and progress bar to show them dynamically

    ImageButton recordButton, playButton, stopButton, submitButton, rejectButton;


    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //Full screen set for this activity

        setContentView(R.layout.activity_main);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        //used to catch accidental disk or network access
        //on the application's main thread,
        //where UI operations are received and animations take place.

        fullname = getIntent().getStringExtra("name");
        lastname = getIntent().getStringExtra("lastname");
        //fetch last and full name from intent

        Log.i(TAG, " Name : " + fullname);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.argb(119, 0, 62, 83)));
        actionBar.setTitle(fullname);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setLogo(R.drawable.puchoicon60);
        //Customize the action bar of this activity by showing the user full name on it and
        //by displaying pucho logo


        recordButton = (ImageButton) findViewById(R.id.recordButton);
        playButton = (ImageButton) findViewById(R.id.playButton);
        stopButton = (ImageButton) findViewById(R.id.stopButton);
        submitButton = (ImageButton) findViewById(R.id.submitButton);
        rejectButton = (ImageButton) findViewById(R.id.rejectButton);

        recordButton.setEnabled(true);
        playButton.setEnabled(false);
        rejectButton.setEnabled(false);
        submitButton.setEnabled(false);
        stopButton.setEnabled(false);
        //Enable only buttons which is required when activity created

        animationFadein = AnimationUtils.loadAnimation(this, R.anim.fade);
        animationFadeout = AnimationUtils.loadAnimation(this, R.anim.fadeout);
        //fade is actually used for fade in animation and fadeout is for fade out animation

        relative4 = (RelativeLayout) findViewById(R.id.relativeLayout);

        testText = (TextView) findViewById(R.id.testText);

        try {
            inputStream = getAssets().open("one.txt");
            //Getting input stream of text file name "one.txt"
            //which I used to store predetermined English sentence which
            //should be popped on screen

            InputStreamReader isr = new InputStreamReader(inputStream);

            bufferedReader = new BufferedReader(isr);
        } catch (IOException e) {
            e.printStackTrace();
        }

        testText.setText(textGenerator());
    }


    //TextGenerator is read one.txt line by line
    private String textGenerator() {
        try {
            sent = bufferedReader.readLine();
            return sent;

        } catch (IOException e) {
            return "Error";
        }
    }


    //When discard button clicked which discard the recorded audio file
    public void rejectClicked(View view) {
        recordButton.setEnabled(true);
        playButton.setEnabled(false);
        rejectButton.setEnabled(false);
        submitButton.setEnabled(false);
        stopButton.setEnabled(false);
        //Enable only required buttons

        tempFile = null;
        //set temp recorded file to null

        Toast.makeText(MainActivity.this, "File Discarded!", Toast.LENGTH_SHORT).show();
    }


    //When record button clicked
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
    public void recordClicked(View view) {
        try {
            recordButton.setEnabled(false);
            playButton.setEnabled(false);
            rejectButton.setEnabled(false);
            submitButton.setEnabled(false);
            stopButton.setEnabled(true);

            micSpeakerView = new ImageView(this);

            params.addRule(RelativeLayout.CENTER_IN_PARENT);

            micSpeakerView.setLayoutParams(params);
            micSpeakerView.setImageResource(R.drawable.mic);
            micSpeakerView.setTag(R.drawable.mic);

            relative4.addView(micSpeakerView);
            micSpeakerView.startAnimation(animationFadein);
            //Here we want to show mic with fade in animation
            //we set Tag of mic to this imageView so that we can
            //detect it whether this imageView contains mic's pic or
            //speaker's

            tempFile = File.createTempFile("tempFile", "mp3", view.getContext().getCacheDir());
            //Create a temparary file to store recording as tempfile.mp3

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

    //when play button clicked
    public void playClicked(View view) {
        recordButton.setEnabled(false);
        playButton.setEnabled(false);
        stopButton.setEnabled(true);
        rejectButton.setEnabled(false);
        submitButton.setEnabled(false);

        micSpeakerView = new ImageView(this);
        micSpeakerView.setLayoutParams(params);

        micSpeakerView.setImageResource(R.drawable.speaker);
        micSpeakerView.setTag(R.drawable.speaker);

        relative4.addView(micSpeakerView);
        micSpeakerView.startAnimation(animationFadein);
        //Here we want to show speaker with fade in animation
        //we set Tag of speaker to this imageView so that we can
        //detect it whether this imageView contains mic's pic or
        //speaker's later

        mediaPlayer = new MediaPlayer();

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                recordButton.setEnabled(true);
                playButton.setEnabled(true);
                stopButton.setEnabled(false);
                rejectButton.setEnabled(true);
                submitButton.setEnabled(true);

                micSpeakerView.startAnimation(animationFadeout);
                relative4.removeView(micSpeakerView);
                //fadeout the Speaker pic after play is complete
            }
        });


        if (tempFile != null) {
            try {
                mediaPlayer.setDataSource(tempFile.getPath());
                mediaPlayer.prepare();
                mediaPlayer.start();

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else
            Toast.makeText(MainActivity.this, "File is Empty!", Toast.LENGTH_SHORT).show();

    }


    //when stop button clicked
    public void stopClicked(View view) {
        recordButton.setEnabled(true);
        playButton.setEnabled(true);
        submitButton.setEnabled(true);
        stopButton.setEnabled(false);
        rejectButton.setEnabled(true);

        //Check whether stop button clicked to stop recording or to stop audio playing
        //by using Tag of micSpeakerView
        if ((int) micSpeakerView.getTag() == (R.drawable.mic)) {
            mediaRecorder.stop();
            mediaRecorder.release();
        } else {
            mediaPlayer.stop();
            mediaPlayer.release();
        }

        micSpeakerView.startAnimation(animationFadeout);
        relative4.removeView(micSpeakerView);
        //finally fadeout the image and remove the view from activity

    }


    //handler used which recieve message as record file is uploaded to server or not successfully
    //and do change in UI as required
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.arg1 == 200) {
                Toast.makeText(MainActivity.this, "File Uploaded!", Toast.LENGTH_SHORT).show();

                submitButton.setEnabled(false);
                playButton.setEnabled(false);
                stopButton.setEnabled(false);
                recordButton.setEnabled(true);
                rejectButton.setEnabled(false);

                tempFile = null;
                //Discard the file

                testText.setText(textGenerator());
                //Show next Sentence on screen if record file uploaded successfully
            } else {
                Toast.makeText(MainActivity.this, "Error!Try Again", Toast.LENGTH_SHORT).show();

                submitButton.setEnabled(true);
                playButton.setEnabled(true);
                stopButton.setEnabled(false);
                recordButton.setEnabled(false);
                rejectButton.setEnabled(true);
            }

            relative4.removeView(progressBar);
            //remove Progress bar

        }
    };



    //When submit button clicked
    public void submitClicked(View view) {
        if (isInternetAvailable()) {
            stopButton.setEnabled(false);
            playButton.setEnabled(false);
            rejectButton.setEnabled(false);
            recordButton.setEnabled(false);
            submitButton.setEnabled(false);


            progressBar = new ProgressBar(this);
            params.addRule(RelativeLayout.CENTER_IN_PARENT);
            progressBar.setLayoutParams(params);
            relative4.addView(progressBar);
            //Show progress bar

            new Thread(new Runnable() {
                @Override
                public void run() {

                    HttpURLConnection conn;
                    DataOutputStream dos;

                    String lineEnd = "\r\n";
                    String twoHyphens = "--";
                    String boundary = "*****";
                    //Used as header elements when we make http request

                    int bytesRead, bytesAvailable, bufferSize;

                    byte[] buffer;
                    //buffer to store record file as bytes array

                    int maxBufferSize = 1 * 1024 * 1024;
                    //1 MB max size of record file

                    String urlString = "http://puchoserver.96.lt/pucho.php";
                    //Server's URL which php file

                    try {
                        FileInputStream fileInputStream = new FileInputStream(tempFile);

                        URL url = new URL(urlString);
                        // open a URL connection to the Servlet

                        conn = (HttpURLConnection) url.openConnection();
                        // Open a HTTP connection to the URL

                        conn.setConnectTimeout(40 * 1000);

                        conn.setDoInput(true);
                        // Allow Inputs

                        conn.setDoOutput(true);
                        // Allow Outputs

                        conn.setUseCaches(false);
                        // Don't use a cached copy.

                        conn.setRequestMethod("POST");
                        // Use a post method.

                        conn.setRequestProperty("Connection", "Keep-Alive");
                        conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                        //Generate multipart encoded http request

                        dos = new DataOutputStream(conn.getOutputStream());

                        dos.writeBytes(twoHyphens + boundary + lineEnd);
                        //write header element of http request to server

                        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
                        String currentDateandTime = sdf.format(new Date());
                        //Get current date time to rename the record file

                        dos.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\"" + lastname + currentDateandTime + ".mp3\"" + lineEnd);
                        //Renamed the recorded file Like= "Sharma19801121_124013.mp3"

                        dos.writeBytes(lineEnd);
                        //http request header end

                        // create a buffer of maximum size
                        bytesAvailable = fileInputStream.available();
                        bufferSize = Math.min(bytesAvailable, maxBufferSize);
                        buffer = new byte[bufferSize];

                        bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                        dos.write(buffer);
                        //write the file to server

                        dos.writeBytes(lineEnd);

                        dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
                        //http request end

                        Log.i(TAG, "Bytes Read : " + bytesRead);
                        int res = conn.getResponseCode();

                        Message message = Message.obtain();
                        message.arg1 = res;
                        message.obj = progressBar;
                        handler.sendMessage(message);
                        //Sendig response code to handler to change UI accordingly

                        fileInputStream.close();
                        dos.flush();
                        dos.close();

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (ProtocolException e) {
                        e.printStackTrace();
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                }
            }).start();
        }

        else
            Toast.makeText(MainActivity.this,"No Internet Connection!",Toast.LENGTH_SHORT).show();

    }

    public boolean isInternetAvailable() {
                try {
                    InetAddress ipAddr = InetAddress.getByName("www.google.com");
                    return !ipAddr.equals("");

                } catch (Exception e) {
                    return false;
                }
            }


}