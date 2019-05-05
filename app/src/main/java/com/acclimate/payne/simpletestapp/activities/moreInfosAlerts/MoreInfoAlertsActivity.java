package com.acclimate.payne.simpletestapp.activities.moreInfosAlerts;

import android.app.FragmentTransaction;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.acclimate.payne.simpletestapp.R;
import com.acclimate.payne.simpletestapp.alerts.AlertWrapper;
import com.acclimate.payne.simpletestapp.alerts.BasicAlert;
import com.acclimate.payne.simpletestapp.alerts.UserAlert;
import com.acclimate.payne.simpletestapp.alerts.alertViewModel.AlertViewModel;
import com.acclimate.payne.simpletestapp.alerts.alertViewModel.AlertViewModelSingletonFactory;
import com.acclimate.payne.simpletestapp.appUtils.App;
import com.acclimate.payne.simpletestapp.appUtils.AppTag;
import com.acclimate.payne.simpletestapp.deviceStorage.localStorage.FileLocalStorage;
import com.acclimate.payne.simpletestapp.server.Server;
import com.acclimate.payne.simpletestapp.server.requests.DeleteRequest;
import com.acclimate.payne.simpletestapp.server.requests.HttpRequest;
import com.acclimate.payne.simpletestapp.server.requests.RequestHandler;
import com.acclimate.payne.simpletestapp.server.requests.exceptions.RequestErrorException;
import com.acclimate.payne.simpletestapp.user.User;
import com.acclimate.payne.simpletestapp.user.karma.VoteKarma;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import de.cketti.mailto.EmailIntentBuilder;

import static com.acclimate.payne.simpletestapp.appUtils.AppConfig.ALERT_WRAPPER_FILENAME;
import static com.acclimate.payne.simpletestapp.appUtils.AppTag.APP_FLOW;
import static com.acclimate.payne.simpletestapp.appUtils.AppTag.PHOTO_UPLOAD;

