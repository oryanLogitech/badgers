package com.example.oryan.testapp1;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;
import android.widget.Toast;

/**
 * Created by oryan on 16/01/2015.
 */
public class SpeechRemoteControlReceiver extends BroadcastReceiver{

    //private boolean doubleUp;


    public SpeechRemoteControlReceiver()
    {
        super();
        //doubleUp = false;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        //Toast.makeText(context, "Intent Detected. hi i'm SRRemote", Toast.LENGTH_LONG).show();
        /*if(doubleUp)
        {
            doubleUp = false;
        }*/
       // else {
        if (Intent.ACTION_MEDIA_BUTTON.equals(intent.getAction()))
        {
                KeyEvent event = (KeyEvent) intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
                //Toast.makeText(context, "this " + event.getKeyCode(), Toast.LENGTH_SHORT).show();
                if (//KeyEvent.KEYCODE_VOLUME_DOWN == event.getKeyCode()
                        KeyEvent.KEYCODE_MEDIA_PLAY == event.getKeyCode()
                        || KeyEvent.KEYCODE_MEDIA_PAUSE == event.getKeyCode()
                        //|| KeyEvent.KEYCODE_MEDIA_NEXT == event.getKeyCode()
                        ||KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE == event.getKeyCode()
                        //|| KeyEvent.KEYCODE_MEDIA_STOP == event.getKeyCode()
                        //|| KeyEvent.KEYCODE_MEDIA_PREVIOUS == event.getKeyCode()
                        //|| KeyEvent.KEYCODE_CALL == event.getKeyCode()
                                                                        )
                {
                    //Toast.makeText(context, "Intent Detected.", Toast.LENGTH_SHORT).show();
                    Intent speak = new Intent();
                    speak.setAction("myCustom");
                    context.sendBroadcast(speak);
                }
        }

            //doubleUp = true;
        //}
    }
}
