package com.example.oryan.testapp1;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothAssignedNumbers;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.Toast;

import java.util.Set;

/**
 * Created by oryan on 16/01/2015.
 */
public class RemoteControlReceiver2 extends BroadcastReceiver{

    private boolean doubleUp;
    private android.os.Handler toaster;

    public RemoteControlReceiver2()
    {
        super();
        doubleUp = false;
        toaster = new android.os.Handler();
    }

    @Override
    public void onReceive(Context context, Intent intent)
    {
        toastToaster("Intent Detected. hi i'm blue");
        if (doubleUp)
        {
            doubleUp = false;
        }
        else
        {
            Toast.makeText(context, "Intent Detected. hi i'm blue", Toast.LENGTH_SHORT).show();
            if (BluetoothHeadset.ACTION_VENDOR_SPECIFIC_HEADSET_EVENT.equals(intent.getAction()))
            {
                Toast.makeText(context, "Vendor-order detected", Toast.LENGTH_SHORT).show();
                Bundle btExtras = intent.getExtras();
                Set<String> btCats = intent.getCategories();

                //Handle Plantronics headsets
                if (btCats.contains(BluetoothAssignedNumbers.JAWBONE))
                {
                    Toast.makeText(context, "blue its a jaw", Toast.LENGTH_SHORT).show();
                    Object[] args = (Object[]) btExtras.get(BluetoothHeadset.EXTRA_VENDOR_SPECIFIC_HEADSET_EVENT_ARGS);

                    if (args != null)
                    {
                        String btEvent = (String) args[0];
                        if (btEvent.equals("BUTTON"))
                        {
                            Integer btType = (Integer) args[1];
                            Toast.makeText(context, "blue button", Toast.LENGTH_SHORT).show();

                        }
                        else if (btEvent.equals("DOFF"))
                        {
                            Toast.makeText(context, "blue off", Toast.LENGTH_SHORT).show();
                        }
                        else if (btEvent.equals("DON"))
                        {
                            // Re-enable things if not already running
                            Toast.makeText(context, "blue on", Toast.LENGTH_SHORT).show();
                        }
                        else if (btEvent.equals("AUDIO"))
                        {
                            Toast.makeText(context, "blue audio", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
            doubleUp = true;
        }
    }

    //+++++Debug Method used to display popups from the service+++++
    private void toastToaster(final String toastText)
    {
        /*toaster.post(new Runnable()
        {
            @Override
            public void run()
            {
                Toast.makeText(Blue2Activity.blueOurContext, toastText, Toast.LENGTH_SHORT).show();
            }
        });*/
    }
}