public class MoreInfoAlertsActivity extends AppCompatActivity
        implements MoreInfoFragmentListener, VoteKarma {

    private boolean alert_is_user;
    CollapsingToolbarLayout collapsingToolbarLayout;
    Toolbar toolbar;
    private BasicAlert basicAlert;
    private MenuItem trash;
    private MenuItem edit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.moreinfo_activity);

        toolbar = findViewById(R.id.moreinfo_toolbar);
        setSupportActionBar(toolbar);

        AppBarLayout abl = findViewById(R.id.moreinfo_app_bar);
        abl.addOnOffsetChangedListener( (appBarLayout, verticalOffset) -> {

            if (trash == null || edit == null) {
                return;
            } else {
                edit.getIcon().setAlpha(Math.abs(verticalOffset/2));
                trash.getIcon().setAlpha(Math.abs(verticalOffset/2));
            }

        });


        this.basicAlert = (BasicAlert) getIntent().getSerializableExtra(AppTag.ALERT_TRANSFER_MORE_INFO);;
        FloatingActionButton fab =  findViewById(R.id.moreinfo_fab);
        fab.setOnClickListener( view -> {
        Intent emailIntent = EmailIntentBuilder.from(this)
                    .to(getString(R.string.report_email))
                    .subject(getString(R.string.report_subject) + basicAlert.getId())
                    .body(getString(R.string.report_body))
                .build();
        startActivity(emailIntent);
        });

        alert_is_user = (basicAlert instanceof UserAlert);

        collapsingToolbarLayout = findViewById(R.id.moreinfo_collapsing_toolbar_layout);
        collapsingToolbarLayout.setExpandedTitleColor(Color.TRANSPARENT);
        collapsingToolbarLayout.setTitle("Informations");

        UIValues uiValues = new UIValues(basicAlert);
        setupUIFromValues(uiValues);
        if (basicAlert instanceof UserAlert){
            setupUIFromAlert(basicAlert);
            MoreInfoMenuToolBarListener menuListener = new MoreInfoMenuToolBarListener(this, (UserAlert) basicAlert);
            toolbar.setOnMenuItemClickListener(menuListener);

        } else {
            setupUIFromAlert(basicAlert);
        }

        LinearLayout sourceLayout = findViewById(R.id.moreinfo_source_container);
        FragmentTransaction fragTransaction = getFragmentManager().beginTransaction();
        if (basicAlert instanceof UserAlert) {
            fragTransaction
                    .replace(R.id.moreinfo_extra_container, ExtraUserAlertFragment.newInstance((UserAlert) basicAlert, uiValues))
                    .replace(R.id.moreinfo_alertscore_container, AlertScoreFragment.newInstance((UserAlert) basicAlert, uiValues));
            setupUserInfo(sourceLayout, ((UserAlert) basicAlert).getUser());

        } else {
            fragTransaction
                    .replace(R.id.moreinfo_extra_container, NullUserFragment.newInstance());
            TextView sourceTextView = new TextView(getApplicationContext());
            sourceTextView.setText(basicAlert.getSource());
            sourceLayout.addView(sourceTextView);
        }

        fragTransaction.commit();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        if (alert_is_user) {
            getMenuInflater().inflate(R.menu.moreinfo_collpased_menu, menu);

            trash = toolbar.getMenu().findItem(R.id.moreinfo_menu_trash);
            edit = toolbar.getMenu().findItem(R.id.moreinfo_menu_edit);
            trash.setIcon(getResources().getDrawable(R.drawable.ic_trash));
            edit.setIcon(getResources().getDrawable(R.drawable.ic_edit));

            User current = App.getInstance().getCurrentUser();

            boolean enabeled;
            if (current == null) {
                enabeled = false;
            }else {
                enabeled = ((UserAlert) basicAlert).getUser().equals(current);
            }

            trash.setEnabled(enabeled);
            edit.setEnabled(enabeled);

            return super.onCreateOptionsMenu(menu);

        }

        return super.onCreateOptionsMenu(menu);

    }

    private void setupUIFromValues(UIValues values) {

        int primary = getResources().getColor(values.getPrimaryColor());


        collapsingToolbarLayout.setContentScrimColor(primary);

        ImageView barBackdrop = findViewById(R.id.moreinfo_backdrop);
        barBackdrop.setImageDrawable(getResources().getDrawable(values.getBackdropImage()));

        TextView titleStatic = findViewById(R.id.moreinfo_static_titre);
        titleStatic.setTextColor(primary);

        TextView catStatic = findViewById(R.id.moreinfo_static_cat);
        catStatic.setTextColor(primary);

        TextView dateStatic = findViewById(R.id.moreinfo_static_date);
        dateStatic.setTextColor(primary);

        TextView subcatStatic = findViewById(R.id.moreinfo_static_subcat);
        subcatStatic.setTextColor(primary);

        TextView sourceStatic = findViewById(R.id.moreinfo_static_source);
        sourceStatic.setTextColor(primary);

        TextView descStatic = findViewById(R.id.moreinfo_static_desc);
        descStatic.setTextColor(primary);

        TextView severiteStatic = findViewById(R.id.moreinfo_static_severite);
        severiteStatic.setTextColor(primary);

        TextView certitudeStatic = findViewById(R.id.moreinfo_static_certitide);
        certitudeStatic.setTextColor(primary);

    }


    private void setupUIFromAlert(BasicAlert alert){

        TextView cat = findViewById(R.id.moreinfo_cat);
        cat.setText(alert.getType());

        TextView subcat = findViewById(R.id.moreinfo_subcat);
        subcat.setText(alert.getSousCategorie());

        TextView date = findViewById(R.id.moreinfo_date);
        date.setText(alert.getDateDeMiseAJour());

        // TextView source = findViewById(R.id.moreinfo_source_content);
        // source.setText(alert.getSource());

        TextView cert = findViewById(R.id.moreinfo_certitide);
        cert.setText(alert.getCertitude());

        TextView sev = findViewById(R.id.moreinfo_severite);
        sev.setText(alert.getSeverite());

        TextView desc = findViewById(R.id.moreinfo_desc);
        desc.setText(alert.getDescription());

        TextView title = findViewById(R.id.moreinfo_titre);
        title.setText(alert.getNom());

    }


    private void setupUIFromAlert(UserAlert alert, UIValues uiValues){
        setupUIFromAlert((BasicAlert) alert);

        Log.i(APP_FLOW, "setup UI USER ALERT");

        // put fragments in their places
        FragmentTransaction fragTransaction = getFragmentManager().beginTransaction();
        fragTransaction
                .replace(R.id.moreinfo_alertscore_container, AlertScoreFragment.newInstance(alert, uiValues))
                .replace(R.id.moreinfo_extra_container, ExtraUserAlertFragment.newInstance(alert, uiValues))
                .commit();

    }





    @Override
    public void onFragmentInteraction(BasicAlert alert) {

    }


    @Override
    public void patchAlertScore(Map<String, Object> body, String userId) {

    }

    @Override
    public void notEnoughKarmaToVote(Map<String, Object> body, String userId) {

    }

    @Override
    public void userNotFound() {

    }


    public void deleteAlert(DialogInterface dialog, int which){
        if (basicAlert.getId() == null) {
            inDeleteFail(new RequestErrorException());
            return;
        }

        if (alert_is_user) {
            HttpRequest<String> deleteAlert = DeleteRequest.alert(basicAlert.getId());
            RequestHandler<String> h = new RequestHandler<>(deleteAlert, this::onDeleteSuccess, this::inDeleteFail);
            h.handle(Server.AuthorizationHeaders.REQUIRED);

        }
    }

    private void onDeleteSuccess(String response) {

        Snackbar.make(findViewById(R.id.moreinfo_main),
                "Effacée  tout jamais :'( ... elle n'métait pas assez bonne ?",
                Snackbar.LENGTH_SHORT).show();

        // remove from view model
        AlertViewModel vm = ViewModelProviders
                .of(this, AlertViewModelSingletonFactory.getInstance())
                .get(AlertViewModel.class);
        List<UserAlert> tmp = vm.getUserAlerts().getValue();
        tmp.remove((UserAlert) basicAlert);
        vm.getUserAlerts().setValue(tmp);


        // save new wrapper ton phone disk
        FileLocalStorage<AlertWrapper> wrapperStorage =
                new FileLocalStorage<>(ALERT_WRAPPER_FILENAME, new AlertWrapper(), this);
        try {
            AlertWrapper wrapper = wrapperStorage.read();
            wrapper.setUser(tmp.toArray(new UserAlert[tmp.size()]));
            wrapperStorage.setData(wrapper);
            wrapperStorage.write();
        } catch (JsonGenerationException jsonError) {
            Log.e("STORAGE", "could not store alertWrapper : " + jsonError.getMessage());

        } catch (IOException ioe) {
            Log.e("STORAGE", "could not store alertWrapper : " + ioe.getMessage());
        }

        // delete photo from Firebase server
        String path = ((UserAlert) basicAlert).getPhotoPath();
        Log.i(PHOTO_UPLOAD, "delete photopath = " + path);
        if (path != null && !path.equals("")) {
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference().child(path);

            // Delete the file
            storageRef.delete()
                    .addOnSuccessListener(aVoid -> {
                        Log.i(PHOTO_UPLOAD, "photo deleted");
                    })
                    .addOnFailureListener(exception -> {
                        Log.e(PHOTO_UPLOAD, "failed to delete photo deleted");
                    });
        }
        onBackPressed();
    }

    private void inDeleteFail(@Nullable RequestErrorException error) {
        Snackbar.make(findViewById(R.id.moreinfo_main),
                "Veuillez réessayer plus tard, nous sommes désolé du contre temps",
                Snackbar.LENGTH_SHORT).show();
    }


    private void setupUserInfo(LinearLayout sourceLayout, User user){

        if(user != null) {

            ImageView icon = new ImageView(getApplicationContext());
            icon.setImageDrawable(user.getIcon(getResources()));

            TextView userName = new TextView(getApplicationContext());
            TextView userScore = new TextView(getApplicationContext());

            String userNameDisplay = "  "+user.getUserName() + " : ";
            userName.setText(userNameDisplay);
            String userCurrentScore = user.getPoints() + "";
            userScore.setText(userCurrentScore);

            sourceLayout.addView(icon);
            sourceLayout.addView(userName);
            sourceLayout.addView(userScore);

        } else {
            TextView defaultText = new TextView(getApplicationContext());
            defaultText.setText("Utilisateur inconnu");
            sourceLayout.addView(defaultText);
        }

    }


}
