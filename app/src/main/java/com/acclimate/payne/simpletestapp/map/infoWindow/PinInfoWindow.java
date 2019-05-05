package com.acclimate.payne.simpletestapp.map.infoWindow;

import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.acclimate.payne.simpletestapp.R;
import com.acclimate.payne.simpletestapp.alerts.AlertTypes;
import com.acclimate.payne.simpletestapp.alerts.BasicAlert;

import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.infowindow.InfoWindow;

import lombok.NonNull;
import lombok.Setter;

public abstract class PinInfoWindow extends InfoWindow {

    protected BasicAlert alerte;

    protected ConstraintLayout layout;

    Button   btnMoreInfos;
    TextView subCat;

    protected TextView title;
    protected TextView description;

    @Setter
    protected View.OnClickListener btnMoreInfoListener = this::onMoreInfoClick;

    PinInfoWindow(@NonNull BasicAlert alerte, int layoutResId, @NonNull MapView mapView) {
        super(layoutResId, mapView);
        this.alerte = alerte;
//        Log.i(PIN_FLOW, "end super constructor");

    }


        @Override
    public void onOpen(Object arg0) {

        bringInViews();

//        Log.i(PIN_FLOW, alerte.toString());

        if ( btnMoreInfos == null
                 || title == null
                 || subCat == null
                 || description == null
                 || layout == null){
            throw new RuntimeException(
                    String.format("Must at least set btnMoreInfos (%s), title (%s), subCat (%s), description (%s) " +
                            "and layout (%s)layouts in PinInfoWindow#bringInViews",
                            btnMoreInfos, title, subCat, description, layout));
        }

        if (alerte.getEnumType() == null) {
            alerte.initType();
        }

        AlertTypes alt = alerte.getEnumType();

        switch (alt) {
            case EAU: case USER_EAU:
//                Log.i(PIN_FLOW, "switch eau");
                title.setBackground(mView.getResources().getDrawable(
                        alt == AlertTypes.USER_EAU ? R.drawable.user_bubble_title_water : R.drawable.bubble_title_water));
                break;
            case FEU: case USER_FEU:
//                Log.i(PIN_FLOW, "switch feu");
                title.setBackground(mView.getResources().getDrawable(
                        alt == AlertTypes.USER_FEU ? R.drawable.user_bubble_title_fire : R.drawable.bubble_title_fire));
                break;
            case TERRAIN: case USER_TERRAIN:
//                Log.i(PIN_FLOW, "switch terrain");
                title.setBackground(mView.getResources().getDrawable(
                        alt == AlertTypes.USER_TERRAIN ? R.drawable.user_bubble_title_earth : R.drawable.bubble_title_earth));
                break;
            case METEO: case USER_METEO:
//                Log.i(PIN_FLOW, "switch meteo");
                title.setBackground(mView.getResources().getDrawable(
                        alt == AlertTypes.USER_METEO ? R.drawable.user_bubble_title_weather : R.drawable.bubble_title_weather));
                break;


        }

        if (alerte != null) {
//            Log.i(PIN_FLOW, "set text");
            title.setText(alerte.getNom().equals("") ? "Aucun titre" : alerte.getNom());
            subCat.setText(alerte.getSousCategorie().equals("") ? "Aucune sous-catégorie" : alerte.getSousCategorie());
            description.setText(alerte.getDescription().equals("") ? "Aucune description" : alerte.getDescription());
        }

        btnMoreInfos.setOnClickListener(btnMoreInfoListener);
        layout.setOnClickListener(view -> closeAllInfoWindowsOn(mMapView));
//        Log.i(PIN_FLOW, "end super on open");

    }

    @Override public void onClose(){ }

    /**
     *
     * @param view
     */
    protected abstract void onMoreInfoClick(View view);

    /**
     * should at least declare something for layout, btnMoreInfos, title, subCat and description at minimum
     */
    protected abstract void bringInViews();

}
