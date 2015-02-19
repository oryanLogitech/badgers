package com.example.oryan.testapp1;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;


public class Debug2Activity extends ActionBarActivity
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
    private Boolean isConfirming;
    private Intent recogIntent;
    private SpeechRecognizer recogCon;
    private AudioManager am;
    private boolean isConnected;
    private Set<BluetoothDevice> pairedDevices;
    private BroadcastReceiver connectedListener;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug);

        am =(AudioManager) getSystemService(AUDIO_SERVICE);
        responseLog = "";
        recogButton = (Button) findViewById(R.id.recogButton);
        backButton = (Button) findViewById(R.id.backButton);
        debugResponseBox = (TextView) findViewById(R.id.debugResponseBox);
        isRecoging = false;
        isConnected = false;
        connectedListener = null;

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

        pairedDevices = debugAdapter.getBondedDevices();
        // If there are paired devices
        if (pairedDevices.size() > 0)
        {
            toaster("Got Paired Devices: ");
            // Loop through paired devices
            for (BluetoothDevice device : pairedDevices)
            {
                // Add the name and address to an array adapter to show in a ListView
               toaster(device.getName() + " : " + device.getAddress());
               if(device.getName().equalsIgnoreCase("ERA by Jawbone"))
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
        debugAdapter.getProfileProxy(Debug2Activity.this, mProfileListener, BluetoothProfile.HEADSET);

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

        recogButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(!isRecoging)
                {
                    isRecoging = true;

                    startConfirmRecog();
                    toaster("recoginising stuff");
                }
                else
                {
                    isRecoging = false;
                    stopRecogConfirm();
                    toaster("recoginising finished");
                }

            }
        });



    }

    private void toaster(String stringToToast)
    {
        Toast.makeText(Debug2Activity.this, stringToToast, Toast.LENGTH_SHORT).show();
        responseLog += stringToToast + "\n";
        debugResponseBox.setText(responseLog);
    }

    private void startConfirmRecog()
    {
        for(BluetoothDevice tryDevice : pairedDevices)
        {
            if(debugHeadset.startVoiceRecognition(tryDevice))
            {
                toaster("breaking loop");
                break;
            }
        }

        recogIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        //recogIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, this.getCallingPackage() );
        recogIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        recogIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 100000);
        //recogIntent.putExtra(RecognizerIntent.ACTION_VOICE_SEARCH_HANDS_FREE, true);
        //recogIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        //recogIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say Something");
        Toast.makeText(this, "confirming begin", Toast.LENGTH_SHORT).show();
        recogCon = SpeechRecognizer.createSpeechRecognizer(Debug2Activity.this);
        recogCon.setRecognitionListener(new RecognitionListener()
        {
            public void onReadyForSpeech(Bundle params)
            {
                toaster("speak now");
            }

            public void onBeginningOfSpeech()
            {
                toaster("recog began");
            }

            public void onRmsChanged(float rmsdB)
            {
            }

            public void onBufferReceived(byte[] buffer)
            {
            }

            public void onEndOfSpeech()
            {
                toaster("recog ended");
                stopRecogConfirm();
            }

            public void onError(int error)
            {
                if (error == SpeechRecognizer.ERROR_SPEECH_TIMEOUT)
                {
                    toaster("recog timeout");
                    stopRecogConfirm();
                }
            }

            public void onPartialResults(Bundle partialResults)
            {
                toaster("recog partial return");
            }

            public void onEvent(int eventType, Bundle params)
            {
            }

            @Override
            public void onResults(Bundle results)
            {
                toaster("calculating result");
                ArrayList strlist = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                float[] otherstuff = results.getFloatArray(SpeechRecognizer.CONFIDENCE_SCORES);
                String outputstring = "";

                String mostLikely = "" + strlist.get(0); //0 is the most likely candiate.

                String[] splitLikely = mostLikely.split(" ");
                toaster("recieved result:" + mostLikely);


            }


        });

        recogCon.startListening(recogIntent);


    }

    private void stopRecogConfirm()
    {

        if (recogCon != null)
        {
            toaster("recog off");

            isRecoging = false;
            recogCon.stopListening();
            for(BluetoothDevice tryDevice: pairedDevices)
            {
                if(debugHeadset.stopVoiceRecognition(tryDevice))
                {
                    toaster("stopping voice recognition mode.");
                    break;
                }
            }
            am.setMode(AudioManager.MODE_NORMAL);
            recogCon = null;
        }

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
