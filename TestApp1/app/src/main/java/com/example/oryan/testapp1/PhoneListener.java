package com.example.oryan.testapp1;

/**
 * Created by oryan on 04/02/2015.
 */

import android.content.Intent;
import android.provider.ContactsContract;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.widget.Toast;

/**
 * Listener to detect incoming calls.
 */
public class PhoneListener extends PhoneStateListener
{
    @Override
    public void onCallStateChanged(int state, String incomingNumber) {
        Intent phoneem = new Intent();
        switch (state) {
            case TelephonyManager.CALL_STATE_RINGING:
                // called when someone is ringing to this phone

                phoneem.setAction("myCustomPhone");
                PhoneActivity.ourPhoneContext.sendBroadcast(phoneem);
                break;
            case TelephonyManager.CALL_STATE_IDLE:
                phoneem = new Intent();
                phoneem.setAction("myCustomPhoneDone");
                //PhoneActivity.ourPhoneContext.sendBroadcast(phoneem);
        }
    }


}
