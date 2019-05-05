package com.acclimate.payne.simpletestapp.map.pins;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;

import com.acclimate.payne.simpletestapp.R;
import com.acclimate.payne.simpletestapp.activities.main.MainActivity;
import com.acclimate.payne.simpletestapp.alerts.AlertTypes;
import com.acclimate.payne.simpletestapp.alerts.AlertWrapper;
import com.acclimate.payne.simpletestapp.alerts.BasicAlert;
import com.acclimate.payne.simpletestapp.alerts.UserAlert;
import com.acclimate.payne.simpletestapp.map.infoWindow.BasicInfoWindow;
import com.acclimate.payne.simpletestapp.map.infoWindow.UserInfoWindow;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.api.IMapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.Projection;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.infowindow.InfoWindow;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import lombok.Getter;
import lombok.Setter;

import static com.acclimate.payne.simpletestapp.appUtils.AppTag.ALERT_EARTH_FILTER;
import static com.acclimate.payne.simpletestapp.appUtils.AppTag.ALERT_FIRE_FILTER;
import static com.acclimate.payne.simpletestapp.appUtils.AppTag.ALERT_HISTO_FILTER;
import static com.acclimate.payne.simpletestapp.appUtils.AppTag.ALERT_USER_COPY_FILTER;
import static com.acclimate.payne.simpletestapp.appUtils.AppTag.ALERT_USER_FILTER;
import static com.acclimate.payne.simpletestapp.appUtils.AppTag.ALERT_WATER_FILTER;
import static com.acclimate.payne.simpletestapp.appUtils.AppTag.ALERT_WIND_FILTER;

/**
 * This class is responsible for all action revolving around the {@link Pin} class. Use it to
 * create, remove and update all the current {@link UserPin} and {@link BasicPin} displayed on
 * the {@link MapView} that are kept in memory in this class.
 */
