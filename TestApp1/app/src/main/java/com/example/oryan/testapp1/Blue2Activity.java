package com.example.oryan.testapp1;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothAssignedNumbers;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;


public class Blue2Activity extends ActionBarActivity {

    Button backButton;
    Button playbackButton;
    Button recordButton;
    Button musicButton;
    TextView feedbackBox;
    BluetoothAdapter blueAdapter;
    BluetoothHeadset blueHS;
    MediaPlayer playbackSounder;
    MediaPlayer sounderMusic;
    MediaRecorder recorder;
    String saveFile;
    Boolean isRecording;
    Boolean isPlayingBack;
    Boolean isMusicPlaying;
    BroadcastReceiver blueReciever;
    AudioManager am;
    Boolean doubledUp;
    Switch modeSwitch;
    private int songIndex;
    private ArrayList<Uri> musicList;

    private BroadcastReceiver blueRecieverthing;
    public static Context blueOurContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blue2);

        blueOurContext = Blue2Activity.this;
        final ContentResolver musicResolver = getContentResolver();
        musicList = new ArrayList<Uri>();
        ContentResolver contentResolver = getContentResolver();

        modeSwitch = (Switch) findViewById(R.id.modeSwitch);

        Uri uri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
        Cursor cursor = contentResolver.query(uri, null, selection, null, null);
        if (cursor == null) {
            // query failed, handle error.
        } else if (!cursor.moveToFirst()) {
            // no media on the device
        } else {
            int titleColumn = cursor.getColumnIndex(android.provider.MediaStore.Audio.Media.TITLE);
            int idColumn = cursor.getColumnIndex(android.provider.MediaStore.Audio.Media._ID);
            int thing =  cursor.getColumnIndex(MediaStore.Audio.Media.DATA);
            do {
                long thisId = cursor.getLong(idColumn);
                String thisTitle = cursor.getString(titleColumn);
                musicList.add(ContentUris.withAppendedId(android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, thisId));
            } while (cursor.moveToNext());
        }


        am = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        backButton = (Button) findViewById(R.id.backButton);
        musicButton = (Button) findViewById(R.id.musicButton);
        musicButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                musicButtonLogic();
            }
        });
        isPlayingBack = false;
        isMusicPlaying = false;
        isRecording = false;
        doubledUp = false;
        recordButton = (Button) findViewById(R.id.recordButton);
        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isRecording)
                {
                    stopRecorder2();
                }
                else
                {
                    startRecorder2();
                }
            }
        });
        playbackButton = (Button) findViewById(R.id.playbackButton);
        playbackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPlayingBack)
                {
                    stopPlayback();
                }
                else
                {
                    sounderMusicClose();
                    startPlayback();
                }
            }
        });

        am.registerMediaButtonEventReceiver(new ComponentName(this, Speech2ControlReceiver.class));
        //am.registerMediaButtonEventReceiver(new ComponentName(this, RemoteControlReceiver2.class));
        //this.registerReceiver(new RemoteControlReceiver2(), new IntentFilter(BluetoothHeadset.ACTION_VENDOR_SPECIFIC_HEADSET_EVENT));
        blueReciever = new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context context, Intent intent)
            {
                if(!doubledUp)
                {
                    feedbackBox.setText("recieved");
                    if (intent.getAction().equals("myCustom2"))
                    {
                        if (modeSwitch.isChecked())
                        {
                            musicButtonLogic();
                        }
                        else
                        {
                            sounderMusicClose();
                            if (isRecording)
                            {
                                stopRecorder2();
                            }
                            else
                            {
                                startRecorder2();
                            }
                        }
                    }
                    doubledUp = true;
                }
                else
                {
                    doubledUp = false;
                }
            }
        };
        this.registerReceiver(blueReciever, new IntentFilter("myCustom2"));//opens the listener for the Next button

        feedbackBox = (TextView) findViewById(R.id.feedbackBox);
        backButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        blueAdapter = BluetoothAdapter.getDefaultAdapter();
        blueAdapter.getProfileProxy(this, blueProfileListener, BluetoothProfile.HEADSET);

        saveFile = Environment.getExternalStorageDirectory().getAbsolutePath();
        saveFile += "/saveFile.3gp";



        blueRecieverthing = new RemoteControlReceiver2();
        IntentFilter iFilter = new IntentFilter();
        iFilter.addCategory( BluetoothHeadset.VENDOR_SPECIFIC_HEADSET_EVENT_COMPANY_ID_CATEGORY + "." + BluetoothAssignedNumbers.JAWBONE);
        iFilter.addAction(BluetoothHeadset.ACTION_VENDOR_SPECIFIC_HEADSET_EVENT);
        registerReceiver(blueRecieverthing, iFilter);

    }

    private void musicButtonLogic()
    {
        //next
        if(isMusicPlaying)
        {
            sounderMusic.start();
            isMusicPlaying = false;
        }
        else
        {
            sounderMusicClose();


            if (songIndex < musicList.size() - 1) {
                songIndex++;
            } else {
                songIndex = 0;
            }

            sounderMusic = MediaPlayer.create(Blue2Activity.this, musicList.get(songIndex));
            //sounderMusic.setVolume(masterVolume, masterVolume);
            sounderMusic.setLooping(false);
            sounderMusic.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    musicButtonLogic();
                }
            });
            sounderMusic.start();
            isMusicPlaying = false;
        }

    }

    private void sounderMusicClose()
    {
        if(sounderMusic != null)
        {
            sounderMusic.stop();
            sounderMusic.release();
            sounderMusic = null;
        }
    }


    private void startPlayback()
    {
        try
        {
            playbackSounder = new MediaPlayer();
            playbackSounder.setDataSource(saveFile);
            playbackSounder.prepare();
            playbackSounder.start();
            isPlayingBack = true;
            feedbackBox.setText("Playing back");

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void stopPlayback() {
        if (playbackSounder != null)
        {
            playbackSounder.stop();
            playbackSounder.release();
            isPlayingBack = false;
            playbackSounder = null;
            feedbackBox.setText("stopped playing back");
        }
    }

    private void startRecorder2()
    {
        try
        {
            if(recorder == null)
            {
                am.startBluetoothSco();
                recorder = new MediaRecorder();
                recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                recorder.setOutputFile(saveFile);
                recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                recorder.prepare();
                recorder.start();
                isRecording = true;
                feedbackBox.setText("started recording");
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void stopRecorder2()
    {
        if(recorder != null)
        {
            recorder.stop();
            am.stopBluetoothSco();
            feedbackBox.setText("recorder stopped");
            recorder.release();
            isRecording = false;
            recorder = null;
        }
    }



    private BluetoothProfile.ServiceListener blueProfileListener = new BluetoothProfile.ServiceListener() {
        @Override
        public void onServiceConnected(int profile, BluetoothProfile proxy)
        {
            if(profile == BluetoothProfile.HEADSET)
            {
                blueHS = (BluetoothHeadset)proxy;
            }
        }

        @Override
        public void onServiceDisconnected(int profile)
        {
            if(profile == BluetoothProfile.HEADSET)
            {
                blueHS = null;
            }
        }
    };

    @Override
    protected void onPause()
    {
        super.onPause();
        sounderMusicClose();
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopPlayback();
        stopRecorder2();
        am.unregisterMediaButtonEventReceiver(new ComponentName(this, Speech2ControlReceiver.class));
        blueAdapter.closeProfileProxy(BluetoothProfile.HEADSET, blueHS);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_blue2, menu);
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
