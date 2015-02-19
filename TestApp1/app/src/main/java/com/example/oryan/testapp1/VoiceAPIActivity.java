package com.example.oryan.testapp1;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


public class VoiceAPIActivity extends ActionBarActivity
{

    private Button chatButton;
    private Button backButton;
    private Button playbackButton;
    private MediaRecorder SRrecorder;
    private MediaPlayer SRplayer;
    private String callFile;

    private AudioManager am;
    private BroadcastReceiver receiver;
    private BroadcastReceiver receiver2;
    private boolean doubleup;
    private boolean ison;
    private TextView feedbackBox;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_api);

        SRrecorder = null;
        SRplayer = null;

        feedbackBox = (TextView)findViewById(R.id.feedbackBox);

        doubleup = true;
        ison = false;

        callFile = Environment.getExternalStorageDirectory().getAbsolutePath();
        callFile += "/callFiley.3gp";

        playbackButton = (Button) findViewById(R.id.playbackButton);
        playbackButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                chatPlayback();
            }
        });

        chatButton = (Button) findViewById(R.id.chatButton);
        chatButton.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                if(event.getAction() == MotionEvent.ACTION_DOWN)
                {
                    chatStart();
                }
                else if(event.getAction() == MotionEvent.ACTION_UP)
                {
                    Log.v("yarf", "fksajdfklsdjitjiortnvnnnn");
                    chatStop();
                }
                return false;
            }
        });

        am = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        am.registerMediaButtonEventReceiver(new ComponentName(this, VoiceRemoteControlReceiver.class));

        receiver = new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context context, Intent intent)
            {
                feedbackBox.setText("got something");
                if(!ison && intent.getAction().equals("myCustomDown"))
                {
                    if(doubleup)
                    {
                        doubleup = false;
                        ison = true;
                        chatStart();
                    }
                    else
                    {
                        doubleup = true;
                    }
                }

            }
        };

        receiver2 = new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context context, Intent intent)
            {
                //feedbackBox.setText("got something");
                if (ison && intent.getAction().equals("myCustomUp"))
                {
                    if (doubleup)
                    {
                        doubleup = false;
                        ison = false;
                        chatStop();
                    } else
                    {
                        doubleup = true;
                    }
                }
            }
        };

        this.registerReceiver(receiver2, new IntentFilter("myCustomUp"));//opens the listener for the Next button
        this.registerReceiver(receiver, new IntentFilter("myCustomDown"));

        backButton = (Button) findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });
    }


    @Override
    protected void onPause()
    {
        super.onPause();

    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        chatPlaybackStop();
        unregisterReceiver(receiver);
        am.unregisterMediaButtonEventReceiver(new ComponentName(this, VoiceRemoteControlReceiver.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_voice_api, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void chatStart()
    {
        try
        {
            SRrecorder = new MediaRecorder();
            SRrecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            SRrecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            SRrecorder.setOutputFile(callFile);
            SRrecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            SRrecorder.prepare();
            SRrecorder.start();
            feedbackBox.setText("yams away");
            Log.v("fjhsdkjfhsadkjffgetwzxbfagrf","PISS");
        }
        catch (IOException e)
        {

        }

    }

    private void chatStop()
    {
        SRrecorder.stop();
        SRrecorder.release();
        SRrecorder = null;
        feedbackBox.setText("out of yams");
        chatSend();
        Log.v("yarf", "vnnnn");
        //chatPlayback();
    }

    private void chatSend()
    {
        Intent sendMedia = new Intent();
        sendMedia.setAction(Intent.ACTION_SEND);
        sendMedia.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(callFile)));
        sendMedia.setType("audio/3gp");
        //sendMedia.putExtra(Intent.EXTRA_TEXT, "Ok that works");
        //sendMedia.setType("text/plain");
        sendMedia.setPackage("com.whatsapp");
        startActivity(sendMedia);
        feedbackBox.setText("more yams requested");
        Log.v("narf", "it a done sent");
    }

    private void chatPlayback()
    {
        SRplayer = new MediaPlayer();
        try
        {
            SRplayer.setDataSource(callFile);
            SRplayer.prepare();
            SRplayer.start();
            Log.v("yarf", "knffjgkasdok");
        }
        catch (IOException e)
        {
            Log.v("yarf", "error nvnnnn");
            SRplayer.release();
        }
        SRplayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener()
        {
            @Override
            public void onCompletion(MediaPlayer mp)
            {
                SRplayer.release();
            }
        });
    }

    private void chatPlaybackStop()
    {
        if(SRplayer != null)
        {
            SRplayer.stop();
            SRplayer.release();
        }
    }

}
