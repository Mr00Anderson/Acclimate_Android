package com.acclimate.payne.simpletestapp.monitoredZones;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.util.Log;

import com.acclimate.payne.simpletestapp.appUtils.App;
import com.acclimate.payne.simpletestapp.appUtils.AppConfig;
import com.acclimate.payne.simpletestapp.appUtils.AppTag;
import com.acclimate.payne.simpletestapp.deviceStorage.localStorage.FileLocalStorage;
import com.acclimate.payne.simpletestapp.server.Server;
import com.acclimate.payne.simpletestapp.server.requests.DeleteRequest;
import com.acclimate.payne.simpletestapp.server.requests.HttpRequest;
import com.acclimate.payne.simpletestapp.server.requests.PostRequest;
import com.acclimate.payne.simpletestapp.server.requests.RequestHandler;
import com.google.firebase.auth.FirebaseUser;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import java.io.IOException;
import java.util.ArrayList;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;


public class MZController {

    @Getter
    private Activity activity;
    @Getter
    private MapView mapView;
    private SharedPreferences sP;


    /**
     * If the monitored zones should be displayed on the map
     */
    @Setter
    private boolean display;

    /**
     * The list of zones that should be displayed on the {@link org.osmdroid.views.MapView}.
     */
    @Getter
    private ArrayList<DisplayZone> zones = new ArrayList<>();



    public MZController(MapView map, @NonNull final Activity activity) {
        this.mapView = map;
        this.activity = activity;

        this.sP = PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext());
        display = sP.getBoolean(AppTag.MZ_SHOW, true);

