package com.acclimate.payne.simpletestapp.activities.alertForm;

import android.content.Context;
import android.content.Intent;

import org.osmdroid.util.GeoPoint;

import java.io.Serializable;

public class NewAlertFormIntent extends Intent {

    private static final String ARG_LAT = "arg_lat";
    private static final String ARG_LNG = "arg_lng";
    private static final String ARG_POINT = "arg_point";



    public NewAlertFormIntent(Context ctx) {
        super(ctx, NewAlertFormActivity.class);
    }

    //This will be needed for receiving data
    public NewAlertFormIntent(Intent intent) {
        super(intent);
    }

    public void setData(double lat, double lng) {
        putExtra(ARG_LAT, lat);
        putExtra(ARG_LNG, lng);
    }



    public void setData(GeoPoint p){
        putExtra(ARG_POINT, (Serializable) p);
    }

    public double getLat() {
        return getDoubleExtra(ARG_LAT, 0.0);
    }

    public double getLng() {
        return getDoubleExtra(ARG_LNG, 0.0);
    }

    public GeoPoint getPoint(){
        return (GeoPoint) getSerializableExtra(ARG_POINT);
    }

}
