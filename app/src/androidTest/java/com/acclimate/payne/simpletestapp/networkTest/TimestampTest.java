package com.acclimate.payne.simpletestapp.networkTest;

import android.util.Log;

import org.junit.Test;

import java.sql.Timestamp;
import java.util.Calendar;

import static com.acclimate.payne.simpletestapp.networkTest.ServerRequestTest.TIMESTAMP;

public class TimestampTest {


    @Test
    public void timestampCreation(){

        Calendar cal = Calendar.getInstance();

        for (int i = 0; i < 100; i++){
            Log.i(TIMESTAMP, (new Timestamp(cal.getTimeInMillis()).toString()).substring(0, 19));

        }


    }


}
