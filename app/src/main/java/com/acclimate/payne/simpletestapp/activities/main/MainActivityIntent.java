package com.acclimate.payne.simpletestapp.activities.main;

import android.content.Context;
import android.content.Intent;


public class MainActivityIntent extends Intent {

    private static final String ARG_FROM_HOME = "arg_from_home";
    private static final String ARG_IS_COMPLETE = "arg_is_complete";

    public MainActivityIntent(Context ctx) {
        super(ctx, MainActivity.class);
        // setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
    }

    //This will be needed for receiving data
    public MainActivityIntent(Intent intent) {
        super(intent);

    }

    /**
     * Sets this value to true if the alerts array are passed as arguments. <p></p>
     * If set to true, the MainActivity, on launch, will try to fetch the arrays in the
     * extra infos put in the Intent.
     * @param fromHome the boolean that tells MainActivity to check or not. true means go check.
     */
    public void fromHome(boolean fromHome){
        putExtra(ARG_FROM_HOME, fromHome);
    }

    public boolean isFromHome(){
        return getBooleanExtra(ARG_FROM_HOME, false);
    }

    public void setIsCompleted(boolean fromHome){
        putExtra(ARG_IS_COMPLETE, fromHome);
    }

    public boolean isTaskCompleted(){
        return getBooleanExtra(ARG_IS_COMPLETE, false);
    }





}
