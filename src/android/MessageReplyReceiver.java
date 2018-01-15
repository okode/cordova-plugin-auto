package com.okode.cordova.auto;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.RemoteInput;
import android.util.Log;

public class MessageReplyReceiver extends BroadcastReceiver {

    public final static String REPLY_KEY = "VOICE_REPLY_KEY";
    private final static String TAG = MessageReplyReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        // Retrieving conversation ID
        int conversationId = intent.getIntExtra("conversation_id", -1);
        Log.d(TAG, "Conversation ID received on reply from auto: " + conversationId);
        // Call JS context to pass the message
        String message = getMessageText(intent);
        if (message != null) {
            // TODO
        }
    }

    /**
     * Get the message text from the intent.
     */
    private String getMessageText(Intent intent) {
        String message = null;
        Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
        if (remoteInput != null) {
            message = remoteInput.getCharSequence(REPLY_KEY).toString();
        }
        return message;
    }
}
