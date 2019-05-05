package com.acclimate.payne.simpletestapp.activities.main;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.acclimate.payne.simpletestapp.activities.IDisplayInformation;
import com.acclimate.payne.simpletestapp.animator.MyAnimator;

public class MainSnackbarDisplayer implements IDisplayInformation {

    private MainActivity main;

    public MainSnackbarDisplayer(MainActivity main) {
        this.main = main;
    }

    @Override
    public Context getContext() {
        return main.getApplicationContext();
    }

    @Override
    public void showOnView(String message, View view) {
        Snackbar.make(view, message, Snackbar.LENGTH_LONG);
    }

    @Override
    public void showOnFocus(String message, View view) {
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show();
        new MyAnimator(main).fastShakingAnimation(view);
    }

    /**
     * Focus on Auth icon
     *
     * @param StringResId
     * @param view
     */
    @Override
    public void extraSpecialFocus(int StringResId, @Nullable View view) {
        new MainActivityHelper().focusOnUserAuth(main,
                getContext().getString(StringResId));
    }

}
