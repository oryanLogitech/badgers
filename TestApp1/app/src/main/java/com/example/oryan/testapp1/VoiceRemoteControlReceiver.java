package com.example.oryan.testapp1;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;
import android.widget.Toast;

/**
 * Created by oryan on 16/01/2015.
 */
public class VoiceRemoteControlReceiver extends BroadcastReceiver{

    //private boolean doubleUp;


    public VoiceRemoteControlReceiver()
    {
        super();
        //doubleUp = false;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "Intent Detected. hi i'm voice", Toast.LENGTH_LONG).show();
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
                        || KeyEvent.KEYCODE_MEDIA_STOP == event.getKeyCode()
                        //|| KeyEvent.KEYCODE_MEDIA_PREVIOUS == event.getKeyCode()
                        //|| KeyEvent.KEYCODE_CALL == event.getKeyCode()
                                                                        )
                {
                    //Toast.makeText(context, "Intent Detected.", Toast.LENGTH_SHORT).show();
                    Intent speak = new Intent();
                    if(event.getAction() == KeyEvent.ACTION_DOWN)
                    {
                        Toast.makeText(context, "this sandwich", Toast.LENGTH_SHORT).show();
                        speak.setAction("myCustomDown");
                        context.sendBroadcast(speak);
                    }
                    else if(event.getAction() == KeyEvent.ACTION_UP)
                    {
                        Toast.makeText(context, "sandwich UP, sandwich UP", Toast.LENGTH_SHORT).show();
                        speak.setAction("myCustomUp");
                        context.sendBroadcast(speak);
                    }
                }
        }

            //doubleUp = true;
        //}
    }
}
