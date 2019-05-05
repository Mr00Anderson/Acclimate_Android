package com.acclimate.payne.simpletestapp.map.infoWindow;


import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.acclimate.payne.simpletestapp.R;
import com.acclimate.payne.simpletestapp.activities.IDisplayInformation;
import com.acclimate.payne.simpletestapp.activities.alertForm.NewAlertFormActivity;
import com.acclimate.payne.simpletestapp.activities.main.MainActivity;
import com.acclimate.payne.simpletestapp.activities.moreInfosAlerts.MoreInfoAlertsActivity;
import com.acclimate.payne.simpletestapp.alerts.UserAlert;
import com.acclimate.payne.simpletestapp.appUtils.App;
import com.acclimate.payne.simpletestapp.appUtils.AppTag;
import com.acclimate.payne.simpletestapp.appUtils.Async;
import com.acclimate.payne.simpletestapp.appUtils.Callback;
import com.acclimate.payne.simpletestapp.appUtils.InternetCheck;
import com.acclimate.payne.simpletestapp.server.Server;
import com.acclimate.payne.simpletestapp.server.requests.HttpRequest;
import com.acclimate.payne.simpletestapp.server.requests.PatchRequest;
import com.acclimate.payne.simpletestapp.server.requests.RequestHandler;
import com.acclimate.payne.simpletestapp.server.requests.exceptions.RequestErrorException;
import com.acclimate.payne.simpletestapp.user.User;
import com.acclimate.payne.simpletestapp.user.karma.VoteKarma;
import com.acclimate.payne.simpletestapp.user.karma.karmaAPI.KarmaBuilder;
import com.acclimate.payne.simpletestapp.user.karma.karmaAPI.points.KarmaFail;
import com.acclimate.payne.simpletestapp.user.karma.karmaAPI.points.KarmaRequired;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.osmdroid.views.overlay.infowindow.InfoWindow;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static com.acclimate.payne.simpletestapp.user.karma.KarmaRequirementsValues.PLUS_ONE;

/**
 *
 */
public class UserInfoWindow extends PinInfoWindow implements VoteKarma {

    private static boolean request_pending;

    // for displaying information in the attached activity
    private IDisplayInformation displayer;

    // the user that posted this alert
    private UserAlert userAlerte;

    // unique views
    private Button btnPlus;
    private Button btnMinus;
    private TextView userBubbleScore;
    private TextView certitude;

    // on button pressed change
    private List<String> currentList;
    private String field;
    private Button otherButton;
    private Drawable currentPressed;
    private Drawable otherPressed;
    private Drawable currentRegular;
    private Button currentButton;

    //
    private VoteKarma karmaProxy;

    private View.OnClickListener btnDefaultListener = this::defaultPlusMinusOnClick;

    public UserInfoWindow(@NonNull UserAlert userAlert, int layoutResId, @NonNull MainActivity main) {
        super(userAlert, layoutResId, main.getMapDisplay().getMap());
        this.userAlerte = userAlert;
        this.displayer = main.getMainSnackbarDisplayer();
        karmaProxy = KarmaBuilder.createPointHandler(VoteKarma.class, this);
    }

    public UserInfoWindow(@NonNull UserAlert userAlert, int layoutResId, @NonNull NewAlertFormActivity form) {
        super(userAlert, layoutResId, form.getMini_map());
        this.userAlerte = userAlert;
        this.displayer = form.getPinBubbleDisplayer();
        karmaProxy = KarmaBuilder.createPointHandler(VoteKarma.class, this);
    }


    @Override
    public int getPoints() { return App.getInstance().getCurrentUser().getPoints(); }

