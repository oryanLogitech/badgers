package com.example.oryan.testapp1;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;


public class MainActivity extends ActionBarActivity {

    public Button testButton;
    public Button nextButton;
    protected  Button goToSpeechButton;
    protected Button gotoMockupButton;
    Button gotoBlueButton;
    Button gotoBlue2Button;
    Button gotoNoteButton;
    Button goToDebugButton2;
    private Button goToDebugButton;
    private Button goToPhoneButton;
    private Button gotoVoiceButton;
    public TextView testText;
    Intent startNext;
    Intent startMockup;
    Intent startBlue;
    Intent startSpeech;
    Intent startBlue2;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gotoNoteButton =(Button) findViewById(R.id.goToNoteButton);
        testText = (TextView) findViewById(R.id.testBox);
        testButton = (Button) findViewById(R.id.testButton);
        nextButton = (Button) findViewById(R.id.nextButton);
        gotoBlue2Button = (Button) findViewById(R.id.gotoBlue2Button);
        gotoMockupButton = (Button) findViewById(R.id.gotoMockupButton);
        gotoBlueButton = (Button) findViewById(R.id.gotoBlueButton);
        goToSpeechButton = (Button) findViewById(R.id.goToSpeechButton);
        goToPhoneButton = (Button) findViewById(R.id.goToPhoneButton);
        goToDebugButton = (Button) findViewById(R.id.goToDebugButton);
        goToDebugButton2 = (Button) findViewById(R.id.goToDebugButton2);
        gotoVoiceButton = (Button) findViewById(R.id.gotoVoiceButton);
        startMockup = new Intent(this, MockupActivity.class);
        startNext = new Intent(this, SideActivity.class);
        startBlue = new Intent(this, BlueActivity.class);
        startSpeech = new Intent(this, SpeechActivity.class);
        startBlue2 = new Intent(this, Blue2Activity.class);


        goToDebugButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(MainActivity.this, DebugActivity.class));
            }
        });

        gotoNoteButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent goToNoteIntent = new Intent(MainActivity.this, NoteActivity.class);
                startActivity(goToNoteIntent);
            }
        });

        gotoVoiceButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(MainActivity.this, VoiceAPIActivity.class ));
            }
        });

        gotoBlue2Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(startBlue2);
            }
        });

        goToSpeechButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(startSpeech);
            }
        });
        gotoBlueButton.setOnClickListener
        (
            new View.OnClickListener()
            {
                public void onClick(View v)
                {
                   startActivity(startBlue);
                }
            }
        );
        testButton.setOnClickListener
        (
                new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        testText.setText("I learned of honour and haircuts.");
                    }
                }
        );
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(startNext);
            }
        });

        gotoMockupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(startMockup);
            }
        });

        goToPhoneButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(MainActivity.this, PhoneActivity.class));
            }
        });

        goToDebugButton2.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(MainActivity.this, Debug2Activity.class));
            }
        });

    }


    @Override
    public void onPause()
    {
        super.onPause();
        testText.setText("I was paused");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
