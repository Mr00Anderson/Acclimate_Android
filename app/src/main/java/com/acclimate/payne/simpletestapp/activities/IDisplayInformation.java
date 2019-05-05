package com.acclimate.payne.simpletestapp.activities;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

/**
 * This interface define behavior that allows other classes to gain access to displaying
 * information on the screeen of the phone.
 */
public interface IDisplayInformation {

    /**
     * @return The context on which to display information.
     */
    Context getContext();


    /**
     * Used to fetched @String resources
     * @return
     */
    default String getString(int resId){
        return getContext().getResources().getString(resId);
    }

    /**
     * The information to be displayed for the user.
     * @param message the message to be displayed
     */
    void showOnView(String message, View view);

    /**
     * Display information related to a specific view. The method implementation should add
     * some view feedback to notify the user about the view element.
     * @param message
     * @param view
     */
    void showOnFocus(String message, View view);



    default void showOnView(int resId, View view){
        showOnView(getContext().getResources().getString(resId), view);
    }

    default void showOnFocus(int resId, View view){
        showOnFocus(getContext().getResources().getString(resId), view);
    }


    /**
     * super extra special focus ! shuch wow ! much UI !
     * @param resId
     * @param view
     */
    default void extraSpecialFocus(int resId, View view){ };

    default void errorMessage(String message){
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

}
