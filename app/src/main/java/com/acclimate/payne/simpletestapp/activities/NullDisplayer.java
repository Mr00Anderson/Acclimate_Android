package com.acclimate.payne.simpletestapp.activities;

import android.app.Activity;
import android.content.Context;
import android.view.View;

/**
 * Displayer implementation that displays nothing
 */
public class NullDisplayer implements IDisplayInformation {

    private Activity activity;

    public NullDisplayer(Activity activity) {
        this.activity = activity;
    }

    @Override
    public Context getContext() {
        return activity.getApplicationContext();
    }

    @Override
    public void showOnView(String message, View view) {}

    @Override
    public void showOnFocus(String message, View view) {}

}
