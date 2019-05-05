package com.acclimate.payne.simpletestapp.server.google.response;

import org.osmdroid.util.BoundingBox;

import java.util.HashMap;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Viewport {

    public HashMap<String, Double> northeast;
    public HashMap<String, Double> southwest;

    public HashMap<String, Double> getNortheast() {
        return northeast;
    }

    public HashMap<String, Double> getSouthwest() {
        return southwest;
    }

    public BoundingBox getBoundingBox() {

        double[] northEast = {this.northeast.get("lat"), this.northeast.get("lng")};
        double[] southWest = {this.southwest.get("lat"), this.southwest.get("lng")};

        double north = northEast[0];
        double south = southWest[0];
        double east = northEast[1];
        double west = southWest[1];

        return new BoundingBox(north, east, south, west);
    }

}
