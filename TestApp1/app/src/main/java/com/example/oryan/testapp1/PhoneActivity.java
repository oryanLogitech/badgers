package com.example.oryan.testapp1;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;


public class PhoneActivity extends ActionBarActivity
{

    private Switch serviceSwitch;
    private Button backButton;

    public static Context ourPhoneContext;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone);

        ourPhoneContext = PhoneActivity.this;

        final Intent startPhone = new Intent(this, PhoneService.class);
        serviceSwitch = (Switch) findViewById(R.id.serviceSwitch);
        backButton = (Button) findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });


        if(PhoneService.phoneRunning)
        {
            serviceSwitch.setChecked(true);
        }
        else
        {
            serviceSwitch.setChecked(false);
        }

        serviceSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {

                if(isChecked)
                {

                    Thread t = new Thread(){
                        public void run()
                        {
                            startService(startPhone);
                        }
                    };
                    t.start();
                }
                else
                {
                    Toast.makeText(PhoneActivity.this, "not a sandwich", Toast.LENGTH_SHORT).show();
                    stopService(startPhone);
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_phone, menu);
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
