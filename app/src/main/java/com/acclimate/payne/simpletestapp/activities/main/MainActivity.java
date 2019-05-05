package com.acclimate.payne.simpletestapp.activities.main;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SeekBar;

import com.acclimate.payne.simpletestapp.R;
import com.acclimate.payne.simpletestapp.activities.IDisplayInformation;
import com.acclimate.payne.simpletestapp.alerts.BasicAlert;
import com.acclimate.payne.simpletestapp.alerts.UserAlert;
import com.acclimate.payne.simpletestapp.alerts.alertViewModel.AlertViewModel;
import com.acclimate.payne.simpletestapp.alerts.alertViewModel.AlertViewModelSingletonFactory;
import com.acclimate.payne.simpletestapp.animator.MyAnimator;
import com.acclimate.payne.simpletestapp.appUtils.App;
import com.acclimate.payne.simpletestapp.appUtils.AppTag;
import com.acclimate.payne.simpletestapp.backgroundTask.WifiReceiver;
import com.acclimate.payne.simpletestapp.map.MapDisplay;
import com.acclimate.payne.simpletestapp.map.pins.BasicPin;
import com.acclimate.payne.simpletestapp.map.pins.PinBundle;
import com.acclimate.payne.simpletestapp.map.pins.PinController;
import com.acclimate.payne.simpletestapp.map.pins.UserPin;
import com.acclimate.payne.simpletestapp.monitoredZones.MZController;
import com.acclimate.payne.simpletestapp.monitoredZones.MonitoredZone;
import com.acclimate.payne.simpletestapp.monitoredZones.ZoneCircle;
import com.acclimate.payne.simpletestapp.notifications.MyFirebaseMessagingService;
import com.acclimate.payne.simpletestapp.server.Server;
import com.acclimate.payne.simpletestapp.server.requests.HttpRequest;
import com.acclimate.payne.simpletestapp.server.requests.PatchRequest;
import com.acclimate.payne.simpletestapp.server.requests.RequestHandler;
import com.acclimate.payne.simpletestapp.user.User;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.infowindow.InfoWindow;

import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

import lombok.Setter;
import me.toptas.fancyshowcase.FancyShowCaseView;

import lombok.Getter;

import static com.acclimate.payne.simpletestapp.appUtils.App.vLOLLIPOP;
import static com.acclimate.payne.simpletestapp.appUtils.AppTag.CENTER_LAT;
import static com.acclimate.payne.simpletestapp.appUtils.AppTag.CENTER_LNG;
import static com.acclimate.payne.simpletestapp.appUtils.AppTag.MAIN_IS_FIRST_RUN;
import static com.acclimate.payne.simpletestapp.appUtils.AppTag.ZOOM_LVL;
import static com.acclimate.payne.simpletestapp.backgroundTask.BackgroundRequestWorker.backgroundRequest;


/**
 * Classe de la carte.
 */
public final class MainActivity extends AppCompatActivity {

    // positionnement initial au lancement de la carte
    public static double[] init_center_pos = new double[2];


    public MapView mapView;
    @Getter private MapEventsOverlay mapEventsOverlay;

    // todo : find a way to remove static and replace by mpDisplay
    public static MapDisplay staticMapDisplay;

    @Getter private MapDisplay mapDisplay;
    @Getter public  MyAnimator myAnimator;
    private boolean firstRun = true;
    @Getter private Toolbar toolbar;
    @Setter private boolean needRedraw = false;

    @Getter private Toolbar.OnMenuItemClickListener menuItemClickListener;
    @Getter private MapEventsReceiver eventReceiver;
    @Getter private SharedPreferences.OnSharedPreferenceChangeListener preferenceChangeListener;
    @Getter private IDisplayInformation mainSnackbarDisplayer;
    @Getter private HistoricalPinUpdater historicalPinUpdater;
    private BackgroundTaskPrefChangeListener backgroundTaskPrefChangeListener;

    private WifiReceiver wifiReceiver;


    private boolean isReceiverRegistered = false;







// ===========================================================================
// Android Lifecycle methods


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Log.i(APP_FLOW, "\n =========== \nmain onCreate");

