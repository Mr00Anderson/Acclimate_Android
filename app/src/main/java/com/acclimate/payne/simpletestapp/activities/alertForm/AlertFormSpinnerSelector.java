package com.acclimate.payne.simpletestapp.activities.alertForm;

import android.content.res.Resources;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.acclimate.payne.simpletestapp.R;

public class AlertFormSpinnerSelector implements AdapterView.OnItemSelectedListener {

    private NewAlertFormActivity activity;

    AlertFormSpinnerSelector(NewAlertFormActivity activity){
        this.activity = activity;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

        // ugly switch case ... but no choice :(
        switch(parent.getId()){

            case R.id.severity_spinner :
                activity.setSeverity((String) parent.getItemAtPosition(pos));
                break;

            case R.id.form_spinner_alert_type :
                String selected = (String) parent.getItemAtPosition(pos);
                activity.setTypeSelected(selected);
                activity.getMapMarker().setIcon(activity.getTypes().get(selected));
                activity.getMini_map().invalidate();

                // set alert subType based on main alert type
                Spinner subtype_spinner = activity.findViewById(R.id.form_spinner_subtype);
                subtype_spinner.setAdapter(findSubTypeArrayAdater(selected));
                break;

            case R.id.form_spinner_subtype:
                TextView subCatView = activity.findViewById(R.id.user_bubble_subcat);
                String subCat = (String) parent.getItemAtPosition(pos);
                subCatView.setText(subCat);
                break;

        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    private ArrayAdapter<String> findSubTypeArrayAdater(String selected){

        ArrayAdapter<String> adapter;
        Resources res = activity.getResources();
        TextView alertTitle = activity.findViewById(R.id.user_bubble_title);


        // switch case within switch case ... you gotta be kidding me ...
        switch (selected) {

            case "Eau":
                adapter = new ArrayAdapter<>(
                        activity,
                        android.R.layout.simple_spinner_dropdown_item,
                        activity.listSubtypeAlertEau);
                alertTitle.setBackground(res.getDrawable(R.drawable.user_bubble_title_water));

                break;

            case "Seisme":
                adapter = new ArrayAdapter<>(
                        activity,
                        android.R.layout.simple_spinner_dropdown_item,
                        activity.listSubtypeAlertSeiseme);
                alertTitle.setBackground(res.getDrawable(R.drawable.user_bubble_title_earth));
                break;

            case "Météo":
                adapter = new ArrayAdapter<>(
                        activity,
                        android.R.layout.simple_spinner_dropdown_item,
                        activity.listSubtypeAlertMeteo);
                alertTitle.setBackground(res.getDrawable(R.drawable.user_bubble_title_weather));
                break;

            case "Feu": default :
                adapter = new ArrayAdapter<>(
                        activity,
                        android.R.layout.simple_spinner_dropdown_item,
                        activity.listSubtypeAlertFeu);
                alertTitle.setBackground(res.getDrawable(R.drawable.user_bubble_title_fire));

                break;
        }

        return adapter;
    }


}
