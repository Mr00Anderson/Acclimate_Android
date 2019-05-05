package com.acclimate.payne.simpletestapp.server.google.response;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ResultElements {


    @JsonIgnore private Components[] address_components;
    @JsonIgnore private String formatted_address;
    @JsonIgnore private String place_id;
    public GoogleGeometry geometry;
    @JsonIgnore private String[] types;
    @JsonIgnore private String[] postcode_localities;
    @JsonIgnore private String plus_code;
    @JsonIgnore private String partial_match;

}
