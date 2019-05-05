package com.acclimate.payne.simpletestapp.appUtils;

import android.content.Context;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;

import com.acclimate.payne.simpletestapp.activities.main.MainActivity;
import com.acclimate.payne.simpletestapp.deviceStorage.localStorage.FileLocalStorage;
import com.acclimate.payne.simpletestapp.monitoredZones.MonitoredZone;
import com.acclimate.payne.simpletestapp.monitoredZones.MonitoredZoneWrapper;
import com.acclimate.payne.simpletestapp.server.Server;
import com.acclimate.payne.simpletestapp.server.requests.GetRequest;
import com.acclimate.payne.simpletestapp.server.requests.HttpRequest;
import com.acclimate.payne.simpletestapp.server.requests.RequestHandler;
import com.acclimate.payne.simpletestapp.user.User;
import com.google.firebase.auth.FirebaseUser;

import java.io.FileNotFoundException;
import java.io.IOException;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;


public class App {

    private static App app;
    @Getter @Setter
    private Class currentActivity;
    @Getter
    private User currentUser;
    @Getter
    private MonitoredZoneWrapper currentUserZones = new MonitoredZoneWrapper();
    @Getter @Setter
    private boolean syncedWithServer = false;
    public static final boolean vLOLLIPOP = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;

    private final String FLOW_TAG = "Auth App Server Flow";
    private final String REQUEST_TAG = "App() Server Request";

    private InternetCheck internetCheck;

    private final static Object mutex = new Object();

    private App() {
        this.syncedWithServer = false; // when first instanciated, it hasn't been synced yet
    }

    public synchronized static App getInstance() {
        if (app == null) {
            synchronized (mutex) {
                if (app == null) {
                    app = new App();
                }
            }

        }
        return app;
    }

    public void setActivityInstance(AppCompatActivity act) {
        this.currentActivity = act.getClass();
    }


    /**
     * Pour synchroniser les informations de l'usager.
     * Sauvegarde les informations qui proviennent du
     * serveur comme étant les plus à jour.
     *
     * @param user
     * @return booléen qui indique si le serveur a correctement synchronisé
     */
    public void syncPhone(FirebaseUser user, App myApp, @NonNull final Context ctx) {
//        Log.w(FLOW_TAG, "entering syncPhone() with fbUser: " + user);

        if (user != null) { // si connecté (TODO: et pas encore synchronisé? Présentement implicite à cause de l'endroit où c'est appelé)

            // Verifies if there is an internet connection available
            internetCheck = new InternetCheck(hasInternet -> {
                if (hasInternet) {
                    // App.UserRequest = GET UserRequest(uid)
                    getSetSaveUserRequest(user, ctx);
                    getSetSaveMzRequest(user.getUid(), ctx);

                } else {
                    // Load App.User from memory
                    String uId = user.getUid();
                    loadUnsyncedLocalUser(uId, ctx);
                    loadUnsyncedLocalMz(uId, ctx);

                    myApp.setSyncedWithServer(false);
                }
            });

        } else { // pas de FirebaseUser
            myApp.setCurrentUser(null, false, ctx);
            myApp.setSyncedWithServer(false);
        }
    }


    /**
     * Setter glorifié: sauvegarde à l'interne le User SI 'user' est non-null.
     * Si le "user" a été mis comme étant "null", c'est qu'il n'y a pas de FirebaseUser qui permet
     * de s'assurer que c'est la bonne personne qui observe et manipule ce App.User
     *
     * @param user          un 'User' OU 'null'
     * @param writeInMemory Defines whether or not the User is to be considered synced with the Server.
     *                      If 'false', it means all "WRITE" on "User" will be denied.
     * @param ctx
     */
    public void setCurrentUser(User user, boolean writeInMemory, @NonNull final Context ctx) {

        if (user != null) {
            String path = AppConfig.getAppUserFilename(user.getuId());
            FileLocalStorage<User> userStorage = new FileLocalStorage<>(path, user, ctx);

            try {
                // Only if we can and should write() is it considered synced
                if (writeInMemory)
                    userStorage.write();

                App.getInstance().setSyncedWithServer(writeInMemory);
                currentUser = user;

            } catch (Exception e) {
//                Log.w(FLOW_TAG, "error while writing");
                App.getInstance().setSyncedWithServer(false);
                e.printStackTrace();
            }
        } else {
            setSyncedWithServer(writeInMemory);
            currentUser = null;
        }
    }

    public void clearCurrentUserZones() {
        currentUserZones = new MonitoredZoneWrapper();
        MainActivity.staticMapDisplay.getZoneController().clearAndUndrawZones();
    }

