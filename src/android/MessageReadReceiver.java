package com.okode.cordova.auto;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MessageReadReceiver extends BroadcastReceiver {

    private final static String TAG = MessageReadReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        // Retrieving conversation ID
        int conversationId = intent.getIntExtra("conversation_id", -1);
        Log.d(TAG, "Conversation ID received: " + conversationId);
        // Remove the notification
        // TODO
        // Update the list of unread conversations in your app.
        // TODO
    }
}
