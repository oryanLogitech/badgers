package com.example.oryan.testapp1;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;
import android.widget.Toast;

/**
 * Created by oryan on 23/01/2015.
 */
public class Speech2ControlReceiver extends BroadcastReceiver
{

    public Speech2ControlReceiver()
    {
        super();
    }

    @Override
    public void onReceive(Context context, Intent intent)
    {
        if (Intent.ACTION_MEDIA_BUTTON.equals(intent.getAction())) {
            KeyEvent event = (KeyEvent) intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
            Toast.makeText(context, "this " + event.getKeyCode(), Toast.LENGTH_SHORT).show();
            if (KeyEvent.KEYCODE_MEDIA_PLAY == event.getKeyCode()
                || KeyEvent.KEYCODE_MEDIA_PAUSE == event.getKeyCode()
                ||KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE == event.getKeyCode())
            {
                //Toast.makeText(context, "Intent Detected.", Toast.LENGTH_SHORT).show();
                Intent speak = new Intent();
                speak.setAction("myCustom2");
                context.sendBroadcast(speak);
            }
        }
    }
}
