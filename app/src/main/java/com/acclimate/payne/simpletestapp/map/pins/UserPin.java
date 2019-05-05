package com.acclimate.payne.simpletestapp.map.pins;

import android.content.Context;
import android.support.annotation.NonNull;

import com.acclimate.payne.simpletestapp.R;
import com.acclimate.payne.simpletestapp.alerts.UserAlert;

import org.osmdroid.views.MapView;

public class UserPin extends Pin {

    UserPin(@NonNull MapView mapView, @NonNull UserAlert alerte) {
        super(mapView, alerte);
    }


    @Override
    public void setPinIcon(Context ctx) {

        if (alerte.getEnumType() == null){
            alerte.initType();
        }

        switch (alerte.getEnumType()){
            case USER_TERRAIN:
                setIcon(ctx.getResources().getDrawable(R.drawable.pin_user_earth));
                break;
            case USER_FEU:
                setIcon(ctx.getResources().getDrawable(R.drawable.pin_user_fire));
                break;
            case USER_EAU:
                setIcon(ctx.getResources().getDrawable(R.drawable.pin_user_water));
                break;
            case USER_METEO: default:
                setIcon(ctx.getResources().getDrawable(R.drawable.pin_user_wind));
                break;

        }

    }
}
