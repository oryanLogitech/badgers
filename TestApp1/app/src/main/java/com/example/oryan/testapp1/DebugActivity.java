package com.example.oryan.testapp1;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Set;


public class DebugActivity extends ActionBarActivity
{

    private Button backButton;
    private Button recogButton;
    private TextView debugResponseBox;
    private BluetoothAdapter debugAdapter;
    private BluetoothHeadset debugHeadset;
    private BluetoothDevice debugDevice;
    private BroadcastReceiver btStateReceiver;
    private String responseLog;
    private BroadcastReceiver debugReceiver1;
    private BroadcastReceiver debugReceiver2;
    private ArrayAdapter<String> debugArrayAdapter;
    private Boolean isRecoging;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug);

        responseLog = "";
        recogButton = (Button) findViewById(R.id.recogButton);
        backButton = (Button) findViewById(R.id.backButton);
        debugResponseBox = (TextView) findViewById(R.id.debugResponseBox);
        isRecoging = false;

        backButton.setOnClickListener(new View.OnClickListener()
        {
        @Override
        public void onClick(View v)
        {
            finish();
        }
        });

        debugAdapter = BluetoothAdapter.getDefaultAdapter();
        if(debugAdapter == null)
        {
            toaster("Bluetooth adapter not found");
        }
        else
        {
            toaster("bluetooth adapter found");
        }

        if (!debugAdapter.isEnabled()) {
            toaster("bluetooth not enabled");
        }
        else
        {
            toaster("bluetooth already enabled");
        }

        Set<BluetoothDevice> pairedDevices = debugAdapter.getBondedDevices();
        // If there are paired devices
        if (pairedDevices.size() > 0)
        {
            toaster("Got Paired Devices: ");
            // Loop through paired devices
            for (BluetoothDevice device : pairedDevices)
            {
                // Add the name and address to an array adapter to show in a ListView
               toaster(device.getName() + " : " + device.getAddress());
               if(device.getName().equalsIgnoreCase("Powerbeats Wireless"))
               {
                   toaster("I FOUND IT");
                   debugDevice = device;
               }
            }
        }


        BluetoothProfile.ServiceListener mProfileListener = new BluetoothProfile.ServiceListener() {
            public void onServiceConnected(int profile, BluetoothProfile proxy)
            {
                if (profile == BluetoothProfile.HEADSET)
                {
                    debugHeadset = (BluetoothHeadset) proxy;
                    toaster("Headset profile loaded");
                }
            }
            public void onServiceDisconnected(int profile)
            {
                if (profile == BluetoothProfile.HEADSET) {
                    debugHeadset = null;
                    toaster("headset profile unloaded");
                }
            }
        };
        debugAdapter.getProfileProxy(DebugActivity.this, mProfileListener, BluetoothProfile.HEADSET);

        btStateReceiver = new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context context, Intent intent)
            {
                toaster("Bluetooth State Changed");
            }
        };
        registerReceiver(btStateReceiver, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));

        debugReceiver1 = new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context context, Intent intent)
            {
                toaster("Bluetooth Connection State Changed");
            }
        };
        registerReceiver(debugReceiver1, new IntentFilter(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED));

        debugReceiver2 = new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context context, Intent intent)
            {
                toaster("Bluetooth Intent Recieved");
            }
        };
        registerReceiver(debugReceiver2, new IntentFilter(BluetoothHeadset.ACTION_VENDOR_SPECIFIC_HEADSET_EVENT));
    }

    private void toaster(String stringToToast)
    {
        Toast.makeText(DebugActivity.this, stringToToast, Toast.LENGTH_SHORT).show();
        responseLog += stringToToast + "\n";
        debugResponseBox.setText(responseLog);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        debugAdapter.closeProfileProxy(BluetoothProfile.HEADSET, debugHeadset);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_debug, menu);
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
}
