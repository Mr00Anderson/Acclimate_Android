package com.acclimate.payne.simpletestapp.activities.alertForm;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.acclimate.payne.simpletestapp.R;
import com.acclimate.payne.simpletestapp.activities.IDisplayInformation;
import com.acclimate.payne.simpletestapp.activities.NullDisplayer;
import com.acclimate.payne.simpletestapp.alerts.AlertController;
import com.acclimate.payne.simpletestapp.alerts.AlertWrapper;
import com.acclimate.payne.simpletestapp.alerts.Geometry;
import com.acclimate.payne.simpletestapp.alerts.UserAlert;
import com.acclimate.payne.simpletestapp.alerts.alertViewModel.AlertViewModel;
import com.acclimate.payne.simpletestapp.alerts.alertViewModel.AlertViewModelSingletonFactory;
import com.acclimate.payne.simpletestapp.appUtils.App;
import com.acclimate.payne.simpletestapp.appUtils.InternetCheck;
import com.acclimate.payne.simpletestapp.deviceStorage.localStorage.FileLocalStorage;
import com.acclimate.payne.simpletestapp.map.infoWindow.UserInfoWindow;
import com.acclimate.payne.simpletestapp.photo.PhotoUtils;
import com.acclimate.payne.simpletestapp.server.Server;
import com.acclimate.payne.simpletestapp.server.requests.HttpRequest;
import com.acclimate.payne.simpletestapp.server.requests.PostRequest;
import com.acclimate.payne.simpletestapp.server.requests.RequestHandler;
import com.acclimate.payne.simpletestapp.server.requests.exceptions.RequestErrorException;
import com.acclimate.payne.simpletestapp.user.User;
import com.fasterxml.jackson.core.JsonGenerationException;

import org.osmdroid.api.IMapController;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import lombok.Getter;
import lombok.Setter;

import static com.acclimate.payne.simpletestapp.appUtils.App.vLOLLIPOP;
import static com.acclimate.payne.simpletestapp.appUtils.AppConfig.ALERT_WRAPPER_FILENAME;
import static com.acclimate.payne.simpletestapp.appUtils.AppTag.SERVER_HANDLE;

public final class  NewAlertFormActivity extends AppCompatActivity {

    private AlertFormSpinnerSelector alertFormSpinnerSelector;
    @Getter private MapView mini_map;

    @Getter @Setter private Marker mapMarker;
    @Getter @Setter private String typeSelected = "";
    @Getter @Setter private String severity = "";
    @Getter @Setter private GeoPoint center;

    @Getter @Setter private Button btn_accept;
    @Getter @Setter private Button btn_cancel;
    @Getter @Setter private String encodedImageString;
    @Getter @Setter private TextView titleFormField;
    @Getter @Setter private TextView descriptionFormField;
    @Getter @Setter private Spinner severity_spinner;
    @Getter @Setter private Spinner type_spinner;
    @Getter @Setter private Spinner subtype_spinner;
    @Getter @Setter private View.OnClickListener onAcceptListener;
    @Getter @Setter private View.OnClickListener onCancelListener;

    @Getter private IDisplayInformation pinBubbleDisplayer;

    @Getter private HashMap<String, Drawable> types = new HashMap<>(4);
    private static final double CENTER_OFFSET = 0.0021;
    private static final double ZOOM_LEVEL = 17d;

    public final ArrayList<String> listSubtypeAlertFeu = new ArrayList<>();
    public final ArrayList<String> listSubtypeAlertEau = new ArrayList<>();
    public final ArrayList<String> listSubtypeAlertMeteo = new ArrayList<>();
    public final ArrayList<String> listSubtypeAlertSeiseme = new ArrayList<>();

    private AlertFormPhotoHelper photoHelper;
    private String mCurrentPhotoPath;
    @Setter
    private Bitmap image;

