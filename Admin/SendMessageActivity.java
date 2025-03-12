
package com.example.assessment2;

import static android.content.Context.TELEPHONY_SERVICE;
import static android.content.Intent.getIntent;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;

public class SendMessageActivity {
    private static final int MY_PERMISSIONS_REQUEST_SMS = 99;

    Context mContext;
    private static String MY_PHONE_NUMBER = "";

    private final DBase database = new DBase();

    private final List<Event> eventList = database.getEventList();




    @SuppressLint({"MissingPermission", "HardwareIds"})
    public SendMessageActivity(Context mContext) throws URISyntaxException {
        this.mContext = mContext;
        //Retrieve phone numnber

        TelephonyManager telephonyManager = (TelephonyManager) mContext.getSystemService(TELEPHONY_SERVICE);
        MY_PHONE_NUMBER = telephonyManager.getLine1Number();

        sendMessage();
    }

    private void sendMessage() throws URISyntaxException {

        //get message detail
        Intent incomingIntent = getIntent(String.valueOf(mContext));
        String text = (String) Objects.requireNonNull(incomingIntent.getExtras()).get("message");

        //sending Sms Message
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(MY_PHONE_NUMBER, null,
                text, null, null);
    }

}


