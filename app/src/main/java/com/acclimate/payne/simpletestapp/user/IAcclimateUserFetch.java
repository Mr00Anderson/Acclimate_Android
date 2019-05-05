package com.acclimate.payne.simpletestapp.user;

import android.app.Activity;
import android.content.Context;

public interface IAcclimateUserFetch {

    void beginFetchUser();

    /**
     * Method to override for specific actions once user is ready to be used..
     *
     * @param user An instance of an User that has been fetched.
     */
    @SuppressWarnings("all")
    void onFetchedAlert(User user);

    /**
     * Required to gain access to the File local Storage. Usually will just be implemented with
     * {@code return {@link Activity#getApplicationContext()}} to get te current from the current activity
     *
     * @return getApplicationContext() if the interface is implemented by an Activity (it should)
     */
    Context getContext();

}
