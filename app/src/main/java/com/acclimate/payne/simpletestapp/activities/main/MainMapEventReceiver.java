package com.acclimate.payne.simpletestapp.activities.main;

import com.acclimate.payne.simpletestapp.R;
import com.acclimate.payne.simpletestapp.authentification.AuthUIActivity;

import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.infowindow.InfoWindow;

public class MainMapEventReceiver implements MapEventsReceiver {

    private MainActivity main;
    private MapView mMapView;

    public MainMapEventReceiver(MainActivity activity, MapView map){
        main = activity;
        mMapView = map;
    }


    @Override
    public boolean singleTapConfirmedHelper(GeoPoint p) {
//        Log.i(AppTag.MAP, "Single tap at " + p.toIntString());
        InfoWindow.closeAllInfoWindowsOn(mMapView);
        return false;

    }


    @Override
    public boolean longPressHelper(GeoPoint p) {

        MainActivityHelper helper = new MainActivityHelper();
//        Log.i(AppTag.MAP, "Long pressed");

        if (AuthUIActivity.mAuth.getCurrentUser() != null) {
            helper.switchToUserAlertForm(main, p);

        } else {
            helper.focusOnUserAuth(main, main.getString(R.string.required_auth_for_alerts));
        }

        return false;

    }

}
