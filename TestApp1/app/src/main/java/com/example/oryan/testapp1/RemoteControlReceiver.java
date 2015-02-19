package com.example.oryan.testapp1;

import android.bluetooth.BluetoothHeadset;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;
import android.widget.Toast;

/**
 * Created by oryan on 16/01/2015.
 */
public class RemoteControlReceiver extends BroadcastReceiver{

    private boolean doubleUp;


    public RemoteControlReceiver()
    {
        super();
        doubleUp = false;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if(doubleUp)
        {
            doubleUp = false;
        }
        else {
            if (Intent.ACTION_MEDIA_BUTTON.equals(intent.getAction())) {
                KeyEvent event = (KeyEvent) intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
                if (KeyEvent.KEYCODE_MEDIA_PLAY == event.getKeyCode() || KeyEvent.KEYCODE_MEDIA_PAUSE == event.getKeyCode()) {
                    //Toast.makeText(context, "Intent Detected.", Toast.LENGTH_LONG).show();
                    Intent music = new Intent();
                    music.setAction("myCustomMusic");
                    context.sendBroadcast(music);

                }
            }

            doubleUp = true;
        }
    }
}
