package com.acclimate.payne.simpletestapp.backgroundTask;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class BackgroundHelper {

    private Context ctx;

    public BackgroundHelper(Context ctx){
        this.ctx = ctx;
    }

    public long getInterval(){

        SharedPreferences sP = PreferenceManager.getDefaultSharedPreferences(ctx);

        // sP.getString()
        return 1L;

    }

}
