package com.acclimate.payne.simpletestapp.deviceStorage.preferences;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;

import com.acclimate.payne.simpletestapp.R;
import com.acclimate.payne.simpletestapp.appUtils.App;
import com.acclimate.payne.simpletestapp.appUtils.AppConfig;
import com.acclimate.payne.simpletestapp.appUtils.AppTag;
import com.acclimate.payne.simpletestapp.appUtils.InternetCheck;
import com.acclimate.payne.simpletestapp.map.pins.PinController;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static com.acclimate.payne.simpletestapp.appUtils.AppTag.PREF_MZ_DELETE_FLAG;

/*
    Android stores Shared Preferences settings as XML file in shared_prefs folder under
    DATA/data/{application package} directory.
    The DATA folder can be obtained by calling Environment.getDataDirectory().

    WRITE EXAMPLE:
        SharedPreferences.Editor editor = mZ_sP.edit();
        editor.putString(key, "newHighScore");
        editor.apply();

    READ EXAMPLE:
        String highScore = mZ_sP.getString(key, defaultValue);
     */

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsPrefFragment extends PreferenceFragment {

    private SharedPreferences sharedPref;
    private InternetCheck internetCheck;

    public SettingsPrefFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());

        /*
        HOW TO ACCESS PREFERENCE SETTINGS VALUES:

            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
            boolean blockNotifs = sharedPref.getBoolean("pref_notif_sw_block", false);

        ".getBoolean" can be ".getString", etc. (all primitives are available)
        The second parameter is a default value in case the system can't find the 'key'
        or if it contains no value.
         */

        //  [ Setting up the click listeners ]

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        /*
        Keys hierarchy:

            pref_ps_alert
                pref_alert_sw_userFilter
                pref_alert_sw_userReplicateFilters
                pref_alert_cb_fireFilter
                pref_alert_cb_waterFilter
                pref_alert_cb_windFilter
                pref_alert_cb_earthFilter
                pref_alert_sw_histoFilter
            pref_ps_mz
                pref_mz_sw_mzDisplayFilter
                pref_mz_removeAll
            pref_ps_profile
                pref_profile_sw_aliasFilter
                pref_profile_txt_displayName
            pref_ps_notif
                pref_notif_sw_mirrorAlertFilters
                pref_notif_sw_noise
                pref_notif_sw_block
            pref_ps_gps
                pref_gps_sw_activate
                pref_gps_permissions
            pref_ps_network
                pref_network_list_refresh_rate
                pref_network_sw_wifi_only
                pref_network_list_auto_refresh
         */


        /*
            Filtres d'alertes
         */

        // Affichage des alertes d'usagers
        findPreference(AppTag.ALERT_USER_FILTER).setOnPreferenceClickListener(preference -> {
            PinController.PinPreferences.showUserPins =
                    sharedPref.getBoolean(AppTag.ALERT_USER_FILTER, true);
            return true;
        });

        // Répliquer les préférences d'affichage d'alertes officielles sur celles des usagers
        findPreference(AppTag.ALERT_USER_COPY_FILTER).setOnPreferenceClickListener(preference -> {
            PinController.PinPreferences.mirrorAlertFilters =
                    sharedPref.getBoolean(AppTag.ALERT_USER_COPY_FILTER, true);
            return true;
        });

        // Affichage des alertes de type 'feu'
        findPreference(AppTag.ALERT_FIRE_FILTER).setOnPreferenceClickListener(preference -> {
            PinController.PinPreferences.feuFilter =
                    sharedPref.getBoolean(AppTag.ALERT_FIRE_FILTER, true);
            return true;
        });

        // Affichage des alertes de type 'eau'
        findPreference(AppTag.ALERT_WATER_FILTER).setOnPreferenceClickListener(preference -> {
            PinController.PinPreferences.eauFilter =
                    sharedPref.getBoolean(AppTag.ALERT_WATER_FILTER, true);
            return true;
        });

        // Affichage des alertes de type 'vent'
        findPreference(AppTag.ALERT_WIND_FILTER).setOnPreferenceClickListener(preference -> {
            PinController.PinPreferences.meteoFilter =
                    sharedPref.getBoolean(AppTag.ALERT_WIND_FILTER, true);
            return true;
        });

        // Affichage des alertes de type 'terrain'
        findPreference(AppTag.ALERT_EARTH_FILTER).setOnPreferenceClickListener(preference -> {
            PinController.PinPreferences.terrainFilter =
                    sharedPref.getBoolean(AppTag.ALERT_EARTH_FILTER, true);
            return true;
        });

        // Affichage des alertes de type 'historique'
        findPreference(AppTag.ALERT_HISTO_FILTER).setOnPreferenceClickListener(preference -> {
            PinController.PinPreferences.historiqueFilter =
                    sharedPref.getBoolean(AppTag.ALERT_HISTO_FILTER, false);
            return true;
        });




        /*
            Monitored Zones
         */

        Preference mzPref = findPreference(AppTag.PREF_MZ);
        mzPref.setEnabled(false);
        Preference mzRemoveAll = findPreference(AppTag.MZ_REMOVE_ALL);
        mzRemoveAll.setEnabled(false);

        // Enabling access only if authenticated
        if(user != null) mzPref.setEnabled(true);

 //       Log.i(APP_FLOW, "internet : " + InternetCheck.synchronous(getActivity()));
        if (InternetCheck.synchronous(getActivity())
                && App.getInstance().getCurrentUserZones().getZones().size() > 0) {
            mzRemoveAll.setEnabled(true);
        }

        // Removing all MonitoredZones
        mzRemoveAll.setOnPreferenceClickListener(preference -> {

            new AlertDialog.Builder(getActivity())
                .setMessage("Voulez-vous vraiment effacer toutes vos zones surveillées?")
                .setNegativeButton("Annuler", (dialog, which) -> dialog.cancel())
                .setPositiveButton("Oui", (arg0, arg1) -> {

                    sharedPref.edit().putBoolean(PREF_MZ_DELETE_FLAG, true).apply();
                    mzRemoveAll.setEnabled(false);
                    sharedPref.edit().putBoolean(PREF_MZ_DELETE_FLAG, false).apply();
                }).show();

            return true;
        });




        /*
            Profile
         */

        Preference profilePref = findPreference(AppTag.PREF_PROFILE);
        profilePref.setEnabled(false);

        // Enabling access only if authenticated
        if(user != null) {
            profilePref.setEnabled(true);
        }

        // Affichage de l'alias au lieu du uID sur les alertes de l'usager
        findPreference(AppTag.PROFILE_ALIAS).setOnPreferenceClickListener(preference -> {
            // TODO
            return true;
        });

        // Changer son alias (seulement accessible si son affichage a été activé)
        findPreference(AppTag.PROFILE_DISPLAY_NAME).setOnPreferenceChangeListener((preference, newValue) -> {
            // TODO
 //           Log.w("test","change: " + newValue.toString());
            return true;
        });




        /*
            Notifications
         */

        Preference notifPref = findPreference(AppTag.PREF_NOTIF);
        notifPref.setEnabled(false);

        // Enabling access only if authenticated
        if(user != null) {
            notifPref.setEnabled(true);
        }




        /*
            GPS
         */

        // Lancer les settings de permissions de l'application
        findPreference(AppTag.GPS_PERMISSIONS).setOnPreferenceClickListener(preference -> {
            Intent i = new Intent();
            i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            i.addCategory(Intent.CATEGORY_DEFAULT);
            i.setData(Uri.parse("package:" + getActivity().getPackageName()));
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            return true;
        });




        /*
            Réseau
         */

        // Minimal threshold for a new request to be issues to update information
        findPreference(AppTag.NETWORK_REFRESH_RATE).setOnPreferenceChangeListener((preference, newValue) -> {
            int prefNetworkListRefreshRate = Integer.parseInt((String) newValue);
            AppConfig.REQUEST_TIME_TRESHOLD = prefNetworkListRefreshRate * 60L * 1000L;
            return true;
        });

        // Fetching alerts-data only through WiFi
        findPreference(AppTag.NETWORK_WIFI_ONLY).setOnPreferenceChangeListener((preference, newValue) -> {
            boolean prefNetworkWifiOnly = sharedPref.getBoolean(AppTag.NETWORK_WIFI_ONLY, false);
            // TODO (Olivier)
            return true;
        });



        // [ End of click listeners ]



/*
Example of how to populate programmatically the 'preferences.xml' file:

        PreferenceScreen screen = getPreferenceManager().createPreferenceScreen(getActivity());

        PreferenceCategory category = new PreferenceCategory(getActivity());
        category.setTitle("category name");

        screen.addPreference(category);

        CheckBoxPreference checkBoxPref = new CheckBoxPreference(getActivity());
        checkBoxPref.setTitle("title");
        checkBoxPref.setSummary("summary");
        checkBoxPref.setChecked(true);

        category.addPreference(checkBoxPref);
        setPreferenceScreen(screen);

        addPreferencesFromResource(R.xml.preferences);



---------------
OTHER SOLUTION (the one before would add a category at the top of first PreferenceScreen...): UNTESTED

     for(int x = 0; x < getPreferenceScreen().getPreferenceCount(); x++){
        PreferenceCategory lol = (PreferenceCategory) getPreferenceScreen().getPreference(x);
        for(int y = 0; y < lol.getPreferenceCount(); y++){
            Preference pref = lol.getPreference(y);
            pref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener(){

                @Override
                public boolean onPreferenceClick(Preference preference) {
                    return false;
                }

            });
        }
    }
*/
    }
}