    @Override
    public void onOpen(Object arg0) {
        super.onOpen(arg0);

        List<String> plusOneList = userAlerte.getPlusOneUsers();
        List<String> minusOneList = userAlerte.getMinusOneUsers();

        User user_of_alert = userAlerte.getUser();
        User currentUser = App.getInstance().getCurrentUser();

//        Log.i(PIN_FLOW, "APP USER = " + (currentUser == null ? "null": currentUser.getuId()));
//        Log.i(PIN_FLOW, "ALERT USER = " + (user_of_alert == null ? "null": user_of_alert.getuId()));

        if (currentUser == null){
            btnPlus.setBackground(mView.getResources().getDrawable(R.drawable.plus_one_icon));
            btnMinus.setBackground(mView.getResources().getDrawable(R.drawable.minus_one_icon));
        } else if (user_of_alert != null) {

//            Log.i(PIN_FLOW, "user alert : \n" + userAlerte.toString());

            // plus one contains user
            if (plusOneList.contains(currentUser.getuId())) {
                btnPlus.setBackground(mView.getResources().getDrawable(R.drawable.plus_one_pressed));
            } else {
                btnPlus.setBackground(mView.getResources().getDrawable(R.drawable.plus_one_icon));
            }

            // minus one contains user minus one contains user
            if (minusOneList.contains(currentUser.getuId())) {
                btnMinus.setBackground(mView.getResources().getDrawable(R.drawable.minus_one_pressed));
            } else {
                btnMinus.setBackground(mView.getResources().getDrawable(R.drawable.minus_one_icon));
            }

        } else {

            // unfortunately, the User of this alerte is unavailble (null) : we show default
            // icons and prevent NullPointerException with new Listeners on buttons that
            // also display an error message for the user
            btnPlus.setBackground(mView.getResources().getDrawable(R.drawable.plus_one_icon));
            btnMinus.setBackground(mView.getResources().getDrawable(R.drawable.minus_one_icon));
            View.OnClickListener impossible = view -> displayer.showOnView(R.string.impossible_to_vote, mView);

            btnPlus.setOnClickListener(impossible);
            btnMinus.setOnClickListener(impossible);

        }

        certitude.setText(userAlerte.getCertitude());
        String currentScore = userAlerte.getScore() + "";
        userBubbleScore.setText(currentScore);

        if (!request_pending) {
            btnPlus.setOnClickListener(btnDefaultListener);
            btnMinus.setOnClickListener(btnDefaultListener);
        } else {
            cancelAllButtons();
        }

    }

    @Async
    private void defaultPlusMinusOnClick(View button){

        currentButton = (Button) button;
        setupViewsBasedOnButtonClicked(currentButton);

        User user_of_alert = userAlerte.getUser();
        User currentUser = App.getInstance().getCurrentUser();

        // erreur :( l'utilisateur source de l'alerte n'a pas pu être retrouvé
        if (user_of_alert == null) {
            // todo : remove ? find something beter to do ?
            displayer.errorMessage("DEBUG : impossible de voter, car l'utilisateur de l'alerte est null");
            return;
        }

        // impossible de voter si pas connecté
        if (currentUser == null || currentUser.getuId() == null){
            displayer.extraSpecialFocus(R.string.register_required_to_vote, null);
            return;
        }

        // si c'est sa propre alerte, il ne peut pas voter pour lui!
        if (currentUser.getuId().equals(user_of_alert.getuId())){
            displayer.showOnFocus(R.string.same_alert, button);
            return;
        }

        // Tenter d'envoyer la requête qui ne contient que le current app
        // uId dans le champ plusOn ouminusOne
        boolean isConnectedToInternet = InternetCheck.synchronous(displayer.getContext());
        if (!isConnectedToInternet){
            displayer.showOnFocus(R.string.no_connection_for_vote_alert, button);
            return;
        }

        if (userAlerte.getId() == null){
 //           Log.e(SERVER, alerte.toString());
            displayer.showOnFocus("une erreur s'est produite : l'utilisateur de l'alerte est null", button);
            this.close();
        } else {
            Map<String, Object> body = new TreeMap<>();
            body.put(field, currentUser.getuId());
            karmaProxy.patchAlertScore(body, userAlerte.getId());
        }

        // remettre l'icône par défaut s'il avait déja appuyer dessus
        if (currentList.contains(currentUser.getuId())){
            button.setBackground(currentRegular);
        }

    }


    public void whileRequestPending(View button){
        cancelAllButtons();
        request_pending = true;

        // mettre à jour UNIQUEMENT l'affichage
        button.setBackground(currentPressed);
        otherButton.setBackground(otherPressed);

        String updatedScore = userAlerte.getScore() + "";
        userBubbleScore.setText(updatedScore);
        displayer.showOnView(R.string.vote_completed, mMapView);

        userBubbleScore.setText("---");

    }


    @Override
    protected void onMoreInfoClick(View view) {
        Intent intent = new Intent(displayer.getContext(), MoreInfoAlertsActivity.class);
        intent.putExtra(AppTag.ALERT_TRANSFER_MORE_INFO, this.userAlerte);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Context ctx = displayer.getContext();
        ctx.startActivity(intent);
    }

    @Override
    protected void bringInViews() {
        layout = mView.findViewById(R.id.user_bubble_layout);
        btnPlus = mView.findViewById(R.id.user_bubble_button_plus);
        btnMinus = mView.findViewById(R.id.user_bubble_button_minus);
        btnMoreInfos = mView.findViewById(R.id.user_bubble_more_info);
        userBubbleScore = mView.findViewById(R.id.user_bubble_alert_current_score);
        title = mView.findViewById(R.id.user_bubble_title);
        subCat = mView.findViewById(R.id.user_bubble_subcat);
        description = mView.findViewById(R.id.user_bubble_description);
        certitude = mView.findViewById(R.id.user_bubble_certitude);
    }



