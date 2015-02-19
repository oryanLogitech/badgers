package com.example.oryan.testapp1;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothProfile;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Environment;
import android.provider.ContactsContract;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Set;

import static android.app.PendingIntent.getActivity;


public class SpeechActivity extends ActionBarActivity
{

    private Button backButton;
    private Button stopButton;
    private AudioManager am;
    private BroadcastReceiver receiver;
    private boolean doubleUp;
    private MediaRecorder recorder;
    private String saveFile;
    private boolean isRecording;
    private MediaPlayer playbackPlayer;
    private TextView feedbackBox;
    private Button playbackButton;
    private Intent recogIntent;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    private SpeechRecognizer recog;
    private Switch modeSwitch;
    private TextView responseBox;
    private ListView contactsPane;
    private SmsManager texter;

    //WhatsApp intergration variables
    private String callFile;
    private BroadcastReceiver receiverDown;
    private BroadcastReceiver receiverUp;
    private boolean isVoicing;
    private MediaRecorder SRrecorder;

    //Make Phone calls variables
    private BroadcastReceiver receiverPhone;
    private TelephonyManager tm;

    //Confirmation variables
    private Speaker speakem;
    private final int LONG_DURATION = 4600;
    private final int SHORT_DURATION = 600;
    private final int MICRO_DURATION = 1;
    private boolean isConfirming;
    private SpeechRecognizer recogCon;
    private Switch confirmSwitch;

    private Cursor readCursor;
    String readCol[] = {ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME};

    private BluetoothAdapter btAdapter;
    private BluetoothDevice btDevice;
    private BluetoothHeadset btHeadset;
    private Set<BluetoothDevice> pairedDevices;

