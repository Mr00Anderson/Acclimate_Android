package com.acclimate.payne.simpletestapp.monitoredZones;

import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;

import lombok.Getter;

public class ZoneBox extends DisplayZone {

    @Getter
    private GeoPoint nW, sE, nE, sW; // "nE" and "sW" are only there to draw

    /**
     * plese keep the constructor package private.
     * Use {@link MZController#createPostSaveDisplayZone(MonitoredZone)} to create a {@link DisplayZone}.
     *
     * @param nW
     * @param sE
     * @param zone
     */
    ZoneBox(GeoPoint nW, GeoPoint sE, GeoPoint nE, GeoPoint sW, MonitoredZone zone){
        super(zone);

        this.nW = nW; this.sE = sE;
        this.nE = nE; this.sW = sW;

        setShape();
    }

    /**
     * Setup a rectangle
     */
    protected void setShape() {
        ArrayList<GeoPoint> points = new ArrayList<>(5);
        points.add(nE); points.add(nW); points.add(sW); points.add(sE);
        points.add(points.get(0)); // from last to first (close drawing loop)
        setPoints(points);
    }


}
