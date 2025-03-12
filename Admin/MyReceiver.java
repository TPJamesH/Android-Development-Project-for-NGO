/*
package com.example.assessment2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;

import java.util.Objects;

public class MyReceiver extends BroadcastReceiver {


  //  private int lastState = TelephonyManager.CALL_STATE_IDLE;
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Objects.equals(intent.getAction(), My_Site.MESSAGE_BUTTON)){ //RECEIVE COMMAND FROM THE OTHER SIDE
            sendMessage(context, "ALERT: THERE HAS BEEN CHANGES IN" + My_Site.MESSAGE_BUTTON);
        }

    }

    private void sendMessage(Context context, String message){
        Intent newIntent = new Intent(context, SendMessageActivity.class);
        newIntent.putExtra("message", message);
        context.startActivity(newIntent);
    }
}



 */
