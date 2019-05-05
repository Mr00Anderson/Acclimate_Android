package com.acclimate.payne.simpletestapp.server.google.response;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.HashMap;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class GoogleGeometry {

    @JsonIgnore private HashMap<String, Double> location;
    @JsonIgnore private String location_type;
    public Viewport viewport;
    @JsonIgnore private String bounds;

}


