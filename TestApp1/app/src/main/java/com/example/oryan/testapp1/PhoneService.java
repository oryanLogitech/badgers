package com.example.oryan.testapp1;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.provider.MediaStore;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

public class PhoneService extends Service
{

    private AudioManager am;

    public static boolean phoneRunning;

    private TelephonyManager tm;
    private PhoneListener pListener;
    private BroadcastReceiver receiver1;
    private BroadcastReceiver receiver2;
    private boolean isPhoning;
    private boolean doubleUp2;
    private android.os.Handler toaster;

    @Override
    public void onCreate()
    {
        super.onCreate();

        phoneRunning = true;
        toaster = new android.os.Handler();

        am = (AudioManager) getSystemService(AUDIO_SERVICE);

        toastToaster("sandfksdhgjderioiiiii");

        tm = (TelephonyManager) PhoneService.this.getSystemService(this.TELEPHONY_SERVICE);
        pListener = new PhoneListener();
        tm.listen(pListener, PhoneStateListener.LISTEN_CALL_STATE);
        doubleUp2 = false;

        am.registerMediaButtonEventReceiver(new ComponentName(this, RemoteControlReceiver.class));

        receiver1 = new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context context, Intent intent)
            {
                if(intent.getAction().matches("myCustomMusic"))
                {
                    if(isPhoning)
                    {
                        toastToaster("i am answerererer");
                    }
                    else
                    {
                        toastToaster("nothing to answer");
                    }
                }
            }

        };
        this.registerReceiver(receiver1, new IntentFilter("myCustomMusic"));

        receiver2 = new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context context, Intent intent)
            {
                //feedbackBox.setText("got em");
                //+++++++++++++++++++++++++++DoubleUp is work around to bug where the intent is sent twice by the broadcaster.
                //This bug is present in certain android versions and setups, as far as i can tell there is no know cause for the bug.
               toastToaster("got em phones");



                if (doubleUp2)
                {
                    if (intent.getAction().equals("myCustomPhone"))
                    //++++++++++Danger: myCustom needs to be set to detect a Next Button press(I've set it to detect the play/pause button press for testing purposes)
                    {
                       isPhoning = true;
                       toastToaster("phone call discovered!");
                    }
                    else if(intent.getAction().equals("myCustomPhoneDone"))
                    {
                        isPhoning = false;
                        toastToaster("phone call ended");
                    }
                    doubleUp2 = false;
                }
                else
                {

                    doubleUp2 = true;
                }
            }
        };
        this.registerReceiver(receiver2, new IntentFilter("myCustomPhone"));//opens the listener for the Next button
        toastToaster("Phone service started");
    }

    //+++++Method required by Service interface: serves no purpose here so it returns null+++++
    @Override
    public IBinder onBind(Intent intent) { //Not used
        return null;
    }

    //+++++Cleans up all objects and releases resources when Service is ended
    @Override
    public void onDestroy()
    {
        am.unregisterMediaButtonEventReceiver(new ComponentName(this, RemoteControlReceiver.class));//shuts down the listener for the next button
        am.unregisterMediaButtonEventReceiver(new ComponentName(this, MainActivity.class));
        phoneRunning = false;
        tm.listen(pListener, PhoneStateListener.LISTEN_NONE);
        toastToaster("phone service stopped");
        super.onDestroy();

    }

    //+++++Listener that listens for gaining/lossing the audio focus and plays/pauses music when this happens.+++++










    //+++++Debug Method used to display popups from the service+++++
    private void toastToaster(final String toastText)
    {
        toaster.post(new Runnable()
        {
            @Override
            public void run()
            {
                Toast.makeText(getApplicationContext(), toastText, Toast.LENGTH_SHORT).show();
            }
        });
    }


}
