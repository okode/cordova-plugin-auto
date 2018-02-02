package com.okode.cordova.auto;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import android.support.v4.app.NotificationManagerCompat;

public class MessageReadReceiver extends BroadcastReceiver {

    private final static String TAG = MessageReadReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        // Retrieving conversation ID
        int conversationId = intent.getIntExtra("conversation_id", -1);
        Log.d(TAG, "Conversation ID read: " + conversationId);

        NotificationManagerCompat.from(context).cancel(conversationId);
        CDVAuto.processRead(conversationId);
    }
}
