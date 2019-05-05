package com.acclimate.payne.simpletestapp.activities.moreInfosAlerts;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.acclimate.payne.simpletestapp.R;
import com.acclimate.payne.simpletestapp.alerts.UserAlert;

public class AlertScoreFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM_ALERT_SCORE = "alert_param_score";
    private static final String ARG_PARAM_UIVALUES_SCORE = "uivalues_param_score";

    private UserAlert userAlert;
    private UIValues uiValues;

    private MoreInfoFragmentListener mListener;

    public AlertScoreFragment() { }
    static AlertScoreFragment newInstance(UserAlert alerte, UIValues uiValues) {
        AlertScoreFragment fragment = new AlertScoreFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM_ALERT_SCORE, alerte);
        args.putSerializable(ARG_PARAM_UIVALUES_SCORE, uiValues);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userAlert = (UserAlert) getArguments().getSerializable(ARG_PARAM_ALERT_SCORE);
            uiValues = (UIValues) getArguments().getSerializable(ARG_PARAM_UIVALUES_SCORE);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.moreinfo_fragment_alertscore, container, false);
        TextView scoreView = v.findViewById(R.id.moreinfo_score);
        TextView scoreTitle = v.findViewById(R.id.moreinfo_static_score);
        scoreTitle.setTextColor(getResources().getColor(uiValues.getPrimaryColor()));
        String score = "" + userAlert.getScore();
        scoreView.setText(score);
        setupUIFromValues(v, uiValues);
        return v;
    }


    public void onButtonPressed(UserAlert alerte) {
        if (mListener != null) {
            mListener.onFragmentInteraction(alerte);
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MoreInfoFragmentListener) {
            mListener = (MoreInfoFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }



    private void setupUIFromValues(View view, UIValues values){

        int primary = getResources().getColor(values.getPrimaryColor());


    }

}
