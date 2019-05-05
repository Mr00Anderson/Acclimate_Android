package com.acclimate.payne.simpletestapp.appUtils;

public class AppTag {

    // jUnit test tags
    private static final String DELIM =                 ".";

    private static final String OLI =                   "OLI";
    public  static final String nl =                    "\n";

    public static final String SERVER =                 "SERVER";
    public static final String POST =                   "POST";
    public static final String GET =                    "GET";
    public static final String PATCH =                  "PATCH";
    public static final String VIEWMODEL =              "VIEWMODEL";
    public static final String HANDLE =                 "HANDLE";
    public static final String STORAGE =                "storage";
    public static final String MAP =                    "map";
    public static final String CACHE =                  "cache_map";
    public static final String APP_FLOW =               "app_flow";
    public static final String PIN_FLOW =               "pin_flow";
    public static final String WIFI_FLOW =              "wifiFlow";




    public static final String SERVER_POST =           merge(SERVER, POST);
    public static final String SERVER_GET =            merge(SERVER, GET);
    public static final String SERVER_PATCH =          merge(SERVER, PATCH);
    public static final String SERVER_HANDLE =         merge(SERVER, HANDLE);
    public static final String VIEWMODEL_HANDLE =      merge(VIEWMODEL, HANDLE);

    public static final String BUTTON =                 "_button";



    // saved instanced tags
    public static final String CENTER_LNG =             "center_lng";
    public static final String CENTER_LAT =             "center_alt";
    public static final String ZOOM_LVL =               "zoom_lvl";
    public static final String SHOW_USER_PIN =          "show_user_pin";
    public static final String MIRROR_ALERT_FILTER =    "mirror_alert_filter";
    public static final String FEU_FILTER =             "feu_alert";
    public static final String EAU_FILTER =             "eau_filter";
    public static final String TERRAIN_FILTER =         "terrain_filter";
    public static final String METEO_FILTER =           "meteo_filter";
    public static final String SHOW_MONITORED_ZONE =    "show_monitored_zone";
    public static final String HITORIQUE_FILTER =       "historique_filter";
    public static final String HITORIQUE_LOADED =       "historique_loaded";
    public static final String PIN_PREFERENCES =        "pin_preferences";
    public static final String MAIN_IS_FIRST_RUN =      "main_first_run";
    public static final String FIRST_TIME_USER =        "first_time_user";

    // shared preferences tag
    public static final String CURRENT_POS_BOX_NORTH =  "current_pos_box_north";
    public static final String CURRENT_POS_BOX_SOUTH =  "current_pos_box_south";
    public static final String CURRENT_POS_BOX_EAST =   "current_pos_box_east";
    public static final String CURRENT_POS_BOX_WEST =   "current_pos_box_west";
    public static final String CURRENT_POS_CENTER_LAT = "current_pos_center_lat";
    public static final String CURRENT_POS_CENTER_LNG = "current_pos_center_lng";
    public static final String CURRENT_POS_ZOOM_LVL =   "current_pos_zoom_lvl";
    public static final String LAST_KNOWN_LOC_LAT =     "last_known_loc_lat";
    public static final String LAST_KNOWN_LOC_LNG =     "last_known_loc_lng";


    // preference activity (with hierarchy)
    public static final String PREF_ALERT =                 "pref_ps_alert";
    /**/public static final String ALERT_USER_FILTER =      "pref_alert_sw_userFilter";
    /**/public static final String ALERT_USER_COPY_FILTER = "pref_alert_sw_userReplicateFilters";
    /**/public static final String ALERT_FIRE_FILTER =      "pref_alert_cb_fireFilter";
    /**/public static final String ALERT_WATER_FILTER =     "pref_alert_cb_waterFilter";
    /**/public static final String ALERT_WIND_FILTER =      "pref_alert_cb_windFilter";
    /**/public static final String ALERT_EARTH_FILTER =     "pref_alert_cb_earthFilter";
    /**/public static final String ALERT_HISTO_FILTER =     "pref_alert_sw_histoFilter";
    public static final String PREF_MZ =                    "pref_ps_mz";
    /**/public static final String MZ_SHOW =                "pref_mz_sw_mzDisplayFilter";
    /**/public static final String MZ_REMOVE_ALL =          "pref_mz_removeAll";
    public static final String PREF_PROFILE =               "pref_ps_profile";
    /**/public static final String PROFILE_ALIAS =          "pref_profile_sw_aliasFilter";
    /**/public static final String PROFILE_DISPLAY_NAME =   "pref_profile_txt_displayName";
    public static final String PREF_NOTIF =                 "pref_ps_notif";
    /**/public static final String NOTIF_ALERTS_FILTER =    "pref_notif_sw_mirrorAlertFilters";
    /**/public static final String NOTIF_SILENCE =          "pref_notif_sw_silence";
    /**/public static final String NOTIF_BLOCK =            "pref_notif_sw_block";
    public static final String PREF_GPS =                   "pref_ps_gps";
    /**/public static final String GPS_ACTIVATED =          "pref_gps_sw_activate";
    /**/public static final String GPS_PERMISSIONS =        "pref_gps_permissions";
    public static final String PREF_NETWORK =               "pref_ps_network";
    /**/public static final String NETWORK_REFRESH_RATE =   "pref_network_list_refresh_rate";
    /**/public static final String NETWORK_WIFI_ONLY =      "pref_network_sw_wifi_only";
    /**/public static final String NETWORK_AUTO_REFRESH =   "pref_network_list_auto_refresh";
    public static final String USER_ALERTS_PREFERENCES =    "pref_user_alert";
    public static final String PREF_MZ_DELETE_FLAG =        "pref_mz_removeAll_flag";

    public static final String ALERT_TRANSFER_MORE_INFO =   "alert_transfer_more_info";
    public static final String ALERT_MORE_INFO_BUNDLE =     "alert_moreinfo_bundle";

    public static final int NO_CURRENT_USER =           -1;
    public static final String BACK_TASK =                  "back_task";

    // requests
    public static final String CUSTOM_USER_HEADER =         "user";
    public static final String AUTH_HEADERS =               "Authorization";

    public static final String ALERT_SCORE_UPDATE =         "score";

    public static final String TEST_AUTH =                  "testAuth";

    public static final String PHOTO =                      "photo";
    public static final String UPLOAD =                     "upload";
    public static final String DOWNLOAD =                   "download";

    public static final String PHOTO_UPLOAD =                merge(PHOTO, UPLOAD);
    public static final String PHOTO_DOWNLOAD =              merge(PHOTO, DOWNLOAD);

    public static final String HISTO_FLOW =                 "histo_flow";


    private static String merge(String... tags){

        StringBuilder sb = new StringBuilder();

        for (String tag : tags){
            sb.append(tag);
            sb.append(DELIM);
        }
        sb.deleteCharAt(sb.lastIndexOf(DELIM));
        return sb.toString();
    }

}
