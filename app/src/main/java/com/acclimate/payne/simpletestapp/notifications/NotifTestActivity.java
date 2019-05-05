package com.acclimate.payne.simpletestapp.notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.Toast;

import com.acclimate.payne.simpletestapp.R;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

@Deprecated
public class NotifTestActivity extends AppCompatActivity {

    private final String TAG = "NotifTestActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notif_test);

        setUpNotifs();
    }

    /**
     * Copy-pasted from
     * https://github.com/firebase/quickstart-android/blob/master/messaging/app/src/main/java/com/google/firebase/quickstart/fcm/MainActivity.java
     * <p>
     * TODO: Apps that rely on the Play Services SDK should always check the device for a compatible
     * Google Play services APK before accessing Google Play services features. It is recommended
     * to do this in two places: in the main activity's onCreate() method, and in its onResume()
     * method. The check in onCreate() ensures that the app can't be used without a successful
     * check. The check in onResume() ensures that if the user returns to the running app through
     * some other means, such as through the back button, the check is still performed.
     * <p>
     * If the device doesn't have a compatible version of Google Play services, your
     * app can call GoogleApiAvailability.makeGooglePlayServicesAvailable() to
     * allow users to download Google Play services from the Play Store.
     * See:
     * https://developers.google.com/android/reference/com/google/android/gms/common/GoogleApiAvailability.html#public-methods
     */
    private void setUpNotifs() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create channel to show notifications.
            String channelId = getString(R.string.default_notification_channel_id);
            String channelName = getString(R.string.default_notification_channel_name);
            NotificationManager notificationManager =
                    getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(new NotificationChannel(channelId,
                    channelName, NotificationManager.IMPORTANCE_LOW));
        }

        // If a notification message is tapped, any data accompanying the notification
        // message is available in the intent extras. In this sample the launcher
        // intent is fired when the notification is tapped, so any accompanying data would
        // be handled here. If you want a different intent fired, set the click_action
        // field of the notification message to the desired intent. The launcher intent
        // is used when no click_action is specified.
        //
        // Handle possible data accompanying notification message.
        // [START handle_data_extras]
        if (getIntent().getExtras() != null) {
            for (String key : getIntent().getExtras().keySet()) {
                Object value = getIntent().getExtras().get(key);
//                Log.w(TAG, "Key: " + key + " Value: " + value);
            }
        }
        // [END handle_data_extras]

        Button subscribeButton = findViewById(R.id.subscribeButton);
        subscribeButton.setOnClickListener(/* View v */ v -> {
//            Log.w(TAG, "Subscribing to news topic");
            // [START subscribe_topics]
            FirebaseMessaging.getInstance().subscribeToTopic("news")
                    .addOnCompleteListener(task -> {
                        String msg = getString(R.string.msg_subscribed);
                        if (!task.isSuccessful()) {
                            msg = getString(R.string.msg_subscribe_failed);
                        }
//                        Log.w(TAG, msg);
                        Toast.makeText(NotifTestActivity.this, msg, Toast.LENGTH_SHORT).show();
                    });
            // [END subscribe_topics]
        });

        Button logTokenButton = findViewById(R.id.logTokenButton);
        logTokenButton.setOnClickListener(/* View v */ v -> {
            // Get jwtToken
            FirebaseInstanceId.getInstance().getInstanceId()
                    .addOnCompleteListener(task -> {
                        if (!task.isSuccessful()) {
//                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID jwtToken
                        String token = task.getResult().getToken();

                        // Log and toast
                        String msg = getString(R.string.msg_token_fmt, token);
//                        Log.w(TAG, msg);
                        Toast.makeText(NotifTestActivity.this, msg, Toast.LENGTH_SHORT).show();
                    });
        });
    }
}
