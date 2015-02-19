package com.example.oryan.testapp1;

import android.app.Service;
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
import android.net.Uri;
import android.os.IBinder;
import android.provider.MediaStore;

import java.util.ArrayList;

public class BlueService extends Service
{

    private AudioManager am;
    private BroadcastReceiver receiver;
    private MediaPlayer sounderMusic;
    private Boolean isMusicPaused;
    private Boolean doubleUp;
    private int count;
    private int[] songs;
    private int songIndex;
    private ArrayList<Uri> musicList;


    @Override
    public void onCreate()
    {
        super.onCreate();

        ContentResolver musicResolver = getContentResolver();
        musicList = new ArrayList<Uri>();
        ContentResolver contentResolver = getContentResolver();


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

        count = 0;
        doubleUp = false;
        isMusicPaused = false;


        //++Note to self: consider more responible audio focus request system, re: request and realease on play and pause.
        am = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        int result = am.requestAudioFocus(afChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED)
        {
            am.registerMediaButtonEventReceiver(new ComponentName(this, RemoteControlReceiver.class)); //Sets up the reciever listener

            receiver = new BroadcastReceiver() //Creating the Listener for the Next Button
            {

                @Override
                public void onReceive(Context context, Intent intent) {
                    //+++++++++++++++++++++++++++DoubleUp is work around to bug where the intent is sent twice by the broadcaster.
                    //This bug is present in certain android versions and setups, as far as i can tell there is no know cause for the bug.
                    if (doubleUp) {
                        if (intent.getAction().equals("myCustomMusic"))
                        //++++++++++Danger: myCustom needs to be set to detect a Next Button press(I've set it to detect the play/pause button press for testing purposes)
                        {

                            musicButtonLogic();
                            count++;
                        }
                        doubleUp = false;
                    } else {
                        doubleUp = true;
                    }
                }
            };
            this.registerReceiver(receiver, new IntentFilter("myCustomMusic"));//opens the listener for the Next button
        }
    }

    @Override
    public IBinder onBind(Intent intent) { //Not used
        return null;
    }

    @Override
    public void onDestroy()
    {
        sounderMusic.release();//Frees up system resources
        am.abandonAudioFocus(afChangeListener);
        am.unregisterMediaButtonEventReceiver(new ComponentName(this, RemoteControlReceiver.class));//shuts down the listener for the next button
        super.onDestroy();

    }


    private AudioManager.OnAudioFocusChangeListener afChangeListener = new AudioManager.OnAudioFocusChangeListener()
    {
        public void onAudioFocusChange(int focusChange)
        {
            if(focusChange == AudioManager.AUDIOFOCUS_LOSS)
            {
                if(sounderMusic != null)
                {
                    sounderMusic.pause();
                }
            }
            else
            {
                if(sounderMusic != null)
                {
                    sounderMusic.start();
                }
            }
        }
    };

    private void musicButtonLogic()
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


            if (songIndex < musicList.size() - 1) {
                songIndex++;
            } else {
                songIndex = 0;
            }

            sounderMusic = MediaPlayer.create(BlueService.this, musicList.get(songIndex));
            //sounderMusic.setVolume(masterVolume, masterVolume);
            sounderMusic.setLooping(false);
            sounderMusic.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    musicButtonLogic();
                }
            });
            sounderMusic.start();
            isMusicPaused = false;
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

}
