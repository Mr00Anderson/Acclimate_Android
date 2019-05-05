package com.acclimate.payne.simpletestapp.map.pins;

import android.content.Context;
import android.support.annotation.NonNull;

import com.acclimate.payne.simpletestapp.alerts.BasicAlert;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import lombok.Getter;


public abstract class Pin extends Marker {

    @Getter
    protected BasicAlert alerte;
    protected MapView mapView;

    public Pin(@NonNull MapView mapView, @NonNull BasicAlert alerte) {

        super(mapView);

        this.alerte = alerte;
        this.mapView = mapView;

        setPinIcon(mapView.getContext());

        GeoPoint pos = new GeoPoint(alerte.getLat(), alerte.getLng());
        setPosition(pos);
        // setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        // setTitle(alerte.getNom());

    }

    public void draw(){
        mapView.getOverlayManager().add(this);
    }

    public void remove(){
        mapView.getOverlayManager().remove(this);
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final Pin other = (Pin) obj;

        return alerte.equals(other.alerte);
    }

    @Override
    public int hashCode() {
        return alerte.hashCode();
    }

    public abstract void setPinIcon(Context ctx);
}