    /**
     * GET User à l'API d'Acclimate.
     * S'occupe aussi d'instancier le "User" de l'App.
     * Sauvegarde aussi en mémoire ce User le plus récent.
     *
     * @param fbUser jamais null
     * @param ctx
     */
    private void getSetSaveUserRequest(FirebaseUser fbUser, @NonNull final Context ctx) {

        String uId = fbUser.getUid();

        HttpRequest<User> getUser = GetRequest.user(uId);
        new RequestHandler<>(getUser,
                response -> {
//                    Log.w(REQUEST_TAG, "GET USER (" + fbUser.getUid() + ") Server response = " + response.toString());
                    App.getInstance().setCurrentUser(response, true, ctx);
                },
                error -> {
//                    Log.w(REQUEST_TAG, "Server error response = " + error.prettyPrinter());
                    loadUnsyncedLocalUser(fbUser.getUid(), ctx);
                }
        ).handle(Server.AuthorizationHeaders.REQUIRED);
    }

    /**
     * GET MZ (toutes celles d'un UID) à l'API d'Acclimate.
     * S'occupe aussi d'afficher celles-ci.
     * Sauvegarde aussi en mémoire celles-ci si une réponse provient du serveur.
     * Sinon, c'est les MZ en mémoire qui sont utilisées.
     *
     * @param uId
     * @param ctx
     */
    public void getSetSaveMzRequest(final String uId, @NonNull final Context ctx) {

        // temporarily load local MZ and draw them as 'blue'
        loadUnsyncedLocalMz(uId, ctx);
        if(isMzControllerInitialized()) {
//            Log.w(REQUEST_TAG, "pre-request: calling initializeAndDrawZones (BLUE)");
            MainActivity.staticMapDisplay.getZoneController().initializeAndDrawZones(currentUserZones, false);
        }

        HttpRequest<MonitoredZone[]> getZonesOfUser = GetRequest.zoneByUserId(uId);
        new RequestHandler<>(getZonesOfUser,
                response -> {
                    currentUserZones.setToAllMonitoredZones(response);
//                    Log.w(REQUEST_TAG, "Newly created MzWrapper length = " + currentUserZones.getZones().size());

                    if(isMzControllerInitialized()) {
//                        Log.w(REQUEST_TAG, "calling initializeAndDrawZones (RED)");
                        MainActivity.staticMapDisplay.getZoneController().initializeAndDrawZones(currentUserZones, true);
                    }

                    saveSyncedMzWrapper(currentUserZones, ctx);
                    App.getInstance().setSyncedWithServer(true);
                },
                error -> {
                    loadUnsyncedLocalMz(uId, ctx);
                    App.getInstance().setSyncedWithServer(false);
//                    Log.w(REQUEST_TAG, "Server error response = " + error.prettyPrinter());
                }
        ).handle(Server.AuthorizationHeaders.REQUIRED);
    }

    /**
     * Pour sauvegarder un MonitoredZoneWrapper en mémoire.
     * Le UID est par défaut présent et extrait.
     *
     * @param monitoredZoneWrapper
     * @param ctx
     */
    private void saveSyncedMzWrapper(MonitoredZoneWrapper monitoredZoneWrapper, @NonNull final Context ctx) {
        String filename = AppConfig.getMonitoredZoneFilename();
        final FileLocalStorage<MonitoredZoneWrapper> zoneStorage =
                new FileLocalStorage<>(filename, monitoredZoneWrapper, ctx);

        // Saving the new list of MZ for this user
        try {
            zoneStorage.setData(monitoredZoneWrapper);
            zoneStorage.write();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Lorsqu'il n'y a pas de connection internet, mais qu'un utilisateur
     * est authentifié par Firebase, alors on utilise les dernières informations
     * connues (qui sont conservées en mémoire).
     *
     * @param uId
     * @param ctx
     */
    private void loadUnsyncedLocalUser(String uId, @NonNull final Context ctx) {

        String path = AppConfig.getAppUserFilename(uId);
        FileLocalStorage<User> userStorage = new FileLocalStorage<>(path, new User(), ctx);

        try {
            User loadedUser = (User) userStorage.read();
            // Deny all User.Setter actions by setting the "sync" to false
            App.getInstance().setCurrentUser(loadedUser, false, ctx);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Lorsqu'il n'y a pas de connection internet, mais qu'un utilisateur
     * est authentifié par Firebase, alors on utilise les dernières informations
     * connues (qui sont conservées en mémoire).
     *
     * @param uId
     * @param ctx
     */
    private void loadUnsyncedLocalMz(String uId, @NonNull final Context ctx) {

        String filename = AppConfig.getMonitoredZoneFilename(uId);
        final FileLocalStorage<MonitoredZoneWrapper> zoneStorage =
                new FileLocalStorage<>(filename, currentUserZones, ctx);

        // Trying to get older file which might not exist
        try {
            currentUserZones = zoneStorage.read();
        } catch (FileNotFoundException fnf) {
//            Log.w(REQUEST_TAG, "FileNotFoundException");
            fnf.printStackTrace();
        } catch (Exception e) {
//            Log.w(REQUEST_TAG, "Exception");
            e.printStackTrace();
        }
    }

    /**
     * TODO: Refactor so that we can remove this static call
     * @return
     */
    private boolean isMzControllerInitialized() {
        return MainActivity.staticMapDisplay != null
                && MainActivity.staticMapDisplay.getZoneController() != null
                && MainActivity.staticMapDisplay.getZoneController().getZones() != null;
    }
}
