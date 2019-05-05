package com.acclimate.payne.simpletestapp.monitoredZones;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.Polygon;


public class ZoneCircle extends DisplayZone {


    private double radius;
    private GeoPoint center;


    /**
     * please keep the constructor package private.
     * Use {@link MZController#createPostSaveDisplayZone(MonitoredZone)} to create a {@link DisplayZone}.
     *
     * @param radiusInMeters
     * @param center
     * @param zone
     */
    ZoneCircle(double radiusInMeters, GeoPoint center, MonitoredZone zone){
        super(zone);

        this.radius = radiusInMeters;
        this.center = center;

        setShape();
    }

    /**
     * Adding all point that makes a circle.
     */
    protected void setShape(){
        setPoints(Polygon.pointsAsCircle(center, radius));
    }
}