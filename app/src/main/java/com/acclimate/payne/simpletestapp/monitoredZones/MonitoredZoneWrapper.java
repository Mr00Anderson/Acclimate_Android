package com.acclimate.payne.simpletestapp.monitoredZones;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class MonitoredZoneWrapper {

    private List<MonitoredZone> zones = new ArrayList<>();

    @JsonIgnore
    public void addMonitoredZone(MonitoredZone zone){
        zones.add(zone);
    }

    @JsonIgnore
    public void setToAllMonitoredZones(MonitoredZone... zones){
        this.zones.clear();
        this.zones.addAll(new ArrayList<>(Arrays.asList(zones)));
    }

    @JsonIgnore
    public void removeDisplayZone(DisplayZone displayZone){
        zones.remove(displayZone.getMonitoredZone());
    }

    @Override
    public String toString() {

        try {
            return (new ObjectMapper().writeValueAsString(this));

        } catch (Exception e) {

            return "MonitoredZoneWrapper{" +
                    "zones=" + zones +
                    '}';

        }

    }

}