public class PinController implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final int screen_offset_on_center_pin = 6;

    /**
     * a NON-STATIC reference to the {@link MainActivity} which contains the map on
     * which the pins should be displayed on.
     */
    private MainActivity ctx;

    /**
     * The set of all curent live pins.
     */
    private HashSet<BasicPin> livePins;

    /**
     * The set of all current user pins.
     */
    private HashSet<UserPin> userPins;

    /**
     * the set of all current historical pins.
     */
    @Getter @Setter private HashSet<BasicPin> histPins;


    /**
     * If this controller has been initilize on a first run of {@link MainActivity}. Used
     * to not call certain methods and processus when {@link MainActivity#onCreate(Bundle)}
     * is called multiple time during the App lifecycle
     */
    @Getter
    private boolean init;


    /**
     * This constructors sets the values of the instance parameters and creates empty
     * {@link HashSet} for the pins. {@link HashSet} are used for fast lookup times on
     * {@link HashSet#contains(Object)} when a large number of pins are loaded in memory.
     *
     * @param ctx the {@link MainActivity} that conatains the map. Required because the
     *            {@link InfoWindow} associated with a {@link Pin} needs to know about the activity on
     *            which to give feedback on user actions.
     */
    public PinController(MainActivity ctx) {
        this.ctx = ctx;
        this.init = false;

        livePins = new HashSet<>();
        userPins = new HashSet<>();
        histPins = new HashSet<>();

        SharedPreferences sP = PreferenceManager.getDefaultSharedPreferences(ctx);

        // Initialisation des valeurs de préférence des filtres
        PinPreferences.showUserPins = sP.getBoolean(ALERT_USER_FILTER, true);
        PinPreferences.mirrorAlertFilters = sP.getBoolean(ALERT_USER_COPY_FILTER, true);
        PinPreferences.feuFilter = sP.getBoolean(ALERT_FIRE_FILTER, true);
        PinPreferences.eauFilter = sP.getBoolean(ALERT_WATER_FILTER, true);
        PinPreferences.meteoFilter = sP.getBoolean(ALERT_WIND_FILTER, true);
        PinPreferences.terrainFilter = sP.getBoolean(ALERT_EARTH_FILTER, true);
        PinPreferences.historiqueFilter = sP.getBoolean(ALERT_HISTO_FILTER, false);

    }

    /**
     * Creates a single {@link UserPin} to be displayed on a map, based on an Alert
     *
     * @param userAlert the alert from which to create the pin. The information contained in
     *                  the {@link UserAlert} will be displayed in the
     *                  {@link com.acclimate.payne.simpletestapp.map.infoWindow.PinInfoWindow}
     *                  that is automatically created with each new pin. This information can
     *                  be accessed by a user with a single tap on the {@link UserPin}.
     * @param mapView   the {@link MapView} on which to display the created {@link UserPin}.
     * @return the pinm ready to be displayed.
     */
    private UserPin createUserPin(MapView mapView, UserAlert userAlert) {

        UserPin pin = new UserPin(mapView, userAlert);
        pin.setInfoWindow(new UserInfoWindow(userAlert, R.layout.infobubble_user, ctx));
        pin.setPinIcon(mapView.getContext());
        pin.setOnMarkerClickListener(this::onPinClick);
        pin.setAnchor(0.5f, 1f); // width, height
        return pin;

    }

    /**
     * Creates a single {@link BasicPin} to be displayed on a map, based on an Alert.
     * @param alert the alert from which to create the pin. The information contained in
     *                  the {@link BasicAlert} will be displayed in the
     *                  {@link com.acclimate.payne.simpletestapp.map.infoWindow.BasicInfoWindow}
     *                  that is automatically created with each new pin. This information can
     *                  be accessed by a user with a single tap on the {@link UserPin}.
     * @param mapView   the {@link MapView} on which to display the created {@link BasicPin}.
     * @return the pin ready to be displayed.
     */
    private BasicPin createActualPin(MapView mapView, BasicAlert alert) {

        BasicPin pin = new BasicPin(mapView, alert);
        pin.setInfoWindow(new BasicInfoWindow(alert, R.layout.infobubble_basic, ctx));
        pin.setPinIcon(mapView.getContext());
        pin.setOnMarkerClickListener(this::onPinClick);
        pin.setAnchor(0.5f, 1f); // width, height
        return pin;

    }


    /**
     * Creates all {@link UserPin} based on the content of a List of {@link UserAlert}.
     *
     * @param userAlerts the list containing all live alerts
     * @return a new set of pin to be displayed.
     */
    public HashSet<UserPin> createAllUserPin(List<UserAlert> userAlerts) {

        HashSet<UserPin> allUserPins = new HashSet<>();

        for (UserAlert userAlert : userAlerts) {
            allUserPins.add(createUserPin(ctx.getMapDisplay().getMap(), userAlert));
        }

        return allUserPins;

    }



    /**
     * Creates all historical pin based on the content of a List of alerts.
     *
     * @param liveAlerts the list containing all live alerts
     * @return a new set of pin to be displayed.
     */
    public HashSet<BasicPin> createAllBasicPins(List<BasicAlert> liveAlerts) {

        HashSet<BasicPin> allBasicPins = new HashSet<>();

        for (BasicAlert alert : liveAlerts) {
            allBasicPins.add(createActualPin(ctx.getMapDisplay().getMap(), alert));
        }

        return allBasicPins;
    }

    /**
     * Creates all historical pin based on the content of a List of alerts.
     *
     * @param liveAlerts the list containing all live alerts
     * @return a new set of pin to be displayed.
     */
    public HashSet<BasicPin> createAllHistoPins(LinkedBlockingQueue<BasicAlert> liveAlerts) {

        HashSet<BasicPin> allBasicPins = new HashSet<>();

        for (BasicAlert alert : liveAlerts) {

            BasicPin pin = createActualPin(ctx.getMapDisplay().getMap(), alert);
            if (checkDisplayFilters(pin)){
                allBasicPins.add(pin);
            }
        }

        return allBasicPins;
    }


    /**
     * creates and replace pins in memory with new pins based on the content of an alertWrapper
     *
     * @param wrapper the wrapper from which to look for alerts
     */
    public void updatePin(AlertWrapper wrapper) {

        histPins = createAllHistoPins(new LinkedBlockingQueue<>(Arrays.asList(wrapper.getHisto())));
        userPins = createAllUserPin(new ArrayList<>(Arrays.asList(wrapper.getUser())));
        livePins = createAllBasicPins(new ArrayList<>(Arrays.asList(wrapper.getLive())));

        init = true;
    }

    /**
     * draw all the pin on the map in MainActivity
     */
    public void drawAllPins() {

        for (BasicPin pin : livePins) {
            if (checkDisplayFilters(pin))
                pin.draw();
        }


        if (PinPreferences.showUserPins) {
            for (UserPin pin : userPins) {

                if (PinPreferences.mirrorAlertFilters) {
                    if (checkDisplayFilters(pin))
                        pin.draw();
                } else { // sinon on dessine toutes les alertes usagers
                    pin.draw();
                }
            }
        }


        if (PinPreferences.historiqueFilter) {
            for (BasicPin pin : histPins) {
                pin.draw();
            }
        }



    }


    /**
     * Calculates which historical pin to add and remove, base on the new set and the pins in memory
     *
     * @param newUserPins the new set of pins to be dealt with
     */
    public PinBundle<UserPin> updateUserPins(HashSet<UserPin> newUserPins) {

        // add new pins
        ArrayList<UserPin> toAdd = new ArrayList<>();
        for (UserPin pin : newUserPins) {
            if (!userPins.contains(pin) && checkDisplayFilters(pin)) {
                toAdd.add(pin);
            }
        }

        // remove old pins
        ArrayList<UserPin> toRemove = new ArrayList<>();
        for (UserPin pin : userPins) {
            if (!newUserPins.contains(pin)) {
                toRemove.add(pin);
            }
        }

        userPins.addAll(toAdd);
        userPins.removeAll(toRemove);

        return new PinBundle<>(toAdd, toRemove);


    }

    /**
     * Calculates which historical pin to add and remove, base on the new set and the pins in memory
     *
     * @param newLivePins the new set of pins to be dealt with
     */
    public PinBundle<BasicPin> updateLivePins(HashSet<BasicPin> newLivePins) {

        ArrayList<BasicPin> toAdd = new ArrayList<>();

        // add new pins
        for (BasicPin pin : newLivePins) {
            if (!livePins.contains(pin) && checkDisplayFilters(pin)) {
                toAdd.add(pin);
            }
        }

        // remove old pins
        ArrayList<BasicPin> toRemove = new ArrayList<>();
        for (BasicPin pin : livePins) {
            if (!newLivePins.contains(pin)) {
                toRemove.add(pin);
            }
        }

        livePins.addAll(toAdd);
        livePins.removeAll(toRemove);

        return new PinBundle<>(toAdd, toRemove);

    }


    /**
     * Calculates which historical pin to add and remove, base on the new set and the pins in memory
     *
     * @param newHistoPins the new set of pins to be dealt with
     */
    public PinBundle<BasicPin> updateHistoPins(HashSet<BasicPin> newHistoPins) {

//        Log.i(PIN_FLOW, "\n\nPIN HASH SET BEFORE LENGTH = " + histPins.size());

        ArrayList<BasicPin> toAdd = new ArrayList<>();
        // add new pins
        for (BasicPin pin : newHistoPins) {
            if (!livePins.contains(pin)) {
                toAdd.add(pin);
            }
        }

        // remove old pins
        ArrayList<BasicPin> toRemove = new ArrayList<>();
        for (BasicPin pin : histPins) {
            if (!newHistoPins.contains(pin)) {
                toRemove.add(pin);
            }
        }

//        Log.i(PIN_FLOW, "\npins to add size = " + toAdd.size());
//        Log.i(PIN_FLOW, "\npins to remove size = " + toRemove.size());

        histPins.addAll(toAdd);
        histPins.removeAll(toRemove);

//        Log.i(PIN_FLOW, "\nPIN HASH SET AFTER LENGTH = " + histPins.size());

        return new PinBundle<>(toAdd, toRemove);

    }


    /**
     * Vérifie les Préférences de l'appareil pour dire si cette 'pin' devrait être affichée.
     *
     * @param pin the pin to chekck if it needs to be displayed.
     * @return 'true' si ça devrait être affiché
     */
    private boolean checkDisplayFilters(BasicPin pin) {
        return ((PinPreferences.feuFilter && pin.alerte.getEnumType() == AlertTypes.FEU) ||
                (PinPreferences.eauFilter && pin.alerte.getEnumType() == AlertTypes.EAU) ||
                (PinPreferences.meteoFilter && pin.alerte.getEnumType() == AlertTypes.METEO) ||
                (PinPreferences.terrainFilter && pin.alerte.getEnumType() == AlertTypes.TERRAIN));
    }

    /**
     * Vérifie les Préférences de l'appareil pour dire si cette 'pin' devrait être affichée.
     *
     * @param pin the pin to chekck if it needs to be displayed
     * @return 'true' si ça devrait être affiché
     */
    private boolean checkDisplayFilters(UserPin pin) {
        return ((PinPreferences.feuFilter && pin.alerte.getEnumType() == AlertTypes.USER_FEU) ||
                (PinPreferences.eauFilter && pin.alerte.getEnumType() == AlertTypes.USER_EAU) ||
                (PinPreferences.meteoFilter && pin.alerte.getEnumType() == AlertTypes.USER_METEO) ||
                (PinPreferences.terrainFilter && pin.alerte.getEnumType() == AlertTypes.USER_TERRAIN));
    }


    /**
     * Default methods for implementing the action associated with a
     * single tap on the pin by the user.
     *
     * @param marker  the marker that has been tapped on.
     * @param mapView the map
     * @return {@code true} because the action has been consumed
     * and should not be passed to other views.
     */
    private boolean onPinClick(Marker marker, MapView mapView) {

        InfoWindow.closeAllInfoWindowsOn(mapView);
        marker.showInfoWindow();

        // center not on pin, but on infoWindow

        // 1. transform geoPoint Center into screen pixel position
        Projection proj = mapView.getProjection();
        android.graphics.Point pix = proj.toPixels(marker.getPosition(), null);

        // 2. get phone screen heigth in pixels
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ctx.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics); // updates the displayMetrics instance passed as argument

        // 3. calculate offset geoPoint from screen pixels
        int height = displayMetrics.heightPixels;
        int heightOffset = pix.y - height/screen_offset_on_center_pin;
        IGeoPoint offsetPoint = proj.fromPixels(pix.x, heightOffset);

        // 4. animate screen
        IMapController controller = mapView.getController();
        controller.animateTo(offsetPoint, mapView.getZoomLevelDouble(), 400L);

        return true;

    }


    /**
     * Updates {@link PinPreferences} with new values on preferences change
     *
     * @param sharedPreferences the {@link SharedPreferences} instances.
     * @param key the SharedPreferences key on which a change has been recorded.
     */
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        switch (key) {
            case ALERT_USER_FILTER:
                PinPreferences.showUserPins = sharedPreferences.getBoolean(key, true);
                break;
            case ALERT_USER_COPY_FILTER:
                PinPreferences.mirrorAlertFilters = sharedPreferences.getBoolean(key, true);
                break;
            case ALERT_FIRE_FILTER:
                PinPreferences.feuFilter = sharedPreferences.getBoolean(key, true);
                break;
            case ALERT_WATER_FILTER:
                PinPreferences.eauFilter = sharedPreferences.getBoolean(key, true);
                break;
            case ALERT_WIND_FILTER:
                PinPreferences.meteoFilter = sharedPreferences.getBoolean(key, true);
                break;
            case ALERT_EARTH_FILTER:
                PinPreferences.terrainFilter = sharedPreferences.getBoolean(key, true);
                break;
            case ALERT_HISTO_FILTER :
                PinPreferences.historiqueFilter = sharedPreferences.getBoolean(key, false);
                break;
        }

        ctx.getMapDisplay().redraw(ctx);

    }

    /**
     * Simple wrapper around all pin preferences values.
     */
    public static class PinPreferences implements Serializable {

        // Display filters
        public static boolean showUserPins;
        public static boolean mirrorAlertFilters;
        public static boolean feuFilter;
        public static boolean eauFilter;
        public static boolean terrainFilter;
        public static boolean meteoFilter;
        public static boolean historiqueFilter;

    }

}
