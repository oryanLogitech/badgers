package com.example.oryan.testapp1;

import android.content.Context;
import android.media.AudioManager;
import android.speech.tts.TextToSpeech;

import java.util.HashMap;
import java.util.Locale;

/**
 * Created by oryan on 29/01/2015.
 */
public class Speaker implements TextToSpeech.OnInitListener
{
    private TextToSpeech tts; //Object for interacting with the Text-to-Speech Engine.

    private boolean ready = false;
    private boolean allowed = false;

    public Speaker(Context context){
        tts = new TextToSpeech(context, this);
    }

    //+++++Method for checking if the Text-to-Speech has been permitted.+++++
    public boolean isAllowed()
    {
        return allowed;
    }

    //+++++Method for permitting or denying the Text-to-Speech to run.+++++
    public void allow(boolean allowed)
    {
        this.allowed = allowed;
    }


    //+++++Method that listens for intializing the Text to Speech+++++
    @Override
    public void onInit(int status)
    {
        if(status == TextToSpeech.SUCCESS)
        {
            tts.setLanguage(Locale.UK);
            ready = true;
        }
        else
        {
            ready = false;
        }
    }

    //+++++Method that passes a string to the Text-to-Speech Engine for reading.+++++
    public void speak(String text)
    {
        if(ready && allowed)
        {
            HashMap<String, String> hash = new HashMap<String,String>();
            hash.put(TextToSpeech.Engine.KEY_PARAM_STREAM, String.valueOf(AudioManager.STREAM_MUSIC));
            tts.speak(text, TextToSpeech.QUEUE_ADD, hash);
        }
    }

    public void stop()
    {
        tts.stop();
    }

    public boolean speaking()
    {
        return tts.isSpeaking();
    }

    //+++++Method for making the Text-to-Speech engine pause for a duration.+++++
    public void pause(int duration)
    {
        tts.playSilence(duration, TextToSpeech.QUEUE_ADD, null);
    }

    public void destroy()
    {
        tts.shutdown();
    }
}
