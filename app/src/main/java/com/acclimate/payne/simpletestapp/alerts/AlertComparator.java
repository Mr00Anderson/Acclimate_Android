package com.acclimate.payne.simpletestapp.alerts;

import java.sql.Timestamp;
import java.util.Comparator;

public class AlertComparator implements Comparator<BasicAlert> {

    @Override
    public int compare(BasicAlert alert1, BasicAlert alert2) {
        Timestamp ts1 = Timestamp.valueOf(alert1.dateDeMiseAJour);
        Timestamp ts2 = Timestamp.valueOf(alert2.dateDeMiseAJour);
        return ts1.compareTo(ts2);
    }

}
