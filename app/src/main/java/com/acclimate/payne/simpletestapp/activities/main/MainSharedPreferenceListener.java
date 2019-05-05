package com.acclimate.payne.simpletestapp.activities.main;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;

import com.acclimate.payne.simpletestapp.map.MapDisplay;
import com.google.firebase.auth.FirebaseAuth;

import static com.acclimate.payne.simpletestapp.appUtils.AppTag.GPS_ACTIVATED;
import static com.acclimate.payne.simpletestapp.appUtils.AppTag.MZ_SHOW;
import static com.acclimate.payne.simpletestapp.appUtils.AppTag.PREF_MZ_DELETE_FLAG;


public class MainSharedPreferenceListener implements OnSharedPreferenceChangeListener {

    private MainActivity mainActivity;

    MainSharedPreferenceListener(MainActivity mainActivity){
        this.mainActivity = mainActivity;
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        // ugly switch case ... but there is nothing better we can do
        switch (key) {

            case MZ_SHOW:
                showMZ(sharedPreferences.getBoolean(MZ_SHOW, true));
                break;

            case PREF_MZ_DELETE_FLAG:
                if (sharedPreferences.getBoolean(PREF_MZ_DELETE_FLAG, false)){
                    removeMZ();
                }
                sharedPreferences.edit().putBoolean(PREF_MZ_DELETE_FLAG, false).apply();
                break;

            case GPS_ACTIVATED:
                gps();
                break;

        }

    }


    private void showMZ(boolean show){
        mainActivity.getMapDisplay().getZoneController().setDisplay(show);
        mainActivity.setNeedRedraw(true);
    }


    private void removeMZ(){
//        Log.i(APP_FLOW,"REMOVE MZ");
        MapDisplay mapDisplay = mainActivity.getMapDisplay();
        if (mapDisplay != null)
            mapDisplay.getZoneController().deleteAllZones(FirebaseAuth.getInstance().getCurrentUser());
    }


    private void gps(){
        MapDisplay map = mainActivity.getMapDisplay();
        map.setShowGps(!map.isShowGps());
    }

}
