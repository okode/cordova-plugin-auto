package com.okode.cordova.auto;

import android.app.PendingIntent;
import android.app.UiModeManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.RemoteInput;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.support.v4.app.NotificationCompat.CarExtender.*;
import android.util.Log;

import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;

public class CDVAuto extends CordovaPlugin {

    private final static String TAG = CDVAuto.class.getSimpleName();
    // TODO: Get the notificationId from the app
    private final static AtomicInteger notificationId = new AtomicInteger(0);
    private static CallbackContext listener;

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {

        if (action.equals("register")) {
            registerListener(callbackContext);
            return true;
        }

        if (action.equals("sendMessage")) {
            int conversationId = args.getInt(0);
            String from = args.getString(1);
            String message = args.getString(2);
            this.sendMessage(conversationId, from, message, callbackContext);
            return true;
        }

        if (action.equals("isCarUIMode")) {
            callbackContext.success(String.valueOf(isCarUIMode()));
            return true;
        }

        return false;
    }

    public static void processReply(int conversationId, String message) {
        try {
            JSONObject event = new JSONObject();
            event.put("type", "REPLY");
            event.put("conversationId", conversationId);
            event.put("message", message);
            PluginResult result = new PluginResult(PluginResult.Status.OK, event);
            result.setKeepCallback(true);
            listener.sendPluginResult(result);
        } catch (JSONException e) {
            Log.e(TAG, String.format("Error generating response with id %s and message %s",
                    conversationId, message), e);
        }
    }

    public static void processRead(int conversationId) {
        try {
            JSONObject event = new JSONObject();
            event.put("type", "READ");
            event.put("conversationId", conversationId);
            PluginResult result = new PluginResult(PluginResult.Status.OK, event);
            result.setKeepCallback(true);
            listener.sendPluginResult(result);
        } catch (JSONException e) {
            Log.e(TAG, String.format("Error generating read with id %s", conversationId), e);
        }
    }

    private void sendMessage(int conversationId, String from, String message, CallbackContext callbackContext) {
        if (from == null || from.length() == 0) {
            callbackContext.error("Expected one non-empty from.");
            return;
        }
        if (message == null || message.length() == 0 || from == null || from.length() == 0) {
            callbackContext.error("Expected one non-empty message.");
            return;
        }

        //String replyLabel = cordova.getActivity().getString(getAppResource("notification_reply", "string"));
        RemoteInput remoteInput = new RemoteInput.Builder(MessageReplyReceiver.REPLY_KEY).build();
        UnreadConversation.Builder unreadConvBuilder = new UnreadConversation.Builder(from)
                        .setReadPendingIntent(getMsgReadPendingIntent(conversationId))
                        .setReplyAction(getMsgReplyPendingIntent(conversationId), remoteInput);
        unreadConvBuilder.addMessage(message).setLatestTimestamp(System.currentTimeMillis());

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(cordova.getActivity().getApplicationContext())
                        .setSmallIcon(getAppResource("ic_notification", "drawable"));
        notificationBuilder.extend(new NotificationCompat.CarExtender()
                .setUnreadConversation(unreadConvBuilder.build()));

        NotificationManagerCompat msgNotificationManager =
                NotificationManagerCompat.from(cordova.getActivity().getApplicationContext());
        msgNotificationManager.notify(TAG, notificationId.incrementAndGet(), notificationBuilder.build());

        callbackContext.success();
    }

    private boolean isCarUIMode() {
        UiModeManager uiModeManager = (UiModeManager) cordova.getActivity().getSystemService(Context.UI_MODE_SERVICE);
        if (uiModeManager.getCurrentModeType() == Configuration.UI_MODE_TYPE_CAR) {
            Log.d(TAG, "Running in Car mode");
            return true;
        } else {
            Log.d(TAG, "Running on a non-Car mode");
            return false;
        }
    }

    private void registerListener(final CallbackContext callbackContext) {
        if (callbackContext != null) {
            try {
                listener = callbackContext;
                JSONObject event = new JSONObject();
                event.put("type", "REGISTER");
                PluginResult result = new PluginResult(PluginResult.Status.OK, event);
                result.setKeepCallback(true);
                callbackContext.sendPluginResult(result);
            } catch (JSONException e) {
                Log.e(TAG, "Error generating register response", e);
                PluginResult result = new PluginResult(PluginResult.Status.JSON_EXCEPTION);
                callbackContext.sendPluginResult(result);
            }
        } else {
            Log.e(TAG, "No callback context!");
        }
    }

    private int getAppResource(String name, String type) {
        return cordova.getActivity().getResources().getIdentifier(name, type, cordova.getActivity().getPackageName());
    }

    private PendingIntent getMsgReadPendingIntent(int conversationId) {
        Intent msgReadIntent = new Intent()
                .addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES)
                .setAction("com.okode.cordova.auto.ACTION_MESSAGE_READ")
                .putExtra("conversation_id", conversationId)
                .setPackage(cordova.getActivity().getPackageName());

        PendingIntent msgReadPendingIntent = PendingIntent.getBroadcast(
                cordova.getActivity().getApplicationContext(), conversationId, msgReadIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        return msgReadPendingIntent;
    }

    private PendingIntent getMsgReplyPendingIntent(int conversationId) {
        Intent msgReplyIntent = new Intent()
                .addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES)
                .setAction("com.okode.cordova.auto.ACTION_MESSAGE_REPLY")
                .putExtra("conversation_id", conversationId)
                .setPackage(cordova.getActivity().getPackageName());

        PendingIntent msgReplyPendingIntent = PendingIntent.getBroadcast(
                cordova.getActivity().getApplicationContext(), conversationId, msgReplyIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        return msgReplyPendingIntent;
    }
}
