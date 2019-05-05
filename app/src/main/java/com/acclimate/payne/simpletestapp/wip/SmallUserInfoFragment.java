package com.acclimate.payne.simpletestapp.wip;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.acclimate.payne.simpletestapp.R;
import com.acclimate.payne.simpletestapp.activities.moreInfosAlerts.ExtraUserAlertFragment;
import com.acclimate.payne.simpletestapp.activities.moreInfosAlerts.MoreInfoFragmentListener;
import com.acclimate.payne.simpletestapp.alerts.BasicAlert;
import com.acclimate.payne.simpletestapp.user.User;

public class SmallUserInfoFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM_USER = "small_user_param";

    // TODO: Rename and change types of parameters
    private User user;

    private MoreInfoFragmentListener mListener;

    public SmallUserInfoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param user Parameter 1.
     * @return A new instance of fragment ExtraUserAlertFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ExtraUserAlertFragment newInstance(User user) {
        ExtraUserAlertFragment fragment = new ExtraUserAlertFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM_USER, user);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            user = (User) getArguments().getSerializable(ARG_PARAM_USER);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.user_short_info, container, false);

        TextView username = container.findViewById(R.id.usershortinfo_uname);
        username.setText(user.getUserName());

        ImageView userIcon = container.findViewById(R.id.usershortinfo_icon);
        userIcon.setImageDrawable(user.getIcon(getActivity().getResources()));

        TextView karmaView = container.findViewById(R.id.usershortinfo_karma);
        String userKarma = "" + user.getKarma();
        karmaView.setText(userKarma);

        return v;

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(BasicAlert alerte) {
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


}
