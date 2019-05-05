package com.acclimate.payne.simpletestapp.activities.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Vibrator;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityOptionsCompat;
import android.view.Gravity;
import android.view.View;

import com.acclimate.payne.simpletestapp.R;
import com.acclimate.payne.simpletestapp.activities.alertForm.NewAlertFormIntent;
import com.acclimate.payne.simpletestapp.activities.moreInfosAlerts.MoreInfoAlertsActivity;
import com.acclimate.payne.simpletestapp.alerts.BasicAlert;
import com.acclimate.payne.simpletestapp.animator.MyAnimator;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.infowindow.InfoWindow;

import me.toptas.fancyshowcase.FancyShowCaseView;

public class MainActivityHelper {

    // MainKarmaHelper karmaHelper = new MainKarmaHelper(App.getInstance().getCurrentUser());


    /**
     * Focus the view on the registration icon in the app bar and display the message
     * on the center of the screen
     *
     * @param main the MainActivity
     * @param message the message tobe displayed
     */
    public void focusOnUserAuth(MainActivity main, String message){

        new FancyShowCaseView.Builder(main)
                .focusOn(main.findViewById(R.id.profileBtn))
                .focusCircleRadiusFactor(0.8)
                .title(message)
                .titleStyle(R.style.MyTitleStyle, Gravity.CENTER)
                .build()
                .show();
        Vibrator v = (Vibrator) main.getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(130);
        main.myAnimator.fastShakingAnimation(main.findViewById(R.id.profileBtn));

    }


    public void switchToUserAlertForm(MainActivity main, GeoPoint p){
        InfoWindow.closeAllInfoWindowsOn(main.mapView);
        NewAlertFormIntent alertFormIntent = new NewAlertFormIntent(main.getApplicationContext());
        alertFormIntent.setData(p);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptionsCompat options = ActivityOptionsCompat.
                    makeSceneTransitionAnimation(main, main.mapView, "map_trans");
            main.startActivity(alertFormIntent, options.toBundle());

        } else {
            main.startActivity(alertFormIntent);
        }
    }


    static void switchToUserMoreInfoActivity(MainActivity main, BasicAlert alert) {
        Intent intent = new Intent(main, MoreInfoAlertsActivity.class);
        main.startActivity(intent);
    }




    public void notifyUserOnView(Activity activity, String message, View view){
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show();
        MyAnimator anim = new MyAnimator(activity);
        anim.fastShakingAnimation(view);

    }



}