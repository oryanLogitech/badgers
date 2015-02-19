package com.example.oryan.testapp1;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.io.IOException;


public class SideActivity extends ActionBarActivity {

    Button backButton;
    Button soundButton;
    Button inputButton;
    Button playbackButton;
    Button chatButton;
    MediaPlayer sounder;
    MediaPlayer sounder2;
    MediaPlayer sounderMusic;
    MediaPlayer sounderCall;
    MediaRecorder recorder;
    boolean isMusicPlaying;
    boolean isRecord;
    boolean isPlayback;
    private static String recordedFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_side);
        isRecord = false;
        isMusicPlaying = false;
        isPlayback = false;
        backButton = (Button) findViewById(R.id.backButton);
        soundButton = (Button) findViewById(R.id.soundButton);
        inputButton = (Button) findViewById(R.id.inputButton);
        playbackButton = (Button) findViewById(R.id.playbackButton);
        chatButton = (Button) findViewById(R.id.chatButton);

        recordedFile = Environment.getExternalStorageDirectory().getAbsolutePath();
        recordedFile += "/recorderFile.3gp";
        sounder = MediaPlayer.create(this, R.raw.ares);

        //Setup audio recorder using devices built in mic


        chatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

            if(isMusicPlaying)
            {
                //Interupt music with call
                sounderMusic.pause();
                sounderCall = MediaPlayer.create(SideActivity.this, R.raw.somekindasong);
                sounderCall.start();
                isMusicPlaying = false;
            }
            else
            {
                if(sounderCall != null)
                {
                    //end call
                    closeSounderCall();
                }
                if(sounderMusic == null) {
                    //start the music
                    sounderMusic = MediaPlayer.create(SideActivity.this, R.raw.ares);
                    sounderMusic.setLooping(true);
                    sounderMusic.start();
                    isMusicPlaying = true;
                }
                else
                {
                    //resume music
                    sounderMusic.start();
                    isMusicPlaying = true;
                }
            }


            }
        });



        //playback recorded file when hit
        playbackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(!isRecord)
                {
                    try
                    {
                        sounder2 = new MediaPlayer();
                        sounder2.setDataSource(recordedFile);
                        sounder2.prepare();
                        sounder2.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                sounder2.release();
                                sounder2 = null;
                            }
                        });
                        sounder2.start();

                    } catch (IOException e) {

                    }
                }


            }
        });

        //records user voice when clicked, then stops recording when clicked again.
        inputButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                    if(sounder2 != null)
                    {
                        sounder2.stop();
                        sounder2.release();
                        sounder2 = null;
                    }

                    try {
                        if(!isRecord)
                        {
                            isRecord = true;
                            recorder = new MediaRecorder();
                            recorder.setAudioSource(MediaRecorder.AudioSource.MIC); //Note: requires permission to use audio device(see manifest).
                            recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                            recorder.setOutputFile(recordedFile);
                            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                            recorder.prepare();//could throw ioexception.
                            recorder.start();
                        }
                        else
                        {
                            isRecord = false;
                            recorder.stop();
                            recorder.release();
                            recorder = null;
                        }
                }
                catch(Exception e)
                {
                    //add some catch here.
                }
            }
        });

        //stops the sound playing and closes the activity(goes back to main activity)
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sounder.stop();
                if(sounder2 != null)
                {
                    sounder2.stop();
                    sounder2.release();
                    sounder2 = null;
                }


                closeSounderMusic();

                closeSounderCall();


                finish();
            }
        });

        //plays a sound when the sound button is clicked
        soundButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sounder.start();
            }
        });
    }

    public void onPause()
    {
        super.onPause();
        isMusicPlaying = false;
        //*Audio Player cleanup
        sounder.stop();
        sounder.release();

        if(sounder2 != null)
        {
            sounder2.stop();
            sounder2.release();
            sounder2 = null;
        }
        closeSounderMusic();

        closeSounderCall();


    }

    private void closeSounderCall()
    {
        if(sounderCall != null)
        {
            sounderCall.stop();
            sounderCall.release();
            sounderCall = null;
        }
    }

    private void closeSounderMusic()
    {
        if(sounderMusic != null)
        {
            sounderMusic.stop();
            sounderMusic.release();
            sounderMusic = null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_side, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
