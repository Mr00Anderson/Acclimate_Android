package com.acclimate.payne.simpletestapp.monitoredZones;

import android.app.AlertDialog;
import android.support.design.widget.Snackbar;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.acclimate.payne.simpletestapp.R;
import com.acclimate.payne.simpletestapp.animator.MyAnimator;
import com.acclimate.payne.simpletestapp.appUtils.InternetCheck;

import org.osmdroid.views.overlay.infowindow.InfoWindow;


public class ZoneInfoWindow extends InfoWindow {

    MZController mzController;
    DisplayZone displayZone;
    Button deleteBtn;
    TextView deleteTxt;
    LinearLayout layout;
    TextView title;
    MyAnimator myAnimator;


    ZoneInfoWindow(DisplayZone displayZone, int layoutResId, MZController mzController) {
        super(layoutResId, mzController.getMapView());

        this.mzController = mzController;
        this.displayZone = displayZone;
        this.myAnimator = new MyAnimator(mzController.getActivity());
    }


    private void promptDeleteConfirm() {

        new InternetCheck(hasInternet -> {
            if (hasInternet) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(layout.getContext());
                alertDialogBuilder.setMessage("Cette action va supprimer votre zone");

                alertDialogBuilder.setNegativeButton("Annuler", (dialog, which) -> dialog.cancel()); // rien faire
                alertDialogBuilder.setPositiveButton("OK", (arg1, arg2) -> {
                    disableDeleteBtns();
                    mzController.deleteOneZone(displayZone);
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            } else {
                myAnimator.fastShakingAnimation(deleteBtn);
                myAnimator.fastShakingAnimation(deleteTxt);
                Snackbar.make(mMapView,
                        "Impossible sans internet", Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    public void disableDeleteBtns() {
        deleteBtn.setEnabled(false);
        deleteTxt.setEnabled(false);
    }

    public void enableDeleteBtns() {
        deleteBtn.setEnabled(true);
        deleteTxt.setEnabled(true);
    }


    @Override
    public void onOpen(Object arg0) {
        closeAllInfoWindowsOn(mMapView);

        layout = mView.findViewById(R.id.zone_bubble_layout);
        deleteBtn = mView.findViewById(R.id.zone_bubble_delete);
        deleteTxt = mView.findViewById(R.id.editText);
        title = mView.findViewById(R.id.zone_bubble_title);

        deleteBtn.setOnClickListener(view -> promptDeleteConfirm());
        deleteTxt.setOnClickListener(view -> promptDeleteConfirm());

        layout.setOnClickListener(view -> closeAllInfoWindowsOn(mMapView));

        title.setText("Nom: " + displayZone.getMonitoredZone().getName());
    }

    @Override public void onClose(){ }

}
