package com.acclimate.payne.simpletestapp.activities.moreInfosAlerts;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.acclimate.payne.simpletestapp.R;
import com.acclimate.payne.simpletestapp.alerts.UserAlert;
import com.acclimate.payne.simpletestapp.appUtils.Async;
import com.acclimate.payne.simpletestapp.appUtils.Callback;
import com.acclimate.payne.simpletestapp.server.Server;
import com.acclimate.payne.simpletestapp.server.requests.HttpRequest;
import com.acclimate.payne.simpletestapp.server.requests.PostRequest;
import com.acclimate.payne.simpletestapp.server.requests.RequestHandler;
import com.acclimate.payne.simpletestapp.user.User;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the interface
 * to handle interaction events.
 * Use the {@link ExtraUserAlertFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ExtraUserAlertFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM_ALERT = "alert_param";
    private static final String ARG_PARAM_UIVALUES = "uivalues_param";

    // TODO: Rename and change types of parameters
    private UserAlert userAlert;
    private UIValues uiValues;

    private MoreInfoFragmentListener mListener;

    public ExtraUserAlertFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param alerte Parameter 1.
     * @return A new instance of fragment ExtraUserAlertFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ExtraUserAlertFragment newInstance(UserAlert alerte, UIValues uiValues) {
        ExtraUserAlertFragment fragment = new ExtraUserAlertFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM_ALERT, alerte);
        args.putSerializable(ARG_PARAM_UIVALUES, uiValues);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userAlert = (UserAlert) getArguments().getSerializable(ARG_PARAM_ALERT);
            uiValues = (UIValues) getArguments().getSerializable(ARG_PARAM_UIVALUES);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.moreinfo_fragment_user, container, false);

        setupUIFromValues(v, uiValues);
//        Log.i(APP_FLOW, "is a user alert yessss !!!!");
        beginFetchAlert(userAlert, v);
        setupPhoto(v);
        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
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

        TextView userTitle = view.findViewById(R.id.moreinfo_extra_static_usertitle);
        userTitle.setTextColor(primary);

        TextView plusOneUserTitle = view.findViewById(R.id.moreinfo_extra_static_plusone);
        plusOneUserTitle.setTextColor(primary);

        TextView minusOneUserTitle = view.findViewById(R.id.moreinfo_extra_static_minusone);
        minusOneUserTitle.setTextColor(primary);

        TextView imageTitle = view.findViewById(R.id.more_info_extra_image_title);
        imageTitle.setTextColor(primary);

    }


    @Async
    public void beginFetchAlert(@NonNull final UserAlert userAlert, @NonNull final View v) {

        HttpRequest<User[]> getPlusOneUser = PostRequest.user(userAlert.getPlusOneUsers());
        RequestHandler handler = new RequestHandler<>(getPlusOneUser,
                response -> plusOneresolve(response, v),
                error -> {
                    plusOneresolve(new User[]{}, v);
//                    Log.e(SERVER, "plus-one fail. \n" + error.prettyPrinter());
                });
        handler.handle(Server.AuthorizationHeaders.REQUIRED);

    }


    @Async
    private void fetchMinusOne(@NonNull final User[] plusOneUsers, @NonNull final View v){
        HttpRequest<User[]> getPlusOneUser = PostRequest.user(userAlert.getMinusOneUsers());
        RequestHandler handler = new RequestHandler<>(getPlusOneUser,
                response -> onFetchedAlert(plusOneUsers, response, v),
                error -> {
                    onFetchedAlert(plusOneUsers, new User[]{}, v);
//                    Log.e(SERVER, "minus-one fail. \n" + error.prettyPrinter());
                });
        handler.handle(Server.AuthorizationHeaders.REQUIRED);
    }


    @Callback(method = "beginFetchAlert")
    public void plusOneresolve(User[] plusOneUsers, View v){
        fetchMinusOne(plusOneUsers, v);
    }


    @Callback(method = "fetchMinusOne")
    public void onFetchedAlert(User[] plusOneUsers, User[] minusOneUser, View v) {

        // if the user has exit the activity, stop everything
        if (getActivity() == null){
            return;
        }

        LinearLayout upvoteList = v.findViewById(R.id.moreinfo_extra_upvoteuser);
        for (User user : plusOneUsers){

            LinearLayout newUserLayout = new LinearLayout(getActivity());
            newUserLayout.setOrientation(LinearLayout.HORIZONTAL);

            ImageView icon = new ImageView(getActivity());
            icon.setImageDrawable(user.getIcon(getResources()));

            TextView userName = new TextView(getActivity());
            TextView userScore = new TextView(getActivity());

            String userNameDisplay = "  "+user.getUserName() + " : ";
            userName.setText(userNameDisplay);
            String userCurrentScore = user.getPoints() + "";
            userScore.setText(userCurrentScore);

            newUserLayout.addView(icon);
            newUserLayout.addView(userName);
            newUserLayout.addView(userScore);

            upvoteList.addView(newUserLayout);

        }

        LinearLayout downvoteList = v.findViewById(R.id.moreinfo_extra_downvoteuser);
        for (User user : minusOneUser){
            LinearLayout newUserLayout = new LinearLayout(getActivity());
            newUserLayout.setOrientation(LinearLayout.HORIZONTAL);

            ImageView icon = new ImageView(getActivity());
            icon.setImageDrawable(user.getIcon(getResources()));

            TextView userName = new TextView(getActivity());
            TextView userScore = new TextView(getActivity());

            String userNameDisplay = "  "+user.getUserName() + " : ";
            userName.setText(userNameDisplay);
            String userCurrentScore = user.getPoints() + "";
            userScore.setText(userCurrentScore);

            newUserLayout.addView(icon);
            newUserLayout.addView(userName);
            newUserLayout.addView(userScore);

            downvoteList.addView(newUserLayout);

        }
    }

    private void setupPhoto(View v){

        if (userAlert.getPhotoPath() == null || userAlert.getPhotoPath().equals("")){
//            Log.i(PHOTO_DOWNLOAD, "DEBUG : can't dl image, photoPath is empty or null : " + userAlert.getPhotoPath());
            ImageView photo = v.findViewById(R.id.moreinfo_extra_image);
            photo.setImageDrawable(getResources().getDrawable(R.drawable.cam_icon));

        } else {

            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference ref = storage.getReference().child(userAlert.getPhotoPath());

            ImageView alertImageView = v.findViewById(R.id.moreinfo_extra_image);

            ref.getDownloadUrl().addOnSuccessListener(uri -> {
                        Picasso.get().load(uri).placeholder(R.drawable.cam_icon).into(alertImageView);
                    }
            ).addOnFailureListener(exception -> {
                ;
//                Log.e(PHOTO_DOWNLOAD, "download error" + exception.getMessage());
            });
        }



    }

}


