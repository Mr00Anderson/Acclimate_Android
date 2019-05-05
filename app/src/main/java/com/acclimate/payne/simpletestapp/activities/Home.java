package com.acclimate.payne.simpletestapp.activities;

import android.Manifest;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.acclimate.payne.simpletestapp.R;
import com.acclimate.payne.simpletestapp.activities.main.MainActivityIntent;
import com.acclimate.payne.simpletestapp.alerts.AlertController;
import com.acclimate.payne.simpletestapp.alerts.AlertWrapper;
import com.acclimate.payne.simpletestapp.alerts.IAcclimateAlertFetch;
import com.acclimate.payne.simpletestapp.alerts.alertViewModel.AlertViewModel;
import com.acclimate.payne.simpletestapp.alerts.alertViewModel.AlertViewModelSingletonFactory;
import com.acclimate.payne.simpletestapp.appUtils.App;
import com.acclimate.payne.simpletestapp.appUtils.AppConfig;
import com.acclimate.payne.simpletestapp.appUtils.AppTag;
import com.acclimate.payne.simpletestapp.authentification.AuthUIActivity;
import com.acclimate.payne.simpletestapp.deviceStorage.localStorage.FileLocalStorage;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;

import lombok.Getter;
import lombok.Setter;

import static com.acclimate.payne.simpletestapp.appUtils.AppConfig.ALERT_WRAPPER_FILENAME;
import static com.acclimate.payne.simpletestapp.appUtils.AppConfig.TEST_HOME;


/**
 * Classe de la Page d'Acceuil. C'est la fenêtre qui s'ouvre au lancement de l'application.
 */
public class Home extends AppCompatActivity implements IAcclimateAlertFetch {

    private final int REQUEST_CODE_ACCESS_FINE_LOCATION = 123;
    @Getter @Setter
    private int delayCounter;
    private FileLocalStorage<AlertWrapper> wrapperStorage;
    private AlertWrapper wrapper = new AlertWrapper();
    private AlertViewModel alertViewModel;
    private SharedPreferences sharedPreferences;

    private FusedLocationProviderClient mFusedLocationClient;

    private boolean isReceiverRegistered = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // todo: integrate "FirstRun" (https://developer.android.com/training/tv/playback/onboarding)

        // See comment on last line of this Class
        if (TEST_HOME) runAsynchronousTestSuite();
//        if (GENERAL_DEBUG_MODE) Log.i(SERVER, InternetCheck.synchronous(getApplicationContext()) + "");

        App.getInstance().setActivityInstance(this);
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        int prefNetworkListRefreshRate = Integer.parseInt(sharedPreferences.getString(AppTag.NETWORK_REFRESH_RATE, "3"));
        AppConfig.REQUEST_TIME_TRESHOLD = prefNetworkListRefreshRate * 60L * 1000L;


        // Updates UI when request is received
        alertViewModel = ViewModelProviders
                .of(this, AlertViewModelSingletonFactory.getInstance())
                .get(AlertViewModel.class);

//        Log.i(VIEWMODEL, (alertViewModel.getLiveAlerts() == null) + "");

         alertViewModel.getLiveAlerts().observe(this, liveAlerts -> {

             final String officials = " alertes actives";

             ((TextView) findViewById(R.id.dlb_eauMain)).setText(wrapper.getEau() + officials);
             ((TextView) findViewById(R.id.dlb_feuMain)).setText(wrapper.getFeu() + officials);
             ((TextView) findViewById(R.id.dlb_meteoMain)).setText(wrapper.getMeteo() + officials);
             ((TextView) findViewById(R.id.dlb_terrainMain)).setText(wrapper.getTerrain() + officials);

         });

         alertViewModel.getUserAlerts().observe(this, userAlerts -> {

             final String usagers = " saisies d'USAGERS";

             ((TextView) findViewById(R.id.dlb_eauSec)).setText("+" + wrapper.getEauU() + usagers);
             ((TextView) findViewById(R.id.dlb_feuSec)).setText("+" + wrapper.getFeuU() + usagers);
             ((TextView) findViewById(R.id.dlb_meteoSec)).setText("+" + wrapper.getMeteoU() + usagers);
             ((TextView) findViewById(R.id.dlb_terrainSec)).setText("+" + wrapper.getTerrainU() + usagers);

         });
        wrapperStorage = new FileLocalStorage<>(ALERT_WRAPPER_FILENAME, wrapper, this);

        // home = this;
        // ready = false;

