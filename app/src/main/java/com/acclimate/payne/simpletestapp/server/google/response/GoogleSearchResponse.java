package com.acclimate.payne.simpletestapp.server.google.response;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class GoogleSearchResponse {

    public ResultElements[] results;
    @JsonIgnore private String status;
    @JsonIgnore private String error_message;


    /**
     * Pour extraire l'information pertinente rapidement.
     * @return
     */
    public ResultElements getResult() {
        return results[0];
    }
}