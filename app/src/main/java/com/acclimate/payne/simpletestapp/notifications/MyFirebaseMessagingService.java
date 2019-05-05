package com.acclimate.payne.simpletestapp.notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;

import com.acclimate.payne.simpletestapp.R;
import com.acclimate.payne.simpletestapp.activities.Home;
import com.acclimate.payne.simpletestapp.appUtils.AppTag;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;


/**
 * Copyright 2016 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    private static String registrationToken = "";

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

//        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
//            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

            if (/* TODO: Check if data needs to be processed by long running job */ true) {
                // For long-running tasks (10 seconds or more) use Firebase Job Dispatcher
                scheduleJob();
            } else {
                // Handle message within 10 seconds
                handleNow();
            }
        }

        // Check if message contains a notification payload
        if (remoteMessage.getNotification() != null) {
            ;
//            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        // To generate our own notifications as a result of a received FCM message
        sendNotification(remoteMessage.getData());
    }


    /**
     * Called if InstanceID jwtToken is updated. This may occur if the security of
     * the previous jwtToken had been compromised. Note that this is called when the InstanceID jwtToken
     * is initially generated so this is where you would retrieve the jwtToken.
     *
     * After you've obtained the jwtToken, you can send it to your app
     * server and store it using your preferred method.
     * See the Instance ID API reference for full detail on the API:
     * https://firebase.google.com/docs/reference/android/com/google/firebase/iid/FirebaseInstanceId
     *
     * The registration jwtToken may change when:
     *   The app deletes Instance ID
     *   The app is restored on a new device
     *   The user uninstalls/reinstall the app
     *   The user clears app data.
     */
    @Override
    public void onNewToken(String token) {
//        Log.d(TAG, "New registrationToken: " + token);
        sendRegistrationToServer(token);
    }

    /**
     * Schedule a job using FirebaseJobDispatcher.
     */
    private void scheduleJob() {
        // [START dispatch_job]
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(this));
        Job myJob = dispatcher.newJobBuilder()
                .setService(MyJobService.class)
                .setTag("my-job-tag")
                .build();
        dispatcher.schedule(myJob);
        // [END dispatch_job]
    }

    /**
     * Handle time allotted to BroadcastReceivers.
     */
    private void handleNow() {
        ;
//        Log.d(TAG, "Short lived task is done.");
    }

    /**
     * Persist jwtToken to third-party servers.
     *
     * Modify this method to associate the user's FCM InstanceID jwtToken with any server-side account
     * maintained by your application.
     *
     * @param token The new jwtToken.
     */
    private void sendRegistrationToServer(String token) {
        // TODO: Implement this method to send regToken to app server, only if authenticated!
    }

    /**
     * Generates properly the notification received from the server.
     * The 'tag' is what distinguishes different notifications. If two notifications are received
     * with the same 'tag', then the newer one overwrite/updates the older one.
     *
     * @param msgData The whole data payload received as a Map.
     */
    private void sendNotification(Map<String, String> msgData) {

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        // Bloquer les notifications en fonction des préférences
        // TODO: Intégrer les autres préférences (comme les filtres de notif par type d'alertes ou MZ)
        boolean blockNotifs = sharedPref.getBoolean(AppTag.NOTIF_BLOCK, false);
        if(blockNotifs) return;

        // Extraction des informations communes à tous les 'data payload'.
//        Log.w(TAG, "msgData: " + msgData);
        String title = msgData.get("title");
        String body = msgData.get("body");

        // Setting up the ENUM
        NotifType type = (title.equals(NotifType.NEW_LIVE_ALERT.getTitle())) ? NotifType.NEW_LIVE_ALERT :
                         (title.equals(NotifType.NEW_USER_ALERT.getTitle())) ? NotifType.NEW_USER_ALERT :
                         (title.equals(NotifType.GPS.getTitle()))            ? NotifType.GPS       :
                         (title.equals(NotifType.UPDATE.getTitle()))         ? NotifType.UPDATE    :
                         NotifType.CUSTOM; // default

        // Construction de l'apparence de la notif
        String channelId = getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Bitmap icon = BitmapFactory.decodeResource(this.getResources(), R.drawable.logo_couleur_icon);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.logo_couleur_icon)
                        .setLargeIcon(icon)
                        .setContentTitle(title)
                        .setContentText(body)
                        .setAutoCancel(true);

        // Bloquer les bruits en fonction des préférences
        boolean silence = sharedPref.getBoolean(AppTag.NOTIF_SILENCE, false);
        if(!silence) notificationBuilder.setSound(defaultSoundUri);


//        /*
//                !! IMPORTANT !!
//            If other classes are included as possible openingClass, this line must be included
//            in the onCreate() method of those classes:
//                PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
//         */
        // Determine which Activity to open when the user clicks on the notification
        Class openingClass;
        switch(type) {
            case NEW_LIVE_ALERT:
            case NEW_USER_ALERT:
            case GPS:
//                openingClass = MainActivity.class;
//                break;
            case CUSTOM:
            case UPDATE:
            default:
                openingClass = Home.class;
                break;
        }
        // Set up the "Activity Opening" action (binding it to the notification)
        Intent intent = new Intent(this, openingClass);
        /* For FLAGS, see:
             https://developer.android.com/reference/android/content/Intent#constants_1
             https://developer.android.com/reference/android/content/Intent.html#setFlags(int)
        */
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0 , intent, PendingIntent.FLAG_ONE_SHOT);
        notificationBuilder.setContentIntent(pendingIntent);


        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        // Creates the notification (places it in the System Tray)
        notificationManager.notify(msgData.get("tag"), 0, notificationBuilder.build());
    }

    /**
     * Needs to be done because asynchroneous.
     */
    public static void setUpRegistrationToken() {
        // Get jwtToken
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
//                        Log.w("registrationToken", "getInstanceId failed", task.getException());
                        return;
                    }

                    // Get the registrationToken
                    registrationToken = task.getResult().getToken();

//                    Log.w("registrationToken", registrationToken);
                });
    }

    public static String getRegistrationToken() {
        return registrationToken;
    }
}