        setContentView(R.layout.home);

        boolean gpsPrefActivated = sharedPreferences.getBoolean(AppTag.GPS_ACTIVATED, true);
        if(gpsPrefActivated) setUpGPS();        // Pour aller chercher la localisation de l'usager
        setUpBoutonsMap();                      // Les listeners sur les boutons qui mènent à "MainActivity"
        AuthUIActivity.setUpAuthSyncUp(this);   // Initialisation de l'authentification et synchro du phone


        /*
        First-time users: privacy policy alert dialog (Google Play compliance).
         */
        boolean showPrivacyNotice = sharedPreferences.getBoolean(AppTag.FIRST_TIME_USER, true);
        if(showPrivacyNotice) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.pp_title)
                    .setMessage(R.string.pp_text)
                    .setPositiveButton(getResources().getString(android.R.string.ok),
                            (dialog, which) ->
                                sharedPreferences.edit()
                                    .putBoolean(AppTag.FIRST_TIME_USER, false)
                                    .apply()
                    );
            builder.setCancelable(false);
            AlertDialog privacyPolicy = builder.create();
            privacyPolicy.show();
        }

    }


    @Override
    protected void onPause() {
        super.onPause();

    }

    /**
     * À chaque retour sur le Home Page, on recalcule le nombre d'alertes.
     */
    public void onResume() {
        super.onResume();

        initButtonText();

        Integer[] views = new Integer[]{
                R.id.dlb_eauMain,R.id.dlb_eauSec,
                R.id.dlb_feuMain,R.id.dlb_feuSec,
                R.id.dlb_terrainMain,R.id.dlb_terrainSec,
                R.id.dlb_meteoMain,R.id.dlb_meteoSec};

        for(int i : views)
            animate(i);

        beginFetchAlert();

    }

    @Override
    public void beginFetchAlert(){

        AlertController.FetchBasicAlert fetchTask = new AlertController.FetchBasicAlert();
        fetchTask.begin(this);

    }




