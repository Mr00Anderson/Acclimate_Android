package com.acclimate.payne.simpletestapp.activities.main;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.acclimate.payne.simpletestapp.R;
import com.acclimate.payne.simpletestapp.alerts.Geometry;
import com.acclimate.payne.simpletestapp.appUtils.InternetCheck;
import com.acclimate.payne.simpletestapp.authentification.AuthUIActivity;
import com.acclimate.payne.simpletestapp.deviceStorage.preferences.SettingsPrefActivity;
import com.acclimate.payne.simpletestapp.monitoredZones.MonitoredZone;

import org.osmdroid.util.GeoPoint;

public class MainMenuItemListener implements Toolbar.OnMenuItemClickListener {

    private MainActivity main;

    public MainMenuItemListener(MainActivity main){
        this.main = main;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {

        switch (item.getItemId()) {

            // Bouton d'ajout de MonitoredZone
            case (R.id.add):

                // Uniquement si authentifié !
                if (AuthUIActivity.mAuth.getCurrentUser() != null) {

                    main.findViewById(R.id.add).setEnabled(false);

                    // Uniquement s'il y a internet !
                    new InternetCheck(hasInternet -> {
                        main.findViewById(R.id.add).setEnabled(true);

                        if(hasInternet) {
                            // Prompt for the type of MZ
                            AlertDialog.Builder builder = new AlertDialog.Builder(main);
                            builder.setTitle("Quel type de zone surveillée voulez-vous ajouter?");

                            // Set up the buttons
                            builder.setNeutralButton("Annuler", (dialog, which) -> dialog.cancel());
                            builder.setNegativeButton("Cercle", (dialog, which) -> {

                                // Extracting the center of the screen
                                GeoPoint center = (GeoPoint) main.mapView.getMapCenter(); // (Lat, Lng, Alt)
                                double[] coords = {center.getLatitude(), center.getLongitude()};

                                Geometry newGeometry = new Geometry();
                                newGeometry.setType("Point");
                                newGeometry.setCoordinates(coords);

                                MonitoredZone newMz = new MonitoredZone();
                                newMz.setGeometry(newGeometry);

                                main.promptRadius(newMz);
                            });
                            builder.setPositiveButton("Rectangle", (dialog, which) -> {

                                // Extraction de la BoundingBox
                                double[] coords = main.getMapDisplay().getBoundingBoxEdges(); // (double) topCoord, botCoord, rightCoord, leftCoord
                                double[] northWest = {coords[0], coords[3]}; // top, left
                                double[] southEast = {coords[1], coords[2]}; // bottom, right
                                double[][] polyCoords = {northWest, southEast};


                                Geometry newGeometry = new Geometry();
                                newGeometry.setType("Polygon");
                                newGeometry.setPolyCoordinates(polyCoords);

                                MonitoredZone newMz = new MonitoredZone();
                                newMz.setGeometry(newGeometry);

                                main.promptName(newMz);
                            });

                            // Dialog
                            AlertDialog dialog = builder.create();
                            dialog.show();

                        } else {

                            main.myAnimator.fastShakingAnimation(main.findViewById(R.id.add));
                            Snackbar.make(main.mapView, "Une connection internet est requise", Snackbar.LENGTH_SHORT)
                                    .setAction("Action", null).show();
                        }
                    });

                } else {
                    // Exemples de cette librairie @ https://github.com/faruktoptas/FancyShowCaseView/blob/master/app/src/main/java/me/toptas/fancyshowcasesample/MainActivity.java
                    main.focusOnUserIdentification();
                }
                break;

            // Bouton pour accéder aux Préférences
            case (R.id.settings):
                Intent intent = new Intent(main.getApplicationContext(), SettingsPrefActivity.class);
                main.startActivity(intent);
                break;

            // Bouton pour accéder à l'Authentification
            case (R.id.profileBtn):
                Intent intent2 = new Intent(main.getApplicationContext(), AuthUIActivity.class);
                main.startActivity(intent2);
                break;

            default:
                break;
        }

        return false;
    }

}
