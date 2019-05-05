package com.acclimate.payne.simpletestapp.activities.moreInfosAlerts;

import android.app.AlertDialog;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.acclimate.payne.simpletestapp.R;
import com.acclimate.payne.simpletestapp.alerts.UserAlert;

public class MoreInfoMenuToolBarListener implements Toolbar.OnMenuItemClickListener {

    private MoreInfoAlertsActivity ctx;
    private UserAlert alert;

    MoreInfoMenuToolBarListener(@NonNull MoreInfoAlertsActivity ctx, @NonNull UserAlert alert) {
        this.ctx = ctx;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.moreinfo_menu_trash :

                // todo : replace with string ressource
                new AlertDialog.Builder(ctx)
                        .setMessage(R.string.delete_alert)
                        .setPositiveButton(R.string.confirm_delete_alert, ctx::deleteAlert)
                        .setNegativeButton(R.string.cancel_delete_alert, (dialog, which) -> dialog.dismiss())
                        .create().show();

                break;

            case R.id.moreinfo_menu_edit:
                Snackbar.make(ctx.findViewById(R.id.moreinfo_main),
                        "Impossible de modifier l'alerte", Snackbar.LENGTH_SHORT).show();
                break;
        }

        return true;
    }

}