/*
    [ GPS RELATED ]
 */

    /**
     * Goal of the GPS set up this early is to try to obtain the localization of the use before
     * MainActivity is entered so that we can center the map there instead of the default (Montreal)
     */
    private void setUpGPS() {

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (!permissionsGranted()) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_ACCESS_FINE_LOCATION);
        } else {
            updateLastKnownLocation();
        }
    }

    private Boolean permissionsGranted() {
        return (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED);
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE_ACCESS_FINE_LOCATION) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // User accepted permission request

                updateLastKnownLocation();

            } else {
                // User refused to grant permission. You can add AlertDialog here
                Toast.makeText(this, "Vous n'avez pas donné la permission d'accéder à la localisation de votre appareil", Toast.LENGTH_LONG).show();
                startInstalledAppDetailsActivity();
            }
        }
    }

    /**
     * Updated the internal memory with the last known GPS Location
     */
    private void updateLastKnownLocation() {
        try {
            mFusedLocationClient.getLastLocation().addOnSuccessListener(this,
                    location -> {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle location object
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();

                            sharedPreferences.edit()
                                    .putFloat(AppTag.LAST_KNOWN_LOC_LAT, (float) latitude)
                                    .putFloat(AppTag.LAST_KNOWN_LOC_LNG, (float) longitude)
                                    .apply();
                        }
                    }
            );
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void startInstalledAppDetailsActivity() {
        Intent i = new Intent();
        i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        i.addCategory(Intent.CATEGORY_DEFAULT);
        i.setData(Uri.parse("package:" + getPackageName()));
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }

/*
    [ FIN DU GPS ]
 */



    /**
     * Afin de permettre à l'usager de faire la transition entre Home et MainActivity
     */
    private void setUpBoutonsMap() {
        View gotoMain = findViewById(R.id.goto_main);
        gotoMain.findViewById(R.id.proceed).setOnClickListener(view -> launchMain());
        gotoMain.findViewById(R.id.textView).setOnClickListener(view -> launchMain());
    }

    /**
     * Tout ce qui doit être fait pour passer à la MainActivity
     */
    private void launchMain() {
        MainActivityIntent intent = new MainActivityIntent(getApplicationContext());
        intent.fromHome(true);
        startActivity(intent);
    }

    /**
     * Pour animer certains éléments visuels.
     * Permet de ne pas simplement afficher les informations incomplètes qui n'ont
     * pas encore été retournées par la requête faites au serveur.
     *
     * Exemples de cette librairie:
     * https://github.com/daimajia/AndroidViewAnimations
     *
     * @param i Doit être de la forme "R.id.quelqueChose"
     */
    public void animate(final int i) {

        // Setting up slight delay for secondary information
        long tmp = 300;
        if(delayCounter % 2 == 1)
            tmp += 450;

        final long delay = tmp;
        final TextView txtView = findViewById(i);
        txtView.setVisibility(View.GONE);

        new Thread( () -> {

            try {

                Thread.sleep(delay);

                runOnUiThread( () -> {
                        txtView.setVisibility(View.VISIBLE);
                        YoYo.with(Techniques.FadeInRight)
                                .duration(1500)
                                .repeat(0) // 1 fois
                                .playOn(txtView);
                });
            } catch (Exception e) {
                e.printStackTrace();
            }

            }).start();

        delayCounter++;

    }




    @Override // Overriden from AcclimateFetchAlert interface
    public Context getContext(){
        return getApplicationContext();
    }


    @Override
    public void onFetchedAlert(AlertWrapper wrapper, boolean completed){

        wrapper.initAlertAmount();
        this.wrapper = wrapper;

        alertViewModel.setValues(wrapper);

        try {

            if (wrapper.isOld()){
                wrapper.setLastSavedInstanceTimestamp();
                wrapperStorage.setData(wrapper);
                wrapperStorage.write();
//                Log.i("STORAGE", "Alert wrapper write to internal storage with timestamp = " + wrapper.getLastRequestTime());
            }

        } catch (JsonGenerationException jsonError) {
            ;
//            Log.e("STORAGE", "could not store alertWrapper : " + jsonError.getMessage());

        } catch (IOException ioe) {
            ;
//            Log.e("STORAGE", "could not store alertWrapper : " + ioe.getMessage());
        }

    }



    /**
     * Sets TextView of the number of alerts to the default value
     *
     */
    private void initButtonText(){

        ((TextView) findViewById(R.id.dlb_eauMain)).setText(R.string.defaul_alert_amount_value);
        ((TextView) findViewById(R.id.dlb_feuMain)).setText(R.string.defaul_alert_amount_value);
        ((TextView) findViewById(R.id.dlb_meteoMain)).setText(R.string.defaul_alert_amount_value);
        ((TextView) findViewById(R.id.dlb_terrainMain)).setText(R.string.defaul_alert_amount_value);

        // Texte lié alertes d'USAGERS
        ((TextView) findViewById(R.id.dlb_eauSec)).setText(R.string.defaul_alert_user_amount_value);
        ((TextView) findViewById(R.id.dlb_feuSec)).setText(R.string.defaul_alert_user_amount_value);
        ((TextView) findViewById(R.id.dlb_meteoSec)).setText(R.string.defaul_alert_user_amount_value);
        ((TextView) findViewById(R.id.dlb_terrainSec)).setText(R.string.defaul_alert_user_amount_value);

    }


    /* *******
    TEST SUITE
    ******* */

    /**
     * To disable : set {@link AppConfig#TEST_HOME} to false.
     */
    private void runAsynchronousTestSuite() {


        /* ********************************************************************************************
        ===============================================================================================

                                                  WARNING !!!
                         TEST SUITE IN APP ACTIVITY FOR ASYNCHRONOUS OPERATIONS!

        The nature of asynchronous methods is so that testing in jUnit is very complicates, almost
        impossible. The Android framework has strict procedure about Threads and the way it uses them.
        As such, to ensure proper testing of asynchronous features, those test are run at the launch
        of the app. To disable those test (tests are mostly ever requests, so they WILL use data from
        cell phone plan if the phone is not connected to the internet) set TEST_HOME to false in AppConfig.


        ===============================================================================================
        ******************************************************************************************** */

        // ------------------------------------------------------------------------------------------
        // new requests


/*
        User _dummy = TestObjectsInstances.getInstance().user;

        HttpRequest<UserAlert[]> userAlerts = GetRequest.alert(AlertRequestType.USER);
        new RequestHandler<>(userAlerts,

                response -> {
                    for (UserAlert alert : response){
                        Log.i(SERVER_HANDLE, alert.getUser().getuId());
                    };
                },

                error -> {
                    Log.i(SERVER_HANDLE, "error callback " + (error == null ? "null" : error.prettyPrinter()));
                }).handle();


        MonitoredZone toPost = TestObjectsInstances.getInstance().zone;
        HttpRequest<MonitoredZone> postZone = PostRequest.zone(toPost);

        new RequestHandler<>(postZone,

                response -> {
                    Log.i(SERVER_HANDLE, "monitoredZone response = " + response.toString());
                },

                error -> {
                    Log.e(SERVER_HANDLE, (error == null ? "null" : error.prettyPrinter()));
                }).handle();


        final String user_to_patch = "NjUWtYELQ0RbhUAREAQ1TLueQB03";

        HttpRequest<User> getUserById = GetRequest.user(user_to_patch);
        new RequestHandler<>(getUserById, this::patchUser,
                error -> {
                    Log.w("patchRegTokAndSyncUser","failure on Oli's Get User test");
                }
        ).handle();




        //////////////////////////////////////////////////////////////////////
        //                      testing request auth                        //

        UserController.getUserJWT(task -> {

            if (task.isSuccessful()) {
                String jwtToken = task.getResult().getToken();
                Log.i("testAuth", "JWT SUCCESS : " + jwtToken);
                sendRequest(jwtToken);
            } else {
                // Handle error -> task.getException();
                Log.e("testAuth", "FAILURE := idToken (OAuth 2) : " +
                        task.getException().getMessage());
                task.getException().printStackTrace();
            }
        });


        HttpRequest<User[]> getAllUsersRequest = GetRequest.user();
        RequestHandler<User[]> handler = new RequestHandler<>(getAllUsersRequest,
                response -> {
                    Log.i(TEST_AUTH, "Success all users : " + getAllUsersRequest.getRequestEntity().getHeaders().toString());
                },
                error -> {
                    Log.e(TEST_AUTH, "Error request : " + error.getRequest().getUrl());
                    Log.e(TEST_AUTH, "Error all users headers : " + error.getRequest().getRequestEntity().getHeaders().toString());
                    Log.e(TEST_AUTH, "Error message : " + error.prettyPrinter());
                });

        handler.handle(Server.AuthorizationHeaders.REQUIRED);
*/


    }


/*
    private void sendRequest(String jwt){
        Log.i("testAuth", "Before send request ");
        HttpRequest<User[]> getAllUsersRequest = GetRequest.user();
        getAllUsersRequest.setAuth(jwt);
        Log.i(AppTag.AUTH_HEADERS, jwt);

        RequestHandler<User[]> handler = new RequestHandler<>(getAllUsersRequest,
                response -> {

                    for (User user : response){
                        Log.i("testAuth", user.toString());
                    }


                    Log.i("testAuth", getAllUsersRequest.getRequestEntity().getHeaders().toString());

                },
                error -> {
                    Log.e("testAuth", error.getRequest().getRequestEntity().getHeaders().toString());
                });
        handler.handle(Server.AuthorizationHeaders.REQUIRED);

    }

    private void patchUser(User user) {

        // Pour obtenir le 'registrationToken' de l'appareil actuel
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w("registrationToken", "getInstanceId failed", task.getException());
                        return;
                    }

                    // Ici on le récupère
                    String regToken = task.getResult().getToken();
                    Log.w("patchRegTokAndSyncUser", regToken);


                    // [ Contenu de l'ancien "patchUser" de Oli ]

                    // Et maintenant c'est comme ton test d'avant, mais avec un VRAI regToken
                    Map<String, Object> body2 = new HashMap<>();
                    List<String> tokens = user.getRegistrationToken();
                    tokens.add(regToken);
                    body2.put("patchRegTokAndSyncUser", tokens);

                    HttpRequest<Map> request3 = PatchRequest.user(body2, user.getuId());
                    new RequestHandler<>(request3, this::sanityCheck,
                            error2 -> {
                                Log.w("patchRegTokAndSyncUser", "failure on Oli's PATCH test");
                            }

                    ).handle(Server.AuthorizationHeaders.REQUIRED);

                    // [ Fin de l'ancien "patchUser" de Oli ]


                });

    }

    private void sanityCheck(Map<String, Object> repsonse3) {


        HttpRequest<User> request4 = GetRequest.user((String)repsonse3.get("uId"));
        new RequestHandler<>(request4,
                response3 -> {
                    Log.w("patchRegTokAndSyncUser", "success on Oli's GET NewUser test: " + response3.toString());
                },
                error3 -> {
                    Log.w("patchRegTokAndSyncUser", "failure on Oli's GET NewUser test");
                }
        ).handle(Server.AuthorizationHeaders.REQUIRED);

    }

*/


}
