package com.acclimate.payne.simpletestapp.activities.main;

import android.content.SharedPreferences;
import android.util.Log;

import com.acclimate.payne.simpletestapp.alerts.BasicAlert;
import com.acclimate.payne.simpletestapp.alerts.alertViewModel.AlertViewModel;
import com.acclimate.payne.simpletestapp.appUtils.AppTag;
import com.acclimate.payne.simpletestapp.server.Server;
import com.acclimate.payne.simpletestapp.server.requests.AlertRequestType;
import com.acclimate.payne.simpletestapp.server.requests.GetRequest;
import com.acclimate.payne.simpletestapp.server.requests.HttpRequest;
import com.acclimate.payne.simpletestapp.server.requests.RequestHandler;
import com.acclimate.payne.simpletestapp.server.requests.exceptions.RequestErrorException;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.events.DelayedMapListener;
import org.osmdroid.events.MapAdapter;
import org.osmdroid.events.ScrollEvent;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.views.MapView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.LinkedBlockingQueue;

import static com.acclimate.payne.simpletestapp.alerts.alertViewModel.AlertViewModel.MAX_HISTO_PIN_QUEUE_CAPACITY;
import static com.acclimate.payne.simpletestapp.appUtils.AppTag.PIN_FLOW;

public class HistoricalPinUpdater implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final long DEFAULT_INACTIVITY_DELAY_IN_MILLISECS = 250;
    private static final long MIN_ZOOM = 10;

    private boolean active;
    private static BoundingBox lastRequestedBox;

    private AlertViewModel alertViewModel;

    HistoricalPinUpdater(AlertViewModel alertViewModel, boolean active){
        this.alertViewModel = alertViewModel;
        this.active = active;
    }


    public DelayedMapListener defaultListener(){
        return new DelayedMapListener(new HistoUpdateScrollEvent(), DEFAULT_INACTIVITY_DELAY_IN_MILLISECS);
    }


    private void sendRequest(BoundingBox boxToEnlarge){
        lastRequestedBox = boxToEnlarge;
        Log.i(PIN_FLOW, "sending histo request");
        HttpRequest<BasicAlert[]> histoRequest = GetRequest.alert(AlertRequestType.HISTORICAL, boxToEnlarge);
        RequestHandler<BasicAlert[]> handler = new RequestHandler<>(histoRequest, this::onAlertReceived, this::onRequestFailed);
        handler.handle(Server.AuthorizationHeaders.NONE);
    }


    private void onAlertReceived(BasicAlert[] histoAlerts){
        Log.i(PIN_FLOW, "request success ! Histo alerts length = " + histoAlerts.length);

        LinkedBlockingQueue<BasicAlert> allNewHistoAlerts;

        // si la taille des alertes du serveur est plus grande que la taille maximale,
        // remplacer toute la queue par une nouvelle queue
        if (histoAlerts.length > MAX_HISTO_PIN_QUEUE_CAPACITY) {
            allNewHistoAlerts = new LinkedBlockingQueue<>(MAX_HISTO_PIN_QUEUE_CAPACITY);
            BasicAlert[] alertToKeep = Arrays.copyOfRange(histoAlerts, 0, MAX_HISTO_PIN_QUEUE_CAPACITY);
            allNewHistoAlerts.addAll(new ArrayList<>(Arrays.asList(alertToKeep)));
            alertViewModel.getHistAlerts().setValue(allNewHistoAlerts);
            return;
        }

        allNewHistoAlerts = alertViewModel.getHistAlerts().getValue();
        int remainingSpace = MAX_HISTO_PIN_QUEUE_CAPACITY - (histoAlerts.length + allNewHistoAlerts.size());

        // si iln'y a pas assez de place dans la queue, dequeue le nombre approprié.
        if (remainingSpace < 0){
            for (int i = remainingSpace; i < 0; i++){
                allNewHistoAlerts.poll();
            }
        }

        allNewHistoAlerts.addAll(new ArrayList<>(Arrays.asList(histoAlerts)));
        Log.i(PIN_FLOW, "\nCURRENT ALERT VIEW MODEL SIZE = " + allNewHistoAlerts.size());
        alertViewModel.getHistAlerts().setValue(allNewHistoAlerts);


    }


    private void onRequestFailed(RequestErrorException e){
        Log.e(PIN_FLOW, "erreur requête histo = " + e.prettyPrinter());
    }


    private class HistoUpdateScrollEvent extends MapAdapter {

        @Override
        public boolean onScroll(ScrollEvent event) {

            MapView map = event.getSource();
            double zoomLvl = map.getZoomLevelDouble();
            IGeoPoint currentCenter = map.getMapCenter();


            if (zoomLvl > MIN_ZOOM && active ){
                if (lastRequestedBox == null) {
                    lastRequestedBox = map.getBoundingBox();
                }
                Log.i(PIN_FLOW, "BOX CONTAINS CENTRE ? : " + lastRequestedBox.contains(currentCenter));
                if (!lastRequestedBox.contains(currentCenter)) {
                    BoundingBox currentBox = map.getBoundingBox();
                    sendRequest(currentBox);
                }
            }

            return super.onScroll(event);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(key.equals(AppTag.ALERT_HISTO_FILTER)) {
            active = sharedPreferences.getBoolean(key, false);
        }
    }


}
