package com.acclimate.payne.simpletestapp.wip;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.acclimate.payne.simpletestapp.R;
import com.acclimate.payne.simpletestapp.alerts.UserAlert;
import com.acclimate.payne.simpletestapp.alerts.alertViewModel.AlertViewModel;
import com.acclimate.payne.simpletestapp.alerts.alertViewModel.AlertViewModelSingletonFactory;
import com.acclimate.payne.simpletestapp.appUtils.App;
import com.acclimate.payne.simpletestapp.user.User;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class UserAlertCardFragment extends Fragment {

    // TODO: Customize parameters
    private int mColumnCount = 1;


    private OnListFragmentInteractionListener mListener;

    private AlertViewModel alertViewModel;
    private List<UserAlert> usersAlerts;
    private User appUser;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public UserAlertCardFragment() {

        alertViewModel = ViewModelProviders
                .of(this, AlertViewModelSingletonFactory.getInstance())
                .get(AlertViewModel.class);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        appUser = App.getInstance().getCurrentUser();
        usersAlerts = new ArrayList<>();
        List<UserAlert> allUserAlerts = alertViewModel.getUserAlerts().getValue();

        if (appUser != null && allUserAlerts != null) {
            for (UserAlert currentAlert : allUserAlerts) {
                if (currentAlert.getId().equals(appUser.getuId())) {
                    usersAlerts.add(currentAlert);
                }
            }
        }

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.useralertcard_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {

            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.setAdapter(new MyUserAlertCardRecyclerViewAdapter(usersAlerts, mListener));

        }
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(UserAlert item);
    }
}
