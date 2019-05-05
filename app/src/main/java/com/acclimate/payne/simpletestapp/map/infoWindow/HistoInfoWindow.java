/*
package com.example.payne.simpletestapp.map.infoWindow;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.payne.simpletestapp.R;
import com.example.payne.simpletestapp.activities.IDisplayInformation;
import com.example.payne.simpletestapp.activities.main.MainActivity;
import com.example.payne.simpletestapp.activities.moreInfosAlerts.MoreInfoAlertsActivity;
import com.example.payne.simpletestapp.alerts.BasicAlert;
import com.example.payne.simpletestapp.appUtils.AppTag;

import org.osmdroid.views.MapView;

import static com.example.payne.simpletestapp.appUtils.AppTag.PIN_FLOW;

public class HistoInfoWindow extends PinInfoWindow {

    private IDisplayInformation displayer;

    public HistoInfoWindow(@NonNull BasicAlert basicAlert, int layoutResId, @NonNull MainActivity main){
        super(basicAlert, layoutResId, main.getMapDisplay().getMap());
        this.displayer = main.getMainSnackbarDisplayer();

    }

    @Override
    public void onOpen(Object arg0) {
        super.onOpen(arg0);

        TextView dateView = mView.findViewById(R.id.bubble_date);
        dateView.setText(alerte.getDateDeMiseAJour());

    }


    @Override
    protected void onMoreInfoClick(View view) {
        Intent intent = new Intent(displayer.getContext(), MoreInfoAlertsActivity.class);
        intent.putExtra(AppTag.ALERT_TRANSFER_MORE_INFO, this.alerte);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Context ctx = displayer.getContext();
        ctx.startActivity(intent);

    }

    @Override
    protected void bringInViews() {
        Log.i(PIN_FLOW, "bring in info windo view");
        layout =            mView.findViewById(R.id.bubble_layout);
        btnMoreInfos =      mView.findViewById(R.id.bubble_more_info);
        title =             mView.findViewById(R.id.bubble_title);
        subCat =            mView.findViewById(R.id.bubble_subcat);
        description =       mView.findViewById(R.id.bubble_description);
    }
}
*/
