package com.acclimate.payne.simpletestapp.alerts;

import android.app.Activity;
import android.content.Context;

public interface IAcclimateAlertFetch {

    void beginFetchAlert();

    /**
     * Method to override for specific actions once alerts are ready to be used..
     *
     * @param wrapper An instance of an alert wrapper containing al alerts fetched.
     */
    @SuppressWarnings("all")
    void onFetchedAlert(AlertWrapper wrapper, boolean success);

    /**
     * Required to gain access to the File local Storage. Usually will just be implemented with
     * {@code return {@link Activity#getApplicationContext()}} to get te current from the current activity
     *
     * @return getApplicationContext() if the interface is implemented by an Activity (it should)
     */
    Context getContext();

}
