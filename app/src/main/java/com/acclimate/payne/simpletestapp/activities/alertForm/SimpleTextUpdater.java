package com.acclimate.payne.simpletestapp.activities.alertForm;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.TextView;

public class SimpleTextUpdater implements TextWatcher {

    private NewAlertFormActivity activity;
    private int updateView;

    public SimpleTextUpdater(NewAlertFormActivity activity, int updateView){
        this.activity = activity;
        this.updateView = updateView;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {


    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        TextView myOutputBox = activity.findViewById(updateView);
        myOutputBox.setText(s);

    }

    @Override
    public void afterTextChanged(Editable s) {

    }

}
