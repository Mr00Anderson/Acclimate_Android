package com.acclimate.payne.simpletestapp.server.requests;

/**
 * Represents one of the main entry points the client can query the REST API.
 */
public enum RequestCategory {

    /**
     * Category for alert requests
     */
    ALERT               ("alerts"),

    /**
     * Category fir user requests
     */
    USER                ("users"),

    /**
     * category for Monitored monitoredZone requests
     */
    MONITORED_ZONE      ("monitoredzones");

    String category;

    RequestCategory(String category) { this.category = category; }

    /**
     *  The associated value used in the actual url when sending the request.
     * @return the url parameter as a String
     */
    public String getCategory() {
        return category;
    }
}
