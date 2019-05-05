package com.acclimate.payne.simpletestapp.server.google.response;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Components {

    @JsonIgnore private String long_name;
    @JsonIgnore private String short_name;
    @JsonIgnore private String[] types;

}
