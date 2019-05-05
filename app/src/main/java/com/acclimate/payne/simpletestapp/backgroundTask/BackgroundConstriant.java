package com.acclimate.payne.simpletestapp.backgroundTask;


import androidx.work.Constraints;
import androidx.work.NetworkType;

public class BackgroundConstriant {

    public static Constraints basic(){
        return new Constraints.Builder()
                .setRequiresStorageNotLow(true)
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .setRequiresBatteryNotLow(true)
                .build();
    }

}
