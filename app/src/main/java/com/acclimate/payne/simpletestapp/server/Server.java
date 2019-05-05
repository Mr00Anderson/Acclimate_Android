package com.acclimate.payne.simpletestapp.server;

import java.sql.Timestamp;
import java.util.Calendar;

public final class Server {

    public final static String BASE_SERVER_ADDRESS = "https://acclimate-api.herokuapp.com/api/";

    public final static String GEOMETRY = "geometry";
    public final static String KARMA = "karma";
    public final static String PLUS_ONE_USER_ALERT = "plusOneUsers";
    public final static String MINUS_ONE_USER_ALERT = "minusOneUsers";
    public final static String USER_LIST = "uids";
    public final static long DEFAULT_INTERVAL = 60 * 60; // une heure


    public static String getCurrentTimeFormatted(){
        long now = Calendar.getInstance().getTimeInMillis();
        return new Timestamp(now).toString().substring(0, 19);
    }

    public enum AuthorizationHeaders {
        REQUIRED, NONE, FORCED
    }

}
