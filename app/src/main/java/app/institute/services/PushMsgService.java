package app.institute.services;

import android.provider.Settings;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import app.institute.model.Message;
import app.institute.sqlite.SQLiteDB;
import app.institute.utils.NotificationUtils;

/**
 * This class is designed to receive downstream message
 */
public class PushMsgService extends FirebaseMessagingService {

    private static final String TAG = "PushMsgService";

    /**
     * Call this method when message arrived
     * @param remoteMessage Pass downstream data
     */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        //TODO(developer): Handle FCM messages here.
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        /**
         * Check if message contains a data payload.
         */
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
        }

        /**
         * Check if message contains a notification payload.
         */
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            Log.d(TAG, "Message Notification Title: " + remoteMessage.getNotification().getTitle());
            Log.d(TAG, "Message Notification Click Action: " + remoteMessage.getNotification().getClickAction());
            Log.d(TAG, "Message Notification Icon: " + remoteMessage.getNotification().getIcon());
        }

        /**
         * Call this method to notify user
         */
        NotificationUtils.notify(this, remoteMessage);

        String message = remoteMessage.getData().get("data");
        String timestamp = String.valueOf(System.currentTimeMillis());
        Log.d(TAG, "Message Notification Data: " + remoteMessage.getData().get("data"));

        new SQLiteDB(this).insert(new Message(message, timestamp));
        DownstreamMessageBroadcast.send(this, message);
    }
}