    AlertDialog wait;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
            getWindow().setAllowEnterTransitionOverlap(true);
        }

        setContentView(R.layout.newalertform_activity);

        alertFormSpinnerSelector = new AlertFormSpinnerSelector(this);
        pinBubbleDisplayer = new NullDisplayer(this);

        types.put("Eau",    getResources().getDrawable(R.drawable.pin_user_water));
        types.put("Feu",    getResources().getDrawable(R.drawable.pin_user_fire));
        types.put("Seisme", getResources().getDrawable(R.drawable.pin_user_earth));
        types.put("Météo",  getResources().getDrawable(R.drawable.pin_user_wind));


        Toolbar toolbar = findViewById(R.id.alert_form_toolbar);
        setSupportActionBar(toolbar);

        NewAlertFormIntent intent = new NewAlertFormIntent(getIntent());
        final GeoPoint center = intent.getPoint();
        this.center = center;
        // All configuration for displaying the map correctly



        setupMapView(center);
        setupAlertForm();



        // All configuration for displaying the form to aks user for input about the alert


    }


    /**
     * Update this to get lists from server at app launch.
     */
    private void initAlertSubCategoryArrays(){

        Collections.addAll(listSubtypeAlertFeu, getResources().getStringArray(R.array.sous_type_feu));
        Collections.addAll(listSubtypeAlertEau, getResources().getStringArray(R.array.sous_type_eau));
        Collections.addAll(listSubtypeAlertMeteo, getResources().getStringArray(R.array.sous_type_meteo));
        Collections.addAll(listSubtypeAlertSeiseme, getResources().getStringArray(R.array.sous_type_seisme));

    }


    /**
     * All the call to configurations to prevent any interactions with the map
     * @param center thecenter pin
     */
    private void makeMapStatic(MapView mini_map, GeoPoint center){

        mini_map.setBuiltInZoomControls(false); // disable zoom controls
        mini_map.setMultiTouchControls(false);  // disable zoon controls
        mini_map.setFlingEnabled(false);
        mini_map.setScrollableAreaLimitDouble(
                new BoundingBox(center.getLatitude(), center.getLongitude(),center.getLatitude(), center.getLongitude()));
        mini_map.setMaxZoomLevel(ZOOM_LEVEL); mini_map.setMinZoomLevel(ZOOM_LEVEL);

        //disable double tap
        mini_map.getOverlayManager().add(new Overlay() {
            @Override public void draw(Canvas c, MapView osmv, boolean shadow) {}
            @Override public boolean onDoubleTap(MotionEvent e, MapView mapView) { return true; }
        });
    }

    /**
     * This method is called during the onCreate to setup everything
     * that is related tho the mini map dispay of this Activity
     * @param center  the center where the pin should be placed
     */
    private void setupMapView(GeoPoint center){

        // final GeoPoint center = new GeoPoint(intent.getLat(), intent.getLng());
        // we want the view to be a little more to the south
        final GeoPoint offCenter = new GeoPoint(
                center.getLatitude() + CENTER_OFFSET,
                center.getLongitude());

        if (vLOLLIPOP) {
            mini_map = findViewById(R.id.mini_map_v21);
        } else {
            mini_map = findViewById(R.id.mini_map);
        }

        // Makes the map static
        makeMapStatic(mini_map, offCenter);
        mapMarker = new Marker(mini_map);

        mapMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        mapMarker.setPosition(center);
        mini_map.getOverlayManager().add(mapMarker);

        UserInfoWindow userInfoWindow = new UserInfoWindow(UserAlert.emptyAlert(), R.layout.infobubble_user, this);
        mapMarker.setInfoWindow(userInfoWindow);
        mapMarker.showInfoWindow();

        userInfoWindow.cancelAllButtons();

        IMapController miniMapController = mini_map.getController();
        miniMapController.setCenter(offCenter);
        miniMapController.setZoom(ZOOM_LEVEL);


    }




    private void setupAlertForm(){

        btn_accept = findViewById(R.id.btn_alert_form_accept);
        btn_cancel = findViewById(R.id.btn_alert_form_cancel);

        btn_accept.setOnClickListener(this::onAcceptBtnPressed);
        btn_cancel.setOnClickListener(this::onCancelBtnPressed);

        // fetches the good values for subCategory spinners
        initAlertSubCategoryArrays();
        initSpinnerContent();

        titleFormField = findViewById(R.id.alert_name);
        descriptionFormField = findViewById(R.id.alert_description);

        // change bubble info as user type
        titleFormField.addTextChangedListener(new SimpleTextUpdater(this, R.id.user_bubble_title));
        descriptionFormField.addTextChangedListener(new SimpleTextUpdater(this, R.id.user_bubble_description));


        TextView alert_score = findViewById(R.id.user_bubble_alert_current_score);
        alert_score.setText("0");


        /// -------------------------
        /// photo icon

        ImageView photoView = findViewById(R.id.add_photo_icon);
        photoHelper = new AlertFormPhotoHelper(this, photoView);
        photoView.setOnClickListener(this :: onPhotoBtnPressed);


    }


    private void initSpinnerContent(){

        severity_spinner = findViewById(R.id.severity_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.severity, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        severity_spinner.setAdapter(adapter);
        severity_spinner.setOnItemSelectedListener(alertFormSpinnerSelector);


        type_spinner = findViewById(R.id.form_spinner_alert_type);
        ArrayAdapter<CharSequence> type_adapter = ArrayAdapter.createFromResource(this,
                R.array.alert_type, android.R.layout.simple_spinner_item);
        type_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        type_spinner.setAdapter(type_adapter);
        type_spinner.setOnItemSelectedListener(alertFormSpinnerSelector);


        subtype_spinner = findViewById(R.id.form_spinner_subtype);
        ArrayAdapter<CharSequence> subtype_adapter = ArrayAdapter.createFromResource(this,
                R.array.sous_type_feu, android.R.layout.simple_spinner_item);
        subtype_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        subtype_spinner.setAdapter(subtype_adapter);
        subtype_spinner.setOnItemSelectedListener(alertFormSpinnerSelector);


    }


    private UserAlert buildAlertInstance(){

        Geometry geom = new Geometry();
        geom.setCoordinates(new double[]{center.getLatitude(), center.getLongitude()});
        geom.setType("Point");

        User mUser = App.getInstance().getCurrentUser();

        UserAlert al = (UserAlert) new AlertController.Builder<>(UserAlert.class)
                .nom(titleFormField.getText().toString())
                .source(mUser == null ? "" : mUser.getuId())
                .territoire(((EditText)findViewById(R.id.alert_territoire)).getText().toString())
                .dateDeMiseAJour(Server.getCurrentTimeFormatted())
                .description(descriptionFormField.getText().toString())
                .type(typeSelected)
                .geometry(geom)
                .certitude(UserAlert.BASE_CERTITUDE)
                .severite(severity)
                .urgence("")
                .sousCategorie(((Spinner) findViewById(R.id.form_spinner_subtype)).getSelectedItem().toString())
                .count("")
                .lat(geom.getLat())
                .lng(geom.getLng())
                .plusOneUsers(new ArrayList<>())
                .minusOneUsers(new ArrayList<>())
                .score(1)
                .user(mUser)
                .build();

        if (image != null){
            PhotoUtils.initPhotoPath(al);
        }

        return al;

    }


    private void onAcceptBtnPressed(View view){

        UserAlert toPost = buildAlertInstance();

        HttpRequest<UserAlert> postAlert = PostRequest.alert(toPost);
        RequestHandler<UserAlert> handler = new RequestHandler<>(postAlert, this::onRequestSuccess, this::onRequestFailure);

        if (InternetCheck.synchronous(this)){

            LayoutInflater layoutInflater = LayoutInflater.from(this);
            View progressDialogBox = layoutInflater.inflate(R.layout.progress_dialog_layout, null);
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setView(progressDialogBox).setTitle("S.V.P. veuillez patienter...");
            ProgressBar progressBar1 = progressDialogBox.findViewById(R.id.prog_dailog_bar);
            progressBar1.setIndeterminate(true);
            wait = alertDialogBuilder.create();
            wait.show();

/*
            Log.i("_auth", "" + App.getInstance().getCurrentUser());
            wait = new AlertDialog.Builder(this)
                    .setMessage("Veuillez patienter")
                    .setView(R.layout.progress_dialog_layout)
                    .create();
            wait.show();
*/

            handler.handle(Server.AuthorizationHeaders.REQUIRED);
        } else {
            Toast.makeText(this, "Vous devez être connecté à internet", Toast.LENGTH_SHORT).show();
        }

    }

    private void onRequestSuccess(UserAlert alert){
        Toast.makeText(this,
                getString(R.string.on_post_alert_success), Toast.LENGTH_SHORT).show();
        Log.i(SERVER_HANDLE, alert.toString());

        AlertViewModel alertModel = ViewModelProviders
                .of(this, AlertViewModelSingletonFactory.getInstance())
                .get(AlertViewModel.class);

        ArrayList<UserAlert> currentAlerts = (ArrayList<UserAlert>) alertModel.getUserAlerts().getValue();
        currentAlerts.add(alert);
        alertModel.getUserAlerts().setValue(currentAlerts);

        if (image != null){
            try {
                new PhotoUtils.SendImageToCloudStorage(PhotoUtils.rotateIfRequired(image, mCurrentPhotoPath), alert).upload();
            } catch (IOException ioe){

            }

        }

        // save new wrapper ton phone disk
        FileLocalStorage<AlertWrapper> wrapperStorage =
                new FileLocalStorage<>(ALERT_WRAPPER_FILENAME, new AlertWrapper(), this);
        try {
            AlertWrapper wrapper = wrapperStorage.read();
            wrapper.setUser(currentAlerts.toArray(new UserAlert[currentAlerts.size()]));
            wrapperStorage.setData(wrapper);
            wrapperStorage.write();
        } catch (JsonGenerationException jsonError) {
            Log.e("STORAGE", "could not store alertWrapper : " + jsonError.getMessage());

        } catch (IOException ioe) {
            Log.e("STORAGE", "could not store alertWrapper : " + ioe.getMessage());
        }


        if (wait != null){
            wait.dismiss();
        }
        onBackPressed();

    }

    private void onRequestFailure(RequestErrorException error){
        Toast.makeText(this,
                getString(R.string.on_post_alert_falure), Toast.LENGTH_SHORT).show();
        Log.e(SERVER_HANDLE, error.prettyPrinter());
        if (wait != null){
            wait.dismiss();
        }
        onBackPressed();
    }

    private void onCancelBtnPressed(View view){
        onBackPressed();
    }

    private void onPhotoBtnPressed(View view){
        photoHelper.dispatchTakePictureIntent();
    }




    /**
     * Activity result from taken picture
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        mCurrentPhotoPath = photoHelper.getCurrentPhotoPath();
        Log.i("_photo", "onActivityResult. Path = " + mCurrentPhotoPath);
        photoHelper.setNewPhoto(mCurrentPhotoPath);

    }

}
