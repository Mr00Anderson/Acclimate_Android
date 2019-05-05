package com.acclimate.payne.simpletestapp.activities.moreInfosAlerts;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.acclimate.payne.simpletestapp.R;

public class NullUserFragment extends Fragment {
    public NullUserFragment(){}
    public static NullUserFragment newInstance() {
        return new NullUserFragment();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.empty_layout, container, false);
    }
}
