package com.acclimate.payne.simpletestapp.map;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.LocationManager;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;

import com.acclimate.payne.simpletestapp.R;
import com.acclimate.payne.simpletestapp.activities.main.MainActivity;
import com.acclimate.payne.simpletestapp.alerts.AlertWrapper;
import com.acclimate.payne.simpletestapp.alerts.BasicAlert;
import com.acclimate.payne.simpletestapp.alerts.UserAlert;
import com.acclimate.payne.simpletestapp.appUtils.AppTag;
import com.acclimate.payne.simpletestapp.map.pins.BasicPin;
import com.acclimate.payne.simpletestapp.map.pins.PinBundle;
import com.acclimate.payne.simpletestapp.map.pins.PinController;
import com.acclimate.payne.simpletestapp.map.pins.UserPin;
import com.acclimate.payne.simpletestapp.monitoredZones.MZController;
import com.acclimate.payne.simpletestapp.server.google.GoogleSearchRequest;

import org.osmdroid.bonuspack.clustering.RadiusMarkerClusterer;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.HashSet;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import lombok.Getter;
import lombok.Setter;

/**
 * Wrapper pour le map de OSMdroid
 * <p>
 * Created by Utilisateur on 2018-05-03.
 */

public class MapDisplay {

    public static final BoundingBox QUEBEC_BOUNDING_BOX = new BoundingBox(63,-58,40,-84);

    @Getter
    public MapView map;
    private SharedPreferences sharedPreferences;

    @Getter
    private PinController pinController;
    @Getter
    private MZController zoneController;

    // Histo Clusters
    @Getter public RadiusMarkerClusterer histoClusters;
    public static Bitmap histoClusterIcon;

    // Position GPS
    private MyLocationNewOverlay locationOverlay;
    @Getter @Setter
    private boolean showGps;



    public MapDisplay(MapView map, MainActivity activity) {
        this.map = map;

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        this.showGps = sharedPreferences.getBoolean(AppTag.GPS_ACTIVATED, true);
        if(showGps) initMyLocationNewOverlay(activity);

        pinController = new PinController(activity);
        zoneController = new MZController(map, activity);

        // Setting up the MarkerClusters
        histoClusters = new RadiusMarkerClusterer(activity);
        histoClusterIcon = ((BitmapDrawable)activity.getResources().getDrawable(R.drawable.marker_cluster)).getBitmap();
        histoClusters.setIcon(histoClusterIcon);
        map.getOverlayManager().add(histoClusters);
    }



    private void initializePins(AlertWrapper wrapper){
        pinController.updatePin(wrapper);
    }

    public PinBundle<BasicPin> updateLivePin(List<BasicAlert> liveAlerts){

        HashSet<BasicPin> livePins = pinController.createAllBasicPins(liveAlerts);
        return pinController.updateLivePins(livePins);

    }

    public PinBundle<UserPin> updateUserPin(List<UserAlert> userAlerts){

        HashSet<UserPin> livePins = pinController.createAllUserPin(userAlerts);
        return pinController.updateUserPins(livePins);

    }

    public PinBundle<BasicPin> updateHistoPin(LinkedBlockingQueue<BasicAlert> histoAlerts){

        HashSet<BasicPin> histoPins = pinController.createAllHistoPins(histoAlerts);
        return pinController.updateHistoPins(histoPins);

    }


    public void redraw(MainActivity mainActivity){
        //TODO : intelligently please :D
        map.getOverlayManager().clear();
        zoneController.redrawAllZones();
        pinController.drawAllPins();
        if(showGps) map.getOverlayManager().add(locationOverlay);
        map.getOverlays().add(mainActivity.getMapEventsOverlay());
    }

    /**
     * North, south, east, west
     *
     * ex:
     * TOP-LEFT COORD = {result[0], result[3]}
     * BOT-RIGHT COORD = {result[1], result[2]}
     *
     * @return
     */
    public double[] getBoundingBoxEdges() {
        BoundingBox current = map.getBoundingBox();
        double[] result = new double[4];

        result[0] = current.getLatNorth();
        result[1] = current.getLatSouth();
        result[2] = current.getLonEast();
        result[3] = current.getLonWest();

        return result;
    }


    /**
     * Pour centrer l'écran sur une BoundingBox retournée par une requête de recherche.
     */
    public void centerOnGoogleQuery(String query) {

        try {
            BoundingBox boundingBox = GoogleSearchRequest.getBoundingBox(query);

            if (boundingBox != null) {
                map.zoomToBoundingBox(boundingBox, false);
            } else {
                throw new Exception("null Bounding Box");
            }
        } catch (Exception e) {
            Snackbar.make(map, "Erreur durant le traitement de la requête", Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show();
            e.printStackTrace();
        }
    }

    private void initMyLocationNewOverlay(MainActivity mainActivity) {
        GpsMyLocationProvider provider = new GpsMyLocationProvider(mainActivity);
        provider.addLocationSource(LocationManager.NETWORK_PROVIDER);
        locationOverlay = new MyLocationNewOverlay(provider, map);
        map.getOverlayManager().add(locationOverlay);
    }



    /**
     * Enlarges a bounding box by adding the respective latitudinal and longitudinal span size
     * to everydirection. The methode will returns a new bounding of correct size.
     *
     * @param bBox the bBox to be enlarge
     * @return a new bBox enlarged from
     */
    public static BoundingBox enlarge(BoundingBox bBox){

        double deltaX, deltaY;
        deltaX = bBox.getLatitudeSpan();
        deltaY = bBox.getLongitudeSpan();
        return new BoundingBox(
                bBox.getLatNorth() - deltaX,
                bBox.getLonEast() - deltaY,
                bBox.getLatSouth() + deltaX,
                bBox.getLonWest() + deltaX);

    }

    public void setupDisplay(AlertWrapper wrapper, MainActivity mainActivity){

        initializePins(wrapper);
        redraw(mainActivity);

    }

    public void forceRedrawHisto(LinkedBlockingQueue<BasicAlert> histoAlertes){

        new Thread(() ->  {
            HashSet<BasicPin> pinSet = pinController.createAllHistoPins(histoAlertes);
            pinController.setHistPins(pinSet);
            map.getOverlayManager().removeAll(pinController.getHistPins());
            map.getOverlayManager().addAll(pinSet);
            }
        ).start();

    }

}