        final Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
            getWindow().setAllowEnterTransitionOverlap(true);
            getWindow().setAllowReturnTransitionOverlap(true);
        }

        //-----------------------------------------------------------------------
        // On create init
        App.getInstance().setActivityInstance(this);
        myAnimator = new MyAnimator(this);
        // is first run ?

        firstRun = savedInstanceState == null ||
                savedInstanceState.getBoolean(MAIN_IS_FIRST_RUN, true);


        // -----------------------------------------------------------------------
        // Loading Preferences stored in the phone (or initializing them)
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);






        // -----------------------------------------------------------------------
        // Fetch AlertViewModel and setup Listener
        AlertViewModel alertModel = ViewModelProviders
                .of(this, AlertViewModelSingletonFactory.getInstance())
                .get(AlertViewModel.class);

        alertModel.getLiveAlerts().observe(this, this::redrawLivePin);
        alertModel.getUserAlerts().observe(this, this::redrawUserPin);
        alertModel.getHistAlerts().observe(this, this::redrawHistPin);




        // -----------------------------------------------------------------------
        // setup map display

        // Inflate and create the mapView
        setContentView(R.layout.activity_main);



        // maps event
        if (vLOLLIPOP) {
            mapView = findViewById(R.id.map_v21);
        } else {
            mapView = findViewById(R.id.map);
        }
        eventReceiver = new MainMapEventReceiver(this, mapView);
        mapEventsOverlay = new MapEventsOverlay(eventReceiver);
        mapView.getOverlays().add(0, mapEventsOverlay);

        // MinLatitude = -85.05112877980659;
        // MaxLatitude = 85.05112877980659;
        // makes map more friendly user
        mapView.setVerticalMapRepetitionEnabled(false);
        mapView.setScrollableAreaLimitLatitude(85.0, -85.0, 0);
        mapView.setMinZoomLevel(3D);

        mapView.setTileSource(TileSourceFactory.MAPNIK);

        // set zoom control and multi-touch gesture
        mapView.setBuiltInZoomControls(false);
        mapView.setMultiTouchControls(true);


        // default initial centered position
        init_center_pos[0] = (double) sharedPreferences.getFloat(AppTag.LAST_KNOWN_LOC_LAT, 45.5161F);
        init_center_pos[1] = (double) sharedPreferences.getFloat(AppTag.LAST_KNOWN_LOC_LNG, -73.6568F);

        if (firstRun) {
            // default initial position
            recenterMap(init_center_pos[0], init_center_pos[1], 7.0);
        } else {
            recenterMap(savedInstanceState.getDouble(CENTER_LAT, init_center_pos[0]),
                    savedInstanceState.getDouble(CENTER_LNG, init_center_pos[1]),
                    savedInstanceState.getDouble(ZOOM_LVL, 7));
        }


        mapDisplay = new MapDisplay(mapView, this);
        staticMapDisplay = mapDisplay;


        mapView.setOnDragListener( (view, event) -> {
//            Log.i(CACHE, event.toString());
            return true;
        });


        // -----------------------------------------------------------------------
        // Populating the Toolbar

        toolbar = findViewById(R.id.main_app_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        // Logo button
        findViewById(R.id.logo).setOnClickListener(view -> {
            // todo : setup app tutorial
//            Toast.makeText(this, mapView.getZoomLevelDouble()+"", Toast.LENGTH_SHORT).show();
            User user = App.getInstance().getCurrentUser();
            if (user != null && user.getPoints() < 5){
                HttpRequest<Map> karmaUp = PatchRequest.karma(user.getPoints() + 5, user.getuId());
                RequestHandler handler = new RequestHandler<>(karmaUp,
                        response -> {
                            Snackbar.make(view, "Merci d'utiliser Acclimate :) +5 points!", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                            user.getKarma().setPoints(user.getPoints() + 5);
                        },
                        error -> {
                            Snackbar.make(view, "Merci d'utiliser Acclimate :)", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        });
                handler.handle(Server.AuthorizationHeaders.REQUIRED);
            } else {
                Snackbar.make(view, "Merci d'utiliser Acclimate :)", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        menuItemClickListener = new MainMenuItemListener(this);
        toolbar.setOnMenuItemClickListener(menuItemClickListener);



        // setup Firebase and permissions
        MyFirebaseMessagingService.setUpRegistrationToken();


        // -----------------------------------------------------------------------
        // display infos from other classes
        mainSnackbarDisplayer = new MainSnackbarDisplayer(this);



        // -----------------------------------------------------------------------
        // debug and test

        historicalPinUpdater = new HistoricalPinUpdater(alertModel,
                sharedPreferences.getBoolean(AppTag.ALERT_HISTO_FILTER, PinController.PinPreferences.historiqueFilter));
        mapView.addMapListener(historicalPinUpdater.defaultListener());


        // -----------------------------------------------------------------------
        // listen to pref changes
        backgroundTaskPrefChangeListener = new BackgroundTaskPrefChangeListener(this);
        preferenceChangeListener = new MainSharedPreferenceListener(this);
        PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                .registerOnSharedPreferenceChangeListener(preferenceChangeListener);
        PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                .registerOnSharedPreferenceChangeListener(historicalPinUpdater);
        PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                .registerOnSharedPreferenceChangeListener(mapDisplay.getPinController());
        PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                .registerOnSharedPreferenceChangeListener(backgroundTaskPrefChangeListener);


        // --------------------------------
        // update background on wifi change
        wifiReceiver = new WifiReceiver(backgroundRequest);
        wifiReceiver.setCtx(this);
        getApplicationContext().registerReceiver(wifiReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        // redraw map screen
        alertModel.forceUpdate(); // <-- when this is commented, basic pins display correctly


    }



    @Override
    public void onStart() {
        super.onStart();
//        Log.i(APP_FLOW, "main onStart");

        getSupportActionBar().setTitle("");
    }




    @Override
    public void onResume() {
        super.onResume();
//        Log.i(APP_FLOW, "main onResume");

        if(needRedraw) {
            needRedraw = false;
            mapDisplay.getZoneController().redrawAllZones();
        }

        InfoWindow.closeAllInfoWindowsOn(mapView);
        mapView.onResume(); //needed for compass, my location overlays, v6.0.0 and up


        AlertViewModel alertModel = ViewModelProviders
                .of(this, AlertViewModelSingletonFactory.getInstance())
                .get(AlertViewModel.class);


/*
        List<BasicAlert> liveAlertList = alertModel.getLiveAlerts().getValue();
        LinkedBlockingQueue<BasicAlert> histAlertList = alertModel.getHistAlerts().getValue();
        List<UserAlert> userALertList = alertModel.getUserAlerts().getValue();

        BasicAlert[] currentLiveAlerts = liveAlertList.toArray(new BasicAlert[liveAlertList.size()]);
        BasicAlert[] currentHistAlerts = histAlertList.toArray(new BasicAlert[histAlertList.size()]);
        UserAlert[] currentUserAlerts = userALertList.toArray(new UserAlert[userALertList.size()]);

        AlertWrapper wrapper = new AlertWrapper();
        wrapper.setLive(currentLiveAlerts);
        wrapper.setUser(currentUserAlerts);
        wrapper.setHisto(currentHistAlerts);

        mapDisplay.setupDisplay(wrapper, this);
*/


        // return to saved position

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));

        BoundingBox box = new BoundingBox(
                sp.getFloat(AppTag.CURRENT_POS_BOX_NORTH,45.5161f),
                sp.getFloat(AppTag.CURRENT_POS_BOX_EAST, -72.6568f),
                sp.getFloat(AppTag.CURRENT_POS_BOX_SOUTH,44.5161f),
                sp.getFloat(AppTag.CURRENT_POS_BOX_WEST, -73.6568f));

        GeoPoint center = new GeoPoint(
                sp.getFloat(AppTag.CURRENT_POS_CENTER_LAT, sp.getFloat(AppTag.LAST_KNOWN_LOC_LAT, 45.5161F)),
                sp.getFloat(AppTag.CURRENT_POS_CENTER_LNG, sp.getFloat(AppTag.LAST_KNOWN_LOC_LNG, -73.6568F))
        );
//        Log.i(APP_FLOW, "fetched center = " + center.toDoubleString());
        // Log.i(APP_FLOW, ((GeoPoint) mapView.getMapCenter()).toDoubleString());

        mapView.setExpectedCenter(center);
        // mapView.getController().zoomTo(sp.getFloat(AppTag.CURRENT_POS_ZOOM_LVL, 17));
        // mapView.getController().setCenter(center);

    }






    @Override
    public void onPause() {
        super.onPause();
//        Log.i(APP_FLOW, "main onPause");
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        mapView.onPause();  //needed for compass, my location overlays, v6.0.0 and up

        Configuration.getInstance().save(this, prefs);

        // save current view position
        BoundingBox currentPos = mapView.getBoundingBox();
        GeoPoint currentCenter = (GeoPoint) mapView.getMapCenter();
//        Log.i(APP_FLOW, "saved center = " + currentCenter.toDoubleString());
        prefs.edit()
                .putFloat(AppTag.CURRENT_POS_BOX_NORTH, Double.valueOf(currentPos.getLatNorth()).floatValue())
                .putFloat(AppTag.CURRENT_POS_BOX_EAST,  Double.valueOf(currentPos.getLonEast()).floatValue())
                .putFloat(AppTag.CURRENT_POS_BOX_SOUTH, Double.valueOf(currentPos.getLatSouth()).floatValue())
                .putFloat(AppTag.CURRENT_POS_BOX_WEST,  Double.valueOf(currentPos.getLonWest()).floatValue())
                .putFloat(AppTag.CURRENT_POS_CENTER_LAT,Double.valueOf(currentCenter.getLatitude()).floatValue())
                .putFloat(AppTag.CURRENT_POS_CENTER_LNG,Double.valueOf(currentCenter.getLongitude()).floatValue())
                .putFloat(AppTag.CURRENT_POS_ZOOM_LVL, Double.valueOf(mapView.getZoomLevelDouble()).floatValue())
                .apply();


    }


    /**
     * SAVE HERE CONFIGURATION YOU WANT TO SURVIVE ROTATION CHANGE
     * @param outState
     */
    @Override
    public void onSaveInstanceState(Bundle outState){
//        Log.i(APP_FLOW, "main onSavedInstanceState");

        // -------------------------------------------
        // save current boundingBox
        IGeoPoint center = mapView.getMapCenter();
        outState.putDouble(CENTER_LNG , center.getLongitude());
        outState.putDouble(CENTER_LAT , center.getLatitude());
        outState.putDouble(ZOOM_LVL, mapView.getZoomLevelDouble());

        // -------------------------------------------
        // save if main activity is launched for the first time when app run
        outState.putBoolean(MAIN_IS_FIRST_RUN, false);


        // -------------------------------------------
        // save current pin display options... already saved in PinController ?

        super.onSaveInstanceState(outState);

    }









// ===========================================================================
// Display and menu methods


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        //Setting up Search bar
        MenuItem ourSearchItem = menu.findItem(R.id.action_search);
        SearchView sv = (SearchView) ourSearchItem.getActionView();

        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mapDisplay.centerOnGoogleQuery(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return true;
    }



    /**
     * Used to prompt the user for a name for his new MonitoredZone
     *
     * @param mz
     */
    public void promptName(MonitoredZone mz) {
        // Prompt for the name of the MZ
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Veuillez nommer votre zone");

        // Set up the input
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // Set up the buttons
        builder.setNegativeButton("Annuler", (dialog, which) -> dialog.cancel());
        builder.setPositiveButton("OK", (dialog, which) -> {

            mz.setZoneId(null);
            mz.setUser(App.getInstance().getCurrentUser());
            mz.setDateCreation(Server.getCurrentTimeFormatted());
            mz.setName(input.getText().toString()); // allows duplicated names...

            mapDisplay.getZoneController().createPostSaveDisplayZone(mz);
        });

        // Dialog
        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(d -> {
            InputMethodManager inM = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inM.showSoftInput(input, InputMethodManager.SHOW_IMPLICIT);
        });
        dialog.show();
    }


    /**
     * Used to prompt the user about the desired radius' length in meters.
     * Provides a slider which shows a Blue preview of the resulting circle.
     *
     * @param mz
     */
    public void promptRadius(MonitoredZone mz) {

        MZController mzController = mapDisplay.getZoneController();

        // Setting up the PopupWindow (inflating its layout to set as View)
        PopupWindow popupWindow = new PopupWindow(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        LayoutInflater inflater = (LayoutInflater) MainActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View sliderPreviewerLayout = inflater.inflate(R.layout.circle_preview_slider, null);
        popupWindow.setContentView(sliderPreviewerLayout);
        popupWindow.setAnimationStyle(R.style.PopupWindowAnimation);
        popupWindow.showAtLocation(findViewById(R.id.main_app_toolbar), Gravity.TOP, 0, 0);

        EditText metersAmnt = sliderPreviewerLayout.findViewById(R.id.circle_mz_amnt_meters);

        // Settings up the SeekBar (slider)
        int defaultInitSliderValue = 5000;
        int maxSliderValue = 40000;
        int multiplier = 7;
        int[] radiusInMeters = new int[1];

        // Generating and displaying the temporary DisplayZone
        int initialRadius = defaultInitSliderValue * multiplier;
        ZoneCircle oldZone[] = new ZoneCircle[1];
        oldZone[0] = generateDummyZone(mzController, mz, initialRadius);
        mzController.refreshOneZone(null, oldZone[0]);

        // Initializing the dummy DisplayZone
        ZoneCircle newZone[] = new ZoneCircle[1];
        newZone[0] = null;


        Snackbar.make(mapView, "Sélectionnez le rayon de votre zone.", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();

        SeekBar seekBar = sliderPreviewerLayout.findViewById(R.id.circle_mz_seekBar);
        seekBar.setMax(maxSliderValue); // Max must be set before Progress
        seekBar.setProgress(defaultInitSliderValue);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress == 0) progress = 1; // min = 1

                radiusInMeters[0] = progress * multiplier;
                metersAmnt.setText(radiusInMeters[0] + "");

                // Generate a new DisplayZone and redraw
                newZone[0] = generateDummyZone(mzController, mz, radiusInMeters[0]);
                mzController.refreshOneZone(oldZone[0], newZone[0]);
                oldZone[0] = newZone[0];
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });

        // Setting up the EditText TODO: dynamically update progress of SeekBar based on number (SHOW KEYBOARD)
        metersAmnt.setText(initialRadius + "");
        metersAmnt.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                // to find the new text: s.toString()
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
        });
//        to show the keyboard
//        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
//        imm.hideSoftInputFromWindow(view.getWindowToken(),0);



        // Setting up the Buttons (Confirm/Cancel)
        ImageButton confirmBtn = sliderPreviewerLayout.findViewById(R.id.circle_mz_confirm);
        ImageButton cancelBtn = sliderPreviewerLayout.findViewById(R.id.circle_mz_cancel);

        confirmBtn.setOnClickListener(v -> {
            mz.setRadius(radiusInMeters[0]);
            mzController.redrawAllZonesButOne(oldZone[0]);
            popupWindow.dismiss();
            promptName(mz);
        });
        cancelBtn.setOnClickListener(v -> {
            mzController.redrawAllZonesButOne(oldZone[0]);
            popupWindow.dismiss();
        });
    }

    /**
     * Returns a dummy DisplayZone to be displayed temporarily.
     *
     * @param mzController
     * @param monitoredZone will see its radius modified
     * @param radius in meters
     * @return
     */
    private ZoneCircle generateDummyZone(MZController mzController, MonitoredZone monitoredZone, int radius) {

        monitoredZone.setRadius(radius);
        ZoneCircle circleDisplay = (ZoneCircle) mzController.generateDisplayZone(monitoredZone);
        circleDisplay.setTemporaryColor();

        return circleDisplay;
    }









// ===========================================================================
// Helper methods



    /**
     * Pour centrer la map aux coordonnées données.
     * Plus le zoom est un grand nombre, plus on est zoomed in.
     *
     * @param lat
     * @param lon
     * @param zoom
     */
    private void recenterMap(double lat, double lon, double zoom) {
        final IMapController mapController = mapView.getController();
        final GeoPoint startPoint = new GeoPoint(lat, lon);

        mapController.setCenter(startPoint);
        mapController.setZoom(zoom);
    }


    // Listeners for ModelView modification. Updates the UI with new pin whne
    // the contents of alerts changes.

    private void redrawLivePin(List<BasicAlert> liveAlerts){
        PinBundle<BasicPin> livePinBundle = mapDisplay.updateLivePin(liveAlerts);
        mapView.getOverlayManager().addAll(livePinBundle.toAdd());
        mapView.getOverlayManager().removeAll(livePinBundle.toRemove());
    }


    private void redrawUserPin(List<UserAlert> userAlerts){
        PinBundle<UserPin> userPinBundle = mapDisplay.updateUserPin(userAlerts);
        mapView.getOverlayManager().addAll(userPinBundle.toAdd());
        mapView.getOverlayManager().removeAll(userPinBundle.toRemove());
    }


    private void redrawHistPin(LinkedBlockingQueue<BasicAlert> histoAlerts){

        mapDisplay.forceRedrawHisto(histoAlerts);

        // PinBundle<BasicPin> histoPinBundle = mapDisplay.updateHistoPin(histoAlerts);
        // mapView.getOverlayManager().addAll(histoPinBundle.toAdd());
        // mapView.getOverlayManager().removeAll(histoPinBundle.toRemove());
    }


    public void focusOnUserIdentification(){

        new FancyShowCaseView.Builder(this)
                .focusOn(findViewById(R.id.profileBtn))
                .focusCircleRadiusFactor(0.8)
                .title("Vous devez être authentifié")
                .titleStyle(R.style.MyTitleStyle, Gravity.CENTER)
                .build()
                .show();

        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(130);

        myAnimator.fastShakingAnimation(findViewById(R.id.profileBtn));

    }


    @Override
    public void onBackPressed() {
        if (InfoWindow.getOpenedInfoWindowsOn(mapView).size() > 0) {
            InfoWindow.closeAllInfoWindowsOn(mapView);
        } else {
            super.onBackPressed();
        }
    }


}




