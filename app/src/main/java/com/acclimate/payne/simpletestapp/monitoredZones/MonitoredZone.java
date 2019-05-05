package com.acclimate.payne.simpletestapp.monitoredZones;

import com.acclimate.payne.simpletestapp.alerts.Geometry;
import com.acclimate.payne.simpletestapp.user.User;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

import lombok.Getter;
import lombok.Setter;

/**
 * Server response for a monitored monitoredZone item
 */
@Getter @Setter
public class MonitoredZone {

    private Integer     zoneId;
    private Geometry    geometry;
    private int         radius;
    private String      name;
    private String      userId;
    private User        user;
    private String      dateCreation;
    private String      pointType;
    private String      polygonType;


    public void setUser(User user){
        this.user = user;
        this.userId = user.getuId();
    }


    @Override
    public String toString() {

        try {
            return (new ObjectMapper()).writeValueAsString(this);
        } catch (IOException ioe){
            return "MonitoredZone{" +
                    "zoneId=" + zoneId +
                    ", geometry=" + geometry.toString() +
                    ", radius=" + radius +
                    ", user=" + user +
                    ", name='" + name + '\'' +
                    '}';
        }
    }

}
