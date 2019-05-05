package com.acclimate.payne.simpletestapp.monitoredZones;

import android.graphics.Color;

import com.acclimate.payne.simpletestapp.R;

import org.osmdroid.views.overlay.Polygon;

import lombok.Getter;
import lombok.Setter;

public abstract class DisplayZone extends Polygon {

    @Getter @Setter
    protected MonitoredZone monitoredZone;
    @Getter
    protected ZoneInfoWindow zoneInfoWindow;

    public DisplayZone(MonitoredZone zone){
        this.monitoredZone = zone;

        // style
        this.setFillColor(Color.argb(75, 255, 0, 0));
        this.setStrokeColor(Color.argb(75, 255, 100, 0));
        this.setStrokeWidth(0);
    }


    /**
     * Not set up immediately because some times DisplayZone are loaded as
     * "previewer" or "not already synced", which shouldn't display any InfoWindow.
     *
     * @param mzController
     */
    public void setUpInfoWindow(MZController mzController) {
        zoneInfoWindow = new ZoneInfoWindow(this, R.layout.zone_bubble_layout, mzController);
        setInfoWindow(zoneInfoWindow);
        setOnClickListener((polygon, mapView, eventPos) -> {
            setInfoWindowLocation(eventPos);
            showInfoWindow();
            return true;
        });
    }


    /**
     * Used for temporarily changing the color of a DisplayZone while it is being
     * POST'ed to the server.
     */
    public void setTemporaryColor() {
        this.setFillColor(Color.argb(75, 0, 150, 255)); // Green option: 75 0 255 0
    }

    /**
     * Once the POST is confirmed, the color is back to normal.
     * (If an error occurs, the DisplayZone is removed.)
     */
    public void revertNormalColor() {
        this.setFillColor(Color.argb(75, 255, 0, 0));
    }

    protected abstract void setShape();


}
