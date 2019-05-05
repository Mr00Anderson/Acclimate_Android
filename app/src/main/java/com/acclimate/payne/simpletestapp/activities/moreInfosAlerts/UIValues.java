package com.acclimate.payne.simpletestapp.activities.moreInfosAlerts;

import com.acclimate.payne.simpletestapp.R;
import com.acclimate.payne.simpletestapp.alerts.BasicAlert;

import java.io.Serializable;

class UIValues implements Serializable {

    private int backdropImage;
    public final int getBackdropImage() { return backdropImage; }

    private int primaryColor;
    public int getPrimaryColor() { return primaryColor; }


    UIValues(int backdropImage, int textColor){
        this.backdropImage = backdropImage;
        this.primaryColor = textColor;
    }

    UIValues(BasicAlert alert){
        setupCustomUIValues(alert);
    }

    UIValues setupCustomUIValues(BasicAlert alert){

        if (alert == null) {
            return defaultUIValues();
        }

        if (alert.getEnumType() == null) {
            alert.initType();
        }

        switch (alert.getEnumType()) {
            case USER_EAU: case EAU:
                backdropImage = R.drawable.water_tag;
                primaryColor = R.color.water;
                break;

            case FEU: case USER_FEU:
                backdropImage = R.drawable.fire_tag;
                primaryColor = R.color.fire;
                break;

            case METEO: case USER_METEO:
                backdropImage = R.drawable.meteo_tag;
                primaryColor = R.color.weather;
                break;

            case TERRAIN: case USER_TERRAIN: default:
                backdropImage = R.drawable.earth_tag;
                primaryColor = R.color.earth;
                break;
        }

        return new UIValues(backdropImage, primaryColor);

    }


    public UIValues defaultUIValues() {
        // water as default
        backdropImage = R.drawable.water_tag;
        primaryColor = R.color.water;
        return new UIValues(R.drawable.water_tag, R.color.water);
    }




}