        setUpZones();
    }

    /**
     * If zones are already loaded in memory, they are drawn.
     *
     * If not, the UID is used to load them temporarily (in blue).
     * The App singleton is asynchronously sending a request to update
     * this info with the latest server's response (redrawn in red).
     */
    public void setUpZones() {

        String blueZoneMsg = "Tentative de synchronisation de vos zones (bleues) avec le serveur";
        MonitoredZoneWrapper currentUserZones = App.getInstance().getCurrentUserZones();

        if (currentUserZones.getZones().size() != 0) {
            // A user is authenticated and has at least 1 zone

            boolean isSynced = App.getInstance().isSyncedWithServer();
            if(!isSynced) {
                // this can happen when the FbUser exists, but there is no connection
                // but also if the GET request hasn't yet come back from the server
                Snackbar.make(mapView, blueZoneMsg, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }

            Log.w("setUpZones", "initializing the zones with sync value: " + isSynced);
            initializeAndDrawZones(currentUserZones, isSynced);

        }
    }


    public void redrawAllZones() {
        undrawAllZones();
        if (display) {
            mapView.getOverlayManager().addAll(0, zones);
            mapView.invalidate();
        }
    }

    public void undrawAllZones() {
        mapView.getOverlayManager().removeAll(zones);
        mapView.invalidate();
    }

    /**
     * Also clears the list of DisplayZones to be drawn eventually
     */
    public void clearAndUndrawZones() {
        undrawAllZones();
        zones.clear();
    }

    public void undrawOneZone(DisplayZone zone) {
        mapView.getOverlayManager().remove(zone);
        mapView.invalidate();
    }


    /**
     * Removes the oldZone (if it exists) and replaces it with the newZone.
     * This avoid having to 'redraw' everything.
     *
     * @param oldZone can be 'null'
     * @param newZone must be a proper DisplayZone (will be displayed)
     */
    public void refreshOneZone(DisplayZone oldZone, DisplayZone newZone) {
        if(oldZone != null) {
            zones.remove(oldZone);
            mapView.getOverlayManager().remove(oldZone);
        }
        if (display) {
            zones.add(newZone);
            mapView.getOverlayManager().add(newZone);
            mapView.invalidate();
        }
    }

    /**
     * Utilisé pour rafraîchir l'affichage pendant la visualisation temporaire d'une MZ.
     * En faisant grossir le rayon de la MZ circulaire, il faut bel et bien recréer l'objet
     * pour que la carte puisse se rafraîchir correctement.
     *
     * @param displayZone
     */
    public void redrawAllZonesButOne(DisplayZone displayZone) {
        undrawAllZones();
        zones.remove(displayZone);
        if (display) {
            mapView.getOverlayManager().addAll(zones);
            Log.w("redrawAllZonesButOne", "triggered with this many zones to draw: " + zones.size());
            mapView.invalidate();
        }
    }



    /**
     * Generates a DisplayZone from a MonitoredZone.
     * Does not have any other side effects.
     *
     * @param mz a MonitoredZone
     * @return a DisplayZone
     */
    public DisplayZone generateDisplayZone(MonitoredZone mz) {

        String zoneType = mz.getGeometry().getType();
        DisplayZone zone;

        // response from server is a String ... TODO: make more robust with enum ???
        switch (zoneType) {

            case "Point": // circle MZ

                double radiusInMeters = mz.getRadius();
                GeoPoint center = new GeoPoint(mz.getGeometry().getLat(), mz.getGeometry().getLng());
                zone = new ZoneCircle(radiusInMeters, center, mz);
                break;

            case "Polygon":
            default: // Box MZ

                double[][] polyCoord = mz.getGeometry().getPolyCoordinates();

                GeoPoint nW = new GeoPoint(polyCoord[0][0], polyCoord[0][1]);
                GeoPoint sE = new GeoPoint(polyCoord[1][0], polyCoord[1][1]);
                GeoPoint nE = new GeoPoint(polyCoord[0][0], polyCoord[1][1]);
                GeoPoint sW = new GeoPoint(polyCoord[1][0], polyCoord[0][1]);

                zone = new ZoneBox(nW, sE, nE, sW, mz);
                break;
        }

        return zone;
    }

    /**
     * Affiche une série de MZ.
     *
     * @param monitoredZoneWrapper les MZ à afficher
     * @param synced si 'false' alors elles seront bleues temporairement
     */
    public void initializeAndDrawZones(MonitoredZoneWrapper monitoredZoneWrapper, boolean synced) {

        clearAndUndrawZones();

        for (MonitoredZone monitoredZone : monitoredZoneWrapper.getZones()) {
            DisplayZone toAdd = generateDisplayZone(monitoredZone);
            zones.add(toAdd);

            if(!synced) {
                toAdd.setTemporaryColor();
            } else {
                toAdd.setUpInfoWindow(this);
            }
        }
        redrawAllZones();

        Log.w("App() Server Request", "Initialized zones length: " + zones.size());
    }

    /**
     * Creates an instance of a DisplayZone from a monitored monitoredZone.
     * Also saves (POST to server and save to internal memory)
     * and displays this monitoredZone.
     *
     * @param mz
     * @return
     */
    public DisplayZone createPostSaveDisplayZone(MonitoredZone mz) {

        DisplayZone zone = generateDisplayZone(mz);
        this.addSaveDisplayZone(zone);

        return zone;
    }


    /**
     * POST of the added MZ.
     * Displays a temporary preview until ajusting it based on the server's response.
     *
     * @param zone
     */
    public void addSaveDisplayZone(DisplayZone zone) {

        Snackbar.make(mapView, "Veuillez patienter.", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();

        final MonitoredZone monitoredZone = zone.getMonitoredZone();

        // Pour temporairement afficher la Zone qui est traitée
        this.zones.add(zone);
        zone.setTemporaryColor();
        redrawAllZones();

        // POST MZ : returns a MZ with ID
        HttpRequest<MonitoredZone> zonePost = PostRequest.zone(monitoredZone);
        new RequestHandler<MonitoredZone>(zonePost,

                response -> {
                    MonitoredZoneWrapper zoneWrapper = App.getInstance().getCurrentUserZones();

                    zoneWrapper.addMonitoredZone(response);
                    writeWrapper(zoneWrapper); // Saving the new list of MZ for this user

                    // Display confirmed color and infoWindow
                    zone.revertNormalColor();
                    zone.setMonitoredZone(response);
                    zone.setUpInfoWindow(this);
                    Snackbar.make(mapView, "Ajoutée avec succès! :)", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    mapView.invalidate();
                },

                error -> {
                    // Enlever la monitoredZone qui a causé un problème
                    redrawAllZonesButOne(zone);
                    Log.e("addSaveDisplayZone", "error callback " + error.prettyPrinter());
                    Snackbar.make(mapView, "Erreur durant le traitement de votre demande", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
        ).handle(Server.AuthorizationHeaders.REQUIRED);
    }

    /**
     * Sends a DELETE request and acts accordingly.
     * If successful, the internal memory is adjusted.
     *
     * @param zone
     */
    public void deleteOneZone(DisplayZone zone) {

        // Visual feedback
        zone.setTemporaryColor();
        Snackbar.make(mapView, "Veuillez patienter.", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();

        // DELETE request
        HttpRequest<String> delReq = DeleteRequest.zone(zone.getMonitoredZone().getZoneId() + "");
        new RequestHandler<>(delReq,
                response -> {
                    Snackbar.make(mapView, "Supprimée avec succès! :)", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();

                    MonitoredZoneWrapper zoneWrapper = App.getInstance().getCurrentUserZones();

                    zoneWrapper.removeDisplayZone(zone);
                    zones.remove(zone);
                    undrawOneZone(zone);
                    zone.getInfoWindow().close();

                    writeWrapper(zoneWrapper); // saving new list
                },
                error -> {
                    Log.e("DELETE one MZ", "error callback " + error.prettyPrinter());
                    Snackbar.make(mapView, "Erreur lors de la suppression", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    zone.revertNormalColor();
                    zone.getZoneInfoWindow().enableDeleteBtns();
                    mapView.invalidate();
                }
        ).handle(Server.AuthorizationHeaders.REQUIRED);
    }


    /**
     * Consider prompting the user for confirmation beforehand.
     *
     * @param user
     */
    public void deleteAllZones(FirebaseUser user) {

        // DELETE ALL MZ
        HttpRequest<String> delReq = DeleteRequest.allzones(user.getUid());
        new RequestHandler<>(delReq,

                response -> {
                    MonitoredZoneWrapper zoneWrapper = App.getInstance().getCurrentUserZones();

                    zoneWrapper.getZones().clear();
                    clearAndUndrawZones();
                    writeWrapper(zoneWrapper); // saving empty list
                },

                error -> {
                    Log.e("DELETE ALL MZ", "error callback " + error.prettyPrinter());
                    Snackbar.make(mapView, "Erreur lors de l'essai de suppression des zones", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
        ).handle(Server.AuthorizationHeaders.REQUIRED);
    }

    /**
     * To save in memory the MzWrapper
     *
     * @param zoneWrapper
     */
    private void writeWrapper(MonitoredZoneWrapper zoneWrapper) {
        String filename = AppConfig.getMonitoredZoneFilename();
        final FileLocalStorage<MonitoredZoneWrapper> zoneStorage =
                new FileLocalStorage<>(filename, zoneWrapper, activity.getApplicationContext());
        try {
            zoneStorage.setData(zoneWrapper);
            zoneStorage.write();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