    @Callback(method = "defaultPlusMinusOnClick")
    public void onRequestReceived(Map response){

        userAlerte = (new ObjectMapper()).convertValue(response, UserAlert.class);
        // this.userAlerte = userAlerte;
//        Toast.makeText(displayer.getContext(), "DEBUG : requête effectuée avec succès", Toast.LENGTH_SHORT).show();

        request_pending = false;

//        Log.i(SERVER_PATCH, response.toString());
        // String userId = App.getInstance().getCurrentUser().getuId();

        // mettre à jour les donnée de l'alerte
        //currentList.add(userId);
        //otherList.remove(userId);

        //userAlerte.setScore(plusOneList.size() - minusOneList.size());
        String score = userAlerte.getScore() + "";
        userBubbleScore.setText(score);

        putBackkAllListeners();
        mView.invalidate();

    }


    @Callback(method = "defaultPlusMinusOnClick") //this
    public void onFailedRequest(RequestErrorException exception){

//        Log.e(PIN_FLOW, "has internet : " + InternetCheck.synchronous(displayer.getContext()));
        request_pending = false;

        if (!InternetCheck.synchronous(displayer.getContext())){
            displayer.showOnFocus(R.string.no_connection_for_vote_alert, currentButton);
        } else {
            // une erreur
            InfoWindow.closeAllInfoWindowsOn(mMapView);
            Toast.makeText(displayer.getContext(), "Une erreur s'est produite", Toast.LENGTH_SHORT).show();
//            Log.e(SERVER_PATCH, exception.prettyPrinter());
//            Log.e(SERVER_PATCH, exception.getRequest().getRequestEntity().getBody().toString());
//            Log.e(SERVER_PATCH, "root exception : ", exception.getRootException());
        }

        putBackkAllListeners();


    }


    private void setupViewsBasedOnButtonClicked(Button button){

        if (button.getId() == R.id.user_bubble_button_plus) {

            currentList = userAlerte.getPlusOneUsers();
            field = Server.PLUS_ONE_USER_ALERT;
            otherButton = mView.findViewById(R.id.user_bubble_button_minus);

            currentPressed = mView.getResources().getDrawable(R.drawable.plus_one_pressed);
            otherPressed = mView.getResources().getDrawable(R.drawable.minus_one_icon);
            currentRegular = mView.getResources().getDrawable(R.drawable.plus_one_icon);

        } else if (button.getId() == R.id.user_bubble_button_minus) {

            currentList = userAlerte.getMinusOneUsers();
            field = Server.MINUS_ONE_USER_ALERT;
            otherButton = mView.findViewById(R.id.user_bubble_button_plus);

            otherPressed = mView.getResources().getDrawable(R.drawable.plus_one_icon);
            currentPressed = mView.getResources().getDrawable(R.drawable.minus_one_pressed);
            currentRegular = mView.getResources().getDrawable(R.drawable.minus_one_icon);

        } else {
            throw new RuntimeException("Illegal click : not registered on plus/minus alert button");
        }

    }


    @KarmaRequired(PLUS_ONE)
    public void patchAlertScore(Map<String, Object> body, @NonNull String alertId) {

        whileRequestPending(currentButton);
//        Log.e(SERVER, "REQUEST PLUS/MINUS ONE body = " + body.toString());
        HttpRequest<Map> request = PatchRequest.alert(body, alertId);   //   response -> onRequestReceived(response) //
        RequestHandler<Map> handler = new RequestHandler<>(request, this::onRequestReceived, this::onFailedRequest);
        handler.handle(Server.AuthorizationHeaders.REQUIRED);

    }

    @KarmaFail(method = "patchAlertScore")
    public void notEnoughKarmaToVote(Map<String, Object> body, String alertId) {
        displayer.showOnFocus(R.string.not_enough_karma_to_vote, currentButton);
        putBackkAllListeners();
    }


    @Override
    public void userNotFound() {
        displayer.extraSpecialFocus(R.string.register_required_to_vote, null);
    }

    public void cancelAllButtons() {
//        Log.i(KARMA, "cancel all buttons");
        cancelVoteButtons();
        btnMoreInfos.setOnClickListener(view -> {});
    }


    private void cancelVoteButtons() {
        btnPlus.setOnClickListener(view -> {});
        btnMinus.setOnClickListener(view -> {});
    }


    private void putBackVoteButtonListeners(){
        btnPlus.setOnClickListener(btnDefaultListener);
        btnMinus.setOnClickListener(btnDefaultListener);
    }

    private void putBackkAllListeners(){
        putBackVoteButtonListeners();
        btnMoreInfos.setOnClickListener(this::onMoreInfoClick);
    }



}