    public static Context ourContext;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speech);

        ourContext = SpeechActivity.this;

        isVoicing = false;

        callFile = Environment.getExternalStorageDirectory().getAbsolutePath();
        callFile += "/callFile.3gp";

        speakem = new Speaker(this);
        speakem.allow(true);
        confirmSwitch = (Switch) findViewById(R.id.confirmSwitch);

        readCursor = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, readCol, null, null, ContactsContract.Contacts.DISPLAY_NAME);
        texter = SmsManager.getDefault();
        contactsPane = (ListView) findViewById(R.id.contactsPane);
        feedbackBox = (TextView) findViewById(R.id.feedbackBox);
        playbackButton = (Button) findViewById(R.id.playbackButton);
        modeSwitch = (Switch) findViewById(R.id.modeSwitch);
        responseBox = (TextView) findViewById(R.id.responseBox);

        isRecording = false;
        isConfirming = false;
        doubleUp = true;
        backButton = (Button) findViewById(R.id.backButton);
        stopButton = (Button) findViewById(R.id.stopButton);

        SetupBluetooth();

        stopButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                stopRecording();
            }
        });

        saveFile = Environment.getExternalStorageDirectory().getAbsolutePath();
        saveFile += "/saveFile.3gp";

        tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        tm.listen(new PhoneListener(), PhoneStateListener.LISTEN_CALL_STATE);

        backButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });

        playbackButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                try
                {
                    if (playbackPlayer != null && (recorder == null))
                    {
                        playbackPlayer.stop();
                        playbackPlayer.release();
                        playbackPlayer = null;
                    } else
                    {
                        playbackPlayer = new MediaPlayer();
                        playbackPlayer.setDataSource(saveFile);
                        playbackPlayer.prepare();
                        playbackPlayer.start();
                    }
                } catch (IOException e)
                {

                }
            }
        });

        am = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        am.registerMediaButtonEventReceiver(new ComponentName(this, SpeechRemoteControlReceiver.class));


        receiverPhone = new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context context, Intent intent)
            {

            }
        };
        this.registerReceiver(receiverPhone, new IntentFilter("myCustomPhoneDone"));

        receiverDown = new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context context, Intent intent)
            {
                if (intent.getAction().equals("muCustomDown"))
                {
                    SRvoice();
                }
            }
        };
        this.registerReceiver(receiverDown, new IntentFilter("myCustomDown"));//opens the listener for the Next button
        receiverUp = new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context context, Intent intent)
            {
                if (intent.getAction().equals("myCustomUp"))
                {
                    SRvoiceStopListen();
                }
            }
        };
        this.registerReceiver(receiverUp, new IntentFilter("myCustomUp"));//opens the listener for the Next button

        receiver = new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context context, Intent intent)
            {
                //feedbackBox.setText("got em");
                //+++++++++++++++++++++++++++DoubleUp is work around to bug where the intent is sent twice by the broadcaster.
                //This bug is present in certain android versions and setups, as far as i can tell there is no know cause for the bug.
                if (doubleUp)
                {
                    feedbackBox.setText("got em more");
                    if (intent.getAction().equals("myCustom"))
                    //++++++++++Danger: myCustom needs to be set to detect a Next Button press(I've set it to detect the play/pause button press for testing purposes)
                    {
                        if (!isRecording)
                        {
                            //feedbackBox.setText("Record on");
                            //startRecording();
                            if (modeSwitch.isChecked())
                            {

                                startRecog();
                            } else
                            {
                                startRecog2();
                            }
                        } else
                        {
                            if (modeSwitch.isChecked())
                            {
                                stopRecog();
                            } else
                            {
                                stopRecog2();
                            }
                            //stopRecording();
                        }

                    }
                    doubleUp = false;
                } else
                {
                    doubleUp = true;
                }
            }
        };
        this.registerReceiver(receiver, new IntentFilter("myCustom"));//opens the listener for the Next button


        //responseBox.setText(responseBox.getText() + checkContacts("test"));
    }

    //+++++Resets layout on rotation so that android doesn't call onCreate on rotation.+++++
    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        setContentView(R.layout.activity_speech);
    }

    private void startRecog()
    {
        //Toast.makeText(this, "fuck badgers", Toast.LENGTH_SHORT).show();
        for(BluetoothDevice tryDevice : pairedDevices)
        {
            //Toast.makeText(this, "badgers", Toast.LENGTH_LONG).show();
            if(btHeadset.startVoiceRecognition(tryDevice))
            {
                Toast.makeText(this, "breaking loop " + tryDevice.getName(), Toast.LENGTH_LONG).show();
                break;
            }
        }

        isRecording = true;
        //am.startBluetoothSco();
        recogIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        //recogIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, this.getCallingPackage() );
        recogIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        recogIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 3000);
        //recogIntent.putExtra(RecognizerIntent.ACTION_VOICE_SEARCH_HANDS_FREE, true);
        //recogIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        //recogIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say Something");

        recog = SpeechRecognizer.createSpeechRecognizer(SpeechActivity.this);
        recog.setRecognitionListener(new RecognitionListener()
        {
            public void onReadyForSpeech(Bundle params)
            {
                feedbackBox.setText("speak now");
            }

            public void onBeginningOfSpeech()
            {
                feedbackBox.setText("recog began");
            }

            public void onRmsChanged(float rmsdB)
            {
            }

            public void onBufferReceived(byte[] buffer)
            {
            }

            public void onEndOfSpeech()
            {
                feedbackBox.setText("recog ended");
                stopRecog();
            }

            public void onError(int error)
            {
                if (error == SpeechRecognizer.ERROR_SPEECH_TIMEOUT)
                {
                    feedbackBox.setText("recog timeout");
                    stopRecog();
                }
            }

            public void onPartialResults(Bundle partialResults)
            {
                feedbackBox.setText("recog partial return");
            }

            public void onEvent(int eventType, Bundle params)
            {
            }

            @Override
            public void onResults(Bundle results)
            {
                ArrayList strlist = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                float[] otherstuff = results.getFloatArray(SpeechRecognizer.CONFIDENCE_SCORES);
                String outputstring = "";

                String mostLikely = "" + strlist.get(0); //0 is the most likely candiate.
                for (int i = 0; i < strlist.size(); i++)
                {
                    outputstring += strlist.get(i) + "\n";
                }
                SRcommander(mostLikely);

                for (int i = 0; i < otherstuff.length; i++)
                {
                    outputstring += otherstuff[i] + " ";
                }
                /*for(int i = 0; i < strlist.size(); i++)
                {
                    outputstring += strlist.get(i);
                } */


                outputstring = "Result: " + outputstring;
                feedbackBox.setText(outputstring);
            }


        });


        //am.setMode(AudioManager.MODE_IN_COMMUNICATION);
        //am.startBluetoothSco();
        //am.setMicrophoneMute(false);
        recog.startListening(recogIntent);
        feedbackBox.setText("recog on");

    }

    private void startConfirmRecog(final String[] address,final String[] words)
    {

        for(BluetoothDevice tryDevice : pairedDevices)
        {
            if(btHeadset.startVoiceRecognition(tryDevice))
            {
                //toaster("breaking loop");
                break;
            }
        }
        isConfirming = true;
        //am.startBluetoothSco();
        recogIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        //recogIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, this.getCallingPackage() );
        recogIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        recogIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 100000);
        //recogIntent.putExtra(RecognizerIntent.ACTION_VOICE_SEARCH_HANDS_FREE, true);
        //recogIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        //recogIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say Something");
        Toast.makeText(this, "confirming begin", Toast.LENGTH_SHORT).show();
        recogCon = SpeechRecognizer.createSpeechRecognizer(SpeechActivity.this);
        recogCon.setRecognitionListener(new RecognitionListener()
        {
            public void onReadyForSpeech(Bundle params)
            {
                feedbackBox.setText("speak now");
            }

            public void onBeginningOfSpeech()
            {
                feedbackBox.setText("recog began");
            }

            public void onRmsChanged(float rmsdB)
            {
            }

            public void onBufferReceived(byte[] buffer)
            {
            }

            public void onEndOfSpeech()
            {
                feedbackBox.setText("recog ended");
                stopRecogConfirm();
            }

            public void onError(int error)
            {
                if (error == SpeechRecognizer.ERROR_SPEECH_TIMEOUT)
                {
                    feedbackBox.setText("recog timeout");
                    stopRecogConfirm();
                }
            }

            public void onPartialResults(Bundle partialResults)
            {
                feedbackBox.setText("recog partial return");
            }

            public void onEvent(int eventType, Bundle params)
            {
            }

            @Override
            public void onResults(Bundle results)
            {
                ArrayList strlist = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                float[] otherstuff = results.getFloatArray(SpeechRecognizer.CONFIDENCE_SCORES);
                String outputstring = "";

                String mostLikely = "" + strlist.get(0); //0 is the most likely candiate.

                String[] splitLikely = mostLikely.split(" ");
                if(splitLikely[0].equalsIgnoreCase("yes"))
                {
                    switch (words[0])
                    {
                        case "tell":
                            responseBox.setText("Sending Msg to ");
                            SRsendMessage(checkContacts(words[1], words[2]), words);
                            break;
                        case "send":
                            responseBox.setText("Sending Msg to ");
                            SRsendMessage(checkContacts(words[1], words[2]), words);
                            break;
                        case "mail":
                            responseBox.setText("Sending Mail to ");
                            SRsendMail(checkContacts(words[1], words[2]), words);
                            break;
                        case "e-mail":
                            responseBox.setText("Sending Mail to ");
                            SRsendMail(checkContacts(words[1], words[2]), words);
                            break;
                        case "voice":
                            responseBox.setText("Sending Voice Message, please standby.");
                            SRvoiceStartListen();
                            break;
                        case "call":
                            responseBox.setText("Making a call to ");
                            SRsendCall(checkContacts(words[1], words[2]), words);
                            break;

                        default:
                            responseBox.setText("I'm sorry, I can't do that .......what was your name again.");
                            break;
                    }
                }


            }


        });


        //am.setMode(AudioManager.MODE_IN_COMMUNICATION);
        //am.startBluetoothSco();
        //am.setMicrophoneMute(false);
        recogCon.startListening(recogIntent);
        feedbackBox.setText("recog on");

    }

    private void startRecog2()
    {
        isRecording = true;
        feedbackBox.setText("recog on");
        recogIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recogIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, this.getCallingPackage());
        recogIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        recogIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        recogIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say Something");

        try
        {
            //am.startBluetoothSco();
            startActivityForResult(recogIntent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException e)
        {
            Toast.makeText(getApplicationContext(), "Speech not supported", Toast.LENGTH_SHORT).show();
            //am.stopBluetoothSco();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        feedbackBox.setText("returned");
        if (requestCode == REQ_CODE_SPEECH_INPUT)
        {
            //am.stopBluetoothSco();
            feedbackBox.setText("no good result");
            if (resultCode == RESULT_OK && null != data)
            {

                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                feedbackBox.setText(result.get(0));
            }

        }
    }

    private void stopRecog()
    {
        if (recog != null)
        {
            //feedbackBox.setText("recog off");

            isRecording = false;
            recog.stopListening();
            for(BluetoothDevice tryDevice: pairedDevices)
            {
                if(btHeadset.stopVoiceRecognition(tryDevice))
                {
                    //toaster("stopping voice recognition mode.");
                    break;
                }
            }
            //am.stopBluetoothSco();
            //am.setMode(AudioManager.MODE_NORMAL);
            recog = null;
        }

    }

    private void stopRecogConfirm()
    {
        if (recogCon != null)
        {
            //feedbackBox.setText("recog off");

            isConfirming = false;
            recogCon.stopListening();
            for(BluetoothDevice tryDevice: pairedDevices)
            {
                if(btHeadset.stopVoiceRecognition(tryDevice))
                {
                    //toaster("stopping voice recognition mode.");
                    break;
                }
            }
            //setMode(AudioManager.MODE_NORMAL);
            recogCon = null;
        }

    }

    private void stopRecog2()
    {
        //feedbackBox.setText("recog off");
        isRecording = false;
    }


    private void startRecording()
    {
        //am.setMode(AudioManager.MODE_IN_CALL);
        //am.startBluetoothSco();
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setOutputFile(saveFile);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        try
        {
            recorder.prepare();
            recorder.start();
            isRecording = true;
        } catch (IOException e)
        {
            feedbackBox.setText("EROOROROROROROROROR");
        }
        if (isRecording)
        {
            feedbackBox.setText("Record on");
        } else
        {
            feedbackBox.setText("Record on failed");
        }

    }

    private void stopRecording()
    {
        if (recorder != null)
        {
            recorder.stop();
            recorder.release();
            recorder = null;
            isRecording = false;

        }
        if (isRecording)
        {
            feedbackBox.setText("Record off failed");
        } else
        {
            feedbackBox.setText("Record off");
        }

        //am.setMode(AudioManager.MODE_NORMAL);
        //am.stopBluetoothSco();
    }

    @Override
    protected void onRestart()
    {
        super.onRestart();
        am.registerMediaButtonEventReceiver(new ComponentName(this, SpeechRemoteControlReceiver.class));
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        stopRecog();
        stopRecog2();
        if (playbackPlayer != null)
        {
            playbackPlayer.stop();
            playbackPlayer.release();
            playbackPlayer = null;
        }

        //stopRecording();
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        am.unregisterMediaButtonEventReceiver(new ComponentName(this, SpeechRemoteControlReceiver.class));
        //am.unregisterMediaButtonEventReceiver(new ComponentName(this, VoiceRemoteControlReceiver.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_speech, menu);
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

    private void SRcommander(String inString)
    {
        String[] words = inString.split(" ");
        if (words.length > 0)
        {
            String output = "";
        /*for(int i = 0; i < words.length; i++)
        {
            output += words[i] + "\n";
        }
        responseBox.setText(output);*/

            switch (words[0])
            {
                case "tell":
                    if(confirmSwitch.isChecked())
                    {
                        SRconfirm(checkContacts(words[1], words[2]), words);
                    }
                    else
                    {
                        responseBox.setText("Sending Msg to ");
                        SRsendMessage(checkContacts(words[1], words[2]), words);
                    }
                    break;
                case "send":
                    if(confirmSwitch.isChecked())
                    {
                        SRconfirm(checkContacts(words[1], words[2]), words);
                    }
                    else
                    {
                        responseBox.setText("Sending Msg to ");
                        SRsendMessage(checkContacts(words[1], words[2]), words);
                    }

                    break;
                case "mail":
                    if(confirmSwitch.isChecked())
                    {
                        SRconfirm(checkContacts(words[1], words[2]), words);
                    }
                    else
                    {
                        responseBox.setText("Sending Mail to ");
                        SRsendMail(checkContacts(words[1], words[2]), words);
                    }

                    break;
                case "e-mail":
                    if(confirmSwitch.isChecked())
                    {
                        SRconfirm(checkContacts(words[1], words[2]), words);
                    }
                    else
                    {
                        responseBox.setText("Sending Mail to ");
                        SRsendMail(checkContacts(words[1], words[2]), words);
                    }

                    break;
                case "voice":
                    if(confirmSwitch.isChecked())
                    {
                        SRconfirm(checkContacts(words[1], words[2]), words);
                    }
                    else
                    {
                        responseBox.setText("Sending Voice Message, please standby.");
                        SRvoiceStartListen();
                    }

                    break;
                case "call":
                    if(confirmSwitch.isChecked())
                    {
                        SRconfirm(checkContacts(words[1], words[2]), words);
                    }
                    else
                    {
                        responseBox.setText("Making a call to ");
                        SRsendCall(checkContacts(words[1], words[2]), words);
                    }

                    break;

                default:
                    responseBox.setText("I'm sorry, I can't do that .......what was your name again.");
                    break;
            }
        }
    }

    //+++++Searches contacts for a match to the name entered(using the surname or lack thereof as a tiebreakers) and returns their details(name, phonenumber, email address, and whether or not the surname was required) +++++
    private String[] checkContacts(String nameToMatch, String surNameToMatch)
    {
        //String checked = "";
        Calendar rightNow = Calendar.getInstance();
        ArrayList<MatchContact> listofMatches = new ArrayList<MatchContact>();
        String personName = "", number = "", email = "", mail = "", surNameUsed = "n";
        MatchContact aMatch = null;
        String[] returnDetails = new String[4];// Name, Phone, Email, wasSurnameUsed
        String[] personNames;
        readCursor = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, readCol, null, null, ContactsContract.Contacts.DISPLAY_NAME);
        if (readCursor == null)
        {
            returnDetails[0] = "piss";
            return returnDetails;
        }
        try
        {
            if (readCursor.getCount() > 0)
            {
                while (readCursor.moveToNext())
                {
                    String id = readCursor.getString(readCursor.getColumnIndex(ContactsContract.Contacts._ID));
                    personName = readCursor.getString(readCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    personNames = personName.split(" ");

                    //~~~~~ Name matching part ~~~~~
                    if (personNames[0].equalsIgnoreCase(nameToMatch))
                    {

                        if (personNames.length > 1)
                        {
                            listofMatches.add(new MatchContact(personNames[0], personNames[1], readCursor.getColumnIndex(ContactsContract.Contacts.LAST_TIME_CONTACTED), 1, id));
                            Toast.makeText(SpeechActivity.this, "Matching double name" + personNames[0] + " " + personNames[1], Toast.LENGTH_SHORT).show();
                        } else
                        {
                            listofMatches.add(new MatchContact(personNames[0], "", readCursor.getColumnIndex(ContactsContract.Contacts.LAST_TIME_CONTACTED), 1, id));
                            Toast.makeText(SpeechActivity.this, "Matching single name " + personNames[0], Toast.LENGTH_SHORT).show();
                        }
                        responseBox.setText(responseBox.getText() + listofMatches.get(0).getName() + "\n");
                    } else
                    {
                        //checked += personName;
                        continue;
                    }
                }

                if (listofMatches.size() > 1)
                {
                    for (int j = 0; j < listofMatches.size(); j++)
                    {
                        if (listofMatches.get(j).getSurname().equalsIgnoreCase(surNameToMatch))
                        {
                            Toast.makeText(SpeechActivity.this, "Matching surname " + listofMatches.get(j).getSurname(), Toast.LENGTH_SHORT).show();
                            aMatch = listofMatches.get(j);
                            surNameUsed = "y";
                            break;
                        }
                    }
                    if (aMatch == null)
                    {
                        for (int j = 0; j < listofMatches.size(); j++)
                        {
                            if (listofMatches.get(j).getSurname().equalsIgnoreCase(""))
                            {
                                Toast.makeText(SpeechActivity.this, "Matching lack of surname ", Toast.LENGTH_SHORT).show();
                                aMatch = listofMatches.get(j);
                                break;
                            }
                        }
                    }

                } else if (listofMatches.size() > 0)
                {
                    Toast.makeText(SpeechActivity.this, "Matching overwriting anyway", Toast.LENGTH_SHORT).show();
                    aMatch = listofMatches.get(0);
                }

                if (aMatch != null)
                {
                    Toast.makeText(SpeechActivity.this, "we have match" + aMatch.getName() + " " + aMatch.getSurname(), Toast.LENGTH_SHORT).show();
                    Cursor cur = SpeechActivity.this.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{aMatch.getId()}, null);
                    if (cur == null)
                    {
                        throw new Exception();
                    }
                    number = "";
                    mail = "";
                    while (cur.moveToNext())
                    {
                        number = cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        //mail = cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS));
                        //Toast.makeText(this, mail, Toast.LENGTH_SHORT).show();
                    }
                    cur = SpeechActivity.this.getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null, ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?", new String[]{aMatch.getId()}, null);
                    while (cur.moveToNext())
                    {
                        mail = cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS));
                        Toast.makeText(this, mail, Toast.LENGTH_SHORT).show();
                    }
                    //number stored in seperate place to name and id.
                    cur.close();
                    returnDetails[0] = personName;
                    returnDetails[1] = number;
                    returnDetails[2] = mail;
                    returnDetails[3] = surNameUsed; //y for surname used, n for surname not used;
                    return returnDetails;
                    //checked += "\n +++ \n";
                }
            }
        } catch (Exception e)
        {
            String[] out = {"problem error code: " + e.toString() + " "};
            return out;
        } finally
        {
            readCursor.close();
        }
        String[] out = {"Name not found"};
        return out;
    }

    private void SRsendCall(String[] address, String[] words)
    {
        Intent makeCall = new Intent();
        makeCall.setAction(Intent.ACTION_CALL);
        makeCall.setData(Uri.parse("tel:" + address[1]));
        startActivity(makeCall);

    }

    private void SRconfirm(String[] address, String[] words)
    {
                    String moreWords = "";
                    //Toast.makeText(this, "confirming", Toast.LENGTH_SHORT).show();
                    for(int i = 2;i < words.length; i++)
                    {
                        if(words.length < 4)
                        {
                            if (address[3].matches("y") && i == 0)
                            {
                                i = 3;
                            }
                        }
                        moreWords += words[i] + " ";
                    }
                    //toastToaster("we talking threads");
                    //Toast.makeText(this, "confirming", Toast.LENGTH_SHORT).show();
                    speakem.pause(SHORT_DURATION);
                    speakem.speak("Do you want to ");
                    speakem.pause(SHORT_DURATION);
                    speakem.speak(words[0] + " " + address[0]);
                    speakem.pause(SHORT_DURATION);
                    /*for(int i = 2;i < words.length; i++)
                    {
                        if(words.length < 4)
                        {
                            if (address[3].matches("y") && i == 0)
                            {
                                i = 3;
                            }
                        }
                        speakem.speak(words[i]);
                        speakem.pause(MICRO_DURATION);
                    }*/
                    speakem.speak(moreWords);
                    Toast.makeText(this, words[0] + " " + address[0] + " " + moreWords, Toast.LENGTH_SHORT).show();
                    while(speakem.speaking())
                    {
                    }
                    startConfirmRecog(address, words);
    }

    private void SRsendMessage(String[] address, String[] words)
    {
        String body = "";
        if(address.length < 2)
        {
            responseBox.setText(responseBox.getText() + " ::: " + address[0]);
        }
        else
        {
            try
            {
                Toast.makeText(SpeechActivity.this, "boat" + address[3], Toast.LENGTH_SHORT).show();
                if(address[3].matches("y"))
                {
                    for (int i = 3; i < words.length; i++)
                    {
                        body += words[i] + " ";
                    }
                }
                else
                {
                    for (int i = 2; i < words.length; i++)
                    {
                        body += words[i] + " ";
                    }
                }
                texter.sendTextMessage(address[1], null, body, null, null);
                responseBox.setText(responseBox.getText() + address[0] + " has been sent");

            } catch (Exception e)
            {
                responseBox.setText(responseBox.getText() + " error sending msg.");
            }
        }
    }

    private void SRsendMail(String[] address, String[] words)
    {
        String body = "";
        if(address.length < 2)
        {
            responseBox.setText(responseBox.getText() + " ::: " + address[0] + " error sending msg.");
        }
        else
        {
            try
            {
                Intent intenty = new Intent(Intent.ACTION_SEND);
                intenty.setType("message/rfc822");
                intenty.putExtra(Intent.EXTRA_EMAIL, new String[]{address[2]});
                intenty.putExtra(Intent.EXTRA_SUBJECT, "Sent from my Tap");
                Toast.makeText(SpeechActivity.this, "boat" + address[3], Toast.LENGTH_SHORT).show();
                if(address[3].equalsIgnoreCase("y"))
                {
                    for (int i = 3; i < words.length; i++)
                    {
                        body += " " + words[i];
                    }
                }
                else
                {
                    for (int i = 2; i < words.length; i++)
                    {
                        body += " " + words[i];
                    }
                }
                intenty.putExtra(Intent.EXTRA_TEXT, body);
                try {
                    startActivity(intenty);
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(SpeechActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                }
                responseBox.setText(responseBox.getText() + "has been sent");

            } catch (Exception e)
            {
                responseBox.setText(responseBox.getText() + " error sending mail." + e.toString() + " " + address.length + " " + words.length );
            }
        }
    }

    private void SRvoice()
    {
        Toast.makeText(this, "engage yam", Toast.LENGTH_SHORT);
        try
        {
            Toast.makeText(this, "engaged yams", Toast.LENGTH_SHORT);
            SRrecorder = new MediaRecorder();
            SRrecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            SRrecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            SRrecorder.setOutputFile(callFile);
            SRrecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            SRrecorder.prepare();
            SRrecorder.start();
            feedbackBox.setText("yams away");
            Log.v("fjhsdkjfhsadkjffgetwzxbfagrf", "PISS");
        }
        catch (IOException e)
        {
            Toast.makeText(this, "taking heavy yams", Toast.LENGTH_SHORT).show();
        }
    }

    private void SRvoiceStartListen()
    {
        Toast.makeText(this, "yam", Toast.LENGTH_SHORT).show();
        isVoicing = true;
        am.registerMediaButtonEventReceiver(new ComponentName(this, VoiceRemoteControlReceiver.class));
        am.unregisterMediaButtonEventReceiver(new ComponentName(this, SpeechRemoteControlReceiver.class));
    }

    private void SRvoiceStopListen()
    {
        Toast.makeText(this, "less yam", Toast.LENGTH_SHORT).show();
        isVoicing = false;
        am.unregisterMediaButtonEventReceiver(new ComponentName(this, VoiceRemoteControlReceiver.class));
        am.registerMediaButtonEventReceiver(new ComponentName(this, SpeechRemoteControlReceiver.class));
        if(SRrecorder != null)
        {
            SRrecorder.stop();
            SRrecorder.release();
            SRrecorder = null;
            feedbackBox.setText("out of yams");
        }

        //chatSend();
    }

    private void chatSend()
    {
        Intent sendMedia = new Intent();
        sendMedia.setAction(Intent.ACTION_SEND);
        sendMedia.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(callFile)));
        sendMedia.setType("audio/3gp");
        //sendMedia.putExtra(Intent.EXTRA_TEXT, "Ok that works");
        //sendMedia.setType("text/plain");
        sendMedia.setPackage("com.whatsapp");
        startActivity(sendMedia);
        feedbackBox.setText("more yams requested");
        Log.v("narf", "it a done sent");
    }

    private void SetupBluetooth()
    {
        btAdapter = BluetoothAdapter.getDefaultAdapter();

        pairedDevices = btAdapter.getBondedDevices();

        BluetoothProfile.ServiceListener mProfileListener = new BluetoothProfile.ServiceListener() {
            public void onServiceConnected(int profile, BluetoothProfile proxy)
            {
                if (profile == BluetoothProfile.HEADSET)
                {
                    btHeadset = (BluetoothHeadset) proxy;
                }
            }
            public void onServiceDisconnected(int profile)
            {
                if (profile == BluetoothProfile.HEADSET) {
                    btHeadset = null;
                }
            }
        };
        btAdapter.getProfileProxy(SpeechActivity.this, mProfileListener, BluetoothProfile.HEADSET);

    }

}

