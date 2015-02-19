package com.example.oryan.testapp1;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


public class NoteActivity extends ActionBarActivity
{

    Button backButton;
    Button notifyButton;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        backButton = (Button) findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });


        final NotificationCompat.Builder noteBuilder = new NotificationCompat.Builder(this).setSmallIcon(R.drawable.mock).setContentTitle("Burning Fish!").setContentText("Fish on fire");
        Intent noteIntent = new Intent(this, MockupActivity.class);

        TaskStackBuilder noteStackBuilder = TaskStackBuilder.create(this);

        noteStackBuilder.addParentStack(MockupActivity.class);
        noteStackBuilder.addParentStack(MainActivity.class);

        noteStackBuilder.addNextIntent(noteIntent);

        PendingIntent notePending = noteStackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        noteBuilder.setContentIntent(notePending);

        final NotificationManager noteManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notifyButton = (Button) findViewById(R.id.notifyButton);
        notifyButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                noteManager.notify(5, noteBuilder.build());
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_note, menu);
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
