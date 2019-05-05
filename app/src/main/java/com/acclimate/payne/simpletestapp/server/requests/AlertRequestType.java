package com.acclimate.payne.simpletestapp.server.requests;

public enum AlertRequestType {

    /**
     *
     */
    USER                ("user"),
    LIVE                ("live"),
    HISTORICAL          ("historical");

    String type;

    AlertRequestType(String type){ this.type = type; }
    public String getType(){ return this.type; }


}
