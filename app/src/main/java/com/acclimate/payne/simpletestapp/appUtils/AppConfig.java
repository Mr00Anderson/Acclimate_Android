package com.acclimate.payne.simpletestapp.appUtils;

import com.google.firebase.auth.FirebaseAuth;

public class AppConfig {

    public static final boolean GENERAL_DEBUG_MODE = true;
    public static final boolean REQUEST_DEBUG_MODE = true;
    public static final boolean TEST_HOME = true;

    // 3 minutes = 3 min * 60 sec * 1000 ms
    // public static long REQUEST_TIME_TRESHOLD = 3L * 60L * 1000L;
    public static long REQUEST_TIME_TRESHOLD = 30L * 1000L;


    public static final String ALERT_WRAPPER_FILENAME = "lastSavedAlertsWrapper.json";
    private static final String MONITORED_ZONE_FILENAME = "savedMonitoredZones";
    private static final String APP_USER_FILENAME = "savedAppUser";

    public static String getAppUserFilename(String uId) {
        return APP_USER_FILENAME + "_" +  uId + ".json";
    }

    public static String getMonitoredZoneFilename() {
        String uId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        return MONITORED_ZONE_FILENAME + "_" +  uId + ".json";
    }

    public static String getMonitoredZoneFilename(String uId) {
        return MONITORED_ZONE_FILENAME + "_" +  uId + ".json";
    }


}
