package com.acclimate.payne.simpletestapp.alerts;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Geometry implements Serializable {

    private String      type;
    private double[]    coordinates;

    private double[][] polyCoordinates;

    @JsonIgnore public double getLat(){
        return coordinates[0];
    }

    @JsonIgnore public double getLng(){
        return coordinates[1];
    }

}
