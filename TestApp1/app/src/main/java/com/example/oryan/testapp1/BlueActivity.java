package com.example.oryan.testapp1;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
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
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Handler;
import java.util.logging.LogRecord;


public class BlueActivity extends ActionBarActivity {

    private TextView feedbackText;
    private Button backButton;
    //BluetoothAdapter blueAdapter;
    //BluetoothDevice blueDevice;
    //Set<BluetoothDevice> bondDevices;
    //BluetoothDevice[] bondDevices2;
    //BluetoothServerSocket blueServer;
    //BluetoothSocket blueSocket;
    //String blueUUID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blue);

        final Intent startBlueService = new Intent(this, BlueService.class);

        Thread t = new Thread(){
            public void run()
            {
                startService(startBlueService);
            }
        };
        t.start();

        backButton = (Button) findViewById(R.id.backButton);
        feedbackText = (TextView) findViewById(R.id.feedbackText);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopService(new Intent(BlueActivity.this, BlueService.class));
                finish();
            }
        });

        //-----Bluetooth Begins
        /*
        blueUUID = "5e1d0c90-9d64-11e4-bd06-0800200c9a66";

        blueAdapter = BluetoothAdapter.getDefaultAdapter();
        if(blueAdapter.isEnabled())
        {
            feedbackText.setText("Blue already enabled");

            bondDevices = blueAdapter.getBondedDevices();
            int countem = 0;
            for( BluetoothDevice aDevice : bondDevices)
            {
                countem++;
                //feedbackText.setText("Count " + countem);

                if(/*aDevice.getBluetoothClass().equals(BluetoothClass.Device.AUDIO_VIDEO_HEADPHONES) aDevice.getName().matches("Powerbeats Wireless"))//.matches("PowerBeats Wireless"))
                {
                    blueDevice = aDevice;
                    feedbackText.setText(blueDevice.getName() + " Got em " + blueDevice.getType() + " " + blueDevice.getBluetoothClass() + " random thing: " + (int)(Math.random() * 100)); //++++++++++Note gettype requires a higher api than getname
                    break;
                }
                else
                {
                    feedbackText.setText("gt " + BluetoothClass.Device.AUDIO_VIDEO_UNCATEGORIZED); //returns 240418
                }
                //feedbackText.setText("gt" + aDevice.getType());
                //feedbackText.setText(aDevice.getName());

            }


        }
        else
        {
            feedbackText.setText("Blue not enabled");
        }
        */
            //UUID tempUUID = blueDevice.getUuids();
        /*try {
            blueSocket = blueDevice.createRfcommSocketToServiceRecord(UUID.fromString(blueUUID));
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        /*try {
            blueServer = blueAdapter.listenUsingRfcommWithServiceRecord("blueService", UUID.fromString(blueUUID));
            new Thread(new Runnable()
            {
                public void run()
                {
                    while(true)
                    {
                        try {
                            blueSocket = blueServer.accept();
                            if (blueSocket != null)
                            {
                                blueServer.close();
                                break;
                            }
                        } catch (IOException e) {

                        }
                    }
                }
            }).start();

        } catch (IOException e) {

        }*/
    }

    @Override
    public void onPause()
    {
        super.onPause();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_blue, menu);
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
