package com.example.oryan.testapp1;

import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewDebug;
import android.widget.Button;

import java.io.IOException;


public class MockupActivity extends ActionBarActivity {

    Button backButton;
    Button musicButton;
    Button chatButton;
    Button volumeUpButton;
    Button volumeDownButton;
    Button playbackButton;
    MediaPlayer sounderChat;
    MediaPlayer sounderMusic;
    MediaRecorder recordCall;
    MediaPlayer sounderPlayback;
    boolean isMusicPaused;
    String callFile;
    float masterVolume;
    int[] songs;
    int songIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //-------Temporary Song store
        songIndex = 0;
        songs = new int[2];
        songs[0] = R.raw.somekindasong;
        songs[1] = R.raw.ares;
        //-------Temporary Song Store

        masterVolume = 0.5f;
        setContentView(R.layout.activity_mockup);
        playbackButton = (Button) findViewById(R.id.playbackButton);
        playbackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sounderPlayback == null)
                {
                    sounderPlayback = new MediaPlayer();
                    sounderPlayback.setVolume(masterVolume,masterVolume);
                    try {
                        sounderPlayback.setDataSource(callFile);
                        sounderPlayback.prepare();
                        sounderPlayback.start();
                    }
                    catch (IOException e)
                    {

                    }
                }
                else
                {
                    sounderPlayback.stop();
                    sounderPlayback.release();
                    sounderPlayback = null;
                }
            }
        });

        callFile = Environment.getExternalStorageDirectory().getAbsolutePath();
        callFile += "/callFile.3gp";

        backButton = (Button) findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        chatButton = (Button) findViewById(R.id.chatButton);
        chatButton.setBackgroundColor(Color.TRANSPARENT);
        chatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sounderMusic != null)
                {
                    if(sounderMusic.isPlaying())
                    {
                        sounderMusic.pause();
                        isMusicPaused = true;
                    }

                }

                if(sounderChat == null)
                {
                    sounderChat = MediaPlayer.create(MockupActivity.this, R.raw.somekindasong);
                    sounderChat.setVolume(masterVolume,masterVolume);
                    sounderChat.start();
                    recordCall = new MediaRecorder();
                    recordCall.setAudioSource(MediaRecorder.AudioSource.MIC);
                    recordCall.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                    recordCall.setOutputFile(callFile);
                    recordCall.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                    try {
                        recordCall.prepare();
                    } catch (IOException e) {

                    }
                    recordCall.start();
                }
                else
                {
                    sounderChatClose();
                }
            }
        });

        chatButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(sounderMusic != null)
                {
                    if(sounderMusic.isPlaying())
                    {
                        sounderMusic.pause();
                        isMusicPaused = true;
                    }
                }

                if(sounderChat == null)
                {
                    sounderChat = MediaPlayer.create(MockupActivity.this, R.raw.ares);
                    sounderChat.setVolume(masterVolume,masterVolume);
                    sounderChat.start();
                    recordCall = new MediaRecorder();
                    recordCall.setAudioSource(MediaRecorder.AudioSource.MIC);
                    recordCall.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                    recordCall.setOutputFile(callFile);
                    recordCall.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                    try {
                        recordCall.prepare();
                    } catch (IOException e) {

                    }
                    recordCall.start();
                }
                else
                {
                    sounderChatClose();
                }
                return true;//determines whether or not click is consumed(long tap for true, hold for false)
            }
        });

        volumeUpButton = (Button) findViewById(R.id.volumeUpButton);
        volumeDownButton = (Button) findViewById(R.id.volumeDownButton);
        volumeUpButton.setBackgroundColor(Color.TRANSPARENT);
        volumeDownButton.setBackgroundColor(Color.TRANSPARENT);
        volumeUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (masterVolume < 1.0) {
                    masterVolume += 0.1;
                    changeVolumes(masterVolume);
                }
            }
        });
        volumeDownButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                  if(masterVolume >= 0.1)
                  {
                      masterVolume -= 0.1;
                      changeVolumes(masterVolume);
                  }
            }
        });

        musicButton = (Button) findViewById(R.id.musicButton);
        musicButton.setBackgroundColor(Color.TRANSPARENT);
        musicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sounderChat == null)
                {
                        musicButtonLogic();

                }
                else
                {
                    if(sounderChat.isPlaying())
                    {
                        sounderChatClose();
                        if(sounderMusic != null)
                        {
                            sounderMusic.start();
                            isMusicPaused = false;
                        }
                    }
                    else
                    {
                        musicButtonLogic();
                    }

                }
            }
        });
    }

    protected void musicButtonLogic()
    {
        //next
        if(isMusicPaused)
        {
            sounderMusic.start();
            isMusicPaused = false;
        }
        else
        {
            sounderMusicClose();


            if (songIndex < songs.length - 1) {
                songIndex++;
            } else {
                songIndex = 0;
            }

            sounderMusic = MediaPlayer.create(MockupActivity.this, songs[songIndex]);
            sounderMusic.setVolume(masterVolume, masterVolume);
            sounderMusic.setLooping(true);
            sounderMusic.start();
            isMusicPaused = false;
        }

    }

    protected void sounderMusicClose()
    {
        if(sounderMusic != null)
        {
            sounderMusic.stop();
            sounderMusic.release();
            sounderMusic = null;
        }
    }

    protected void sounderChatClose()
    {
        if(sounderChat != null)
        {
            sounderChat.stop();
            sounderChat.release();
            sounderChat = null;
            recordCall.stop();
            recordCall.release();
        }
    }

    protected void changeVolumes(float inVolume)
    {
       if(sounderChat != null)
        {
            sounderChat.setVolume(inVolume, inVolume);
        }
        if(sounderMusic != null)
        {
            sounderMusic.setVolume(inVolume, inVolume);
        }
        if(sounderPlayback != null)
        {
            sounderPlayback.setVolume(inVolume, inVolume);
        }
    }

    protected void onPause()
    {
        super.onPause();
        sounderMusicClose();
        sounderChatClose();
        if(sounderPlayback != null)
        {
            sounderPlayback.stop();
            sounderPlayback.release();
            sounderPlayback = null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_mockup, menu);
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
