package com.acclimate.payne.simpletestapp.alerts;

import com.acclimate.payne.simpletestapp.appUtils.Async;
import com.acclimate.payne.simpletestapp.deviceStorage.localStorage.FileLocalStorage;
import com.acclimate.payne.simpletestapp.server.Server;
import com.acclimate.payne.simpletestapp.server.requests.AlertRequestType;
import com.acclimate.payne.simpletestapp.server.requests.GetRequest;
import com.acclimate.payne.simpletestapp.server.requests.HttpRequest;
import com.acclimate.payne.simpletestapp.server.requests.RequestHandler;
import com.acclimate.payne.simpletestapp.user.User;

import java.util.List;

import static com.acclimate.payne.simpletestapp.appUtils.AppConfig.ALERT_WRAPPER_FILENAME;

/**
 * Controller Class used to manage interactions between
 * <p>
 * Alerts : Three type of alerts <ul>
 * <li>Live : Currently live alerts confrimed from an official source like the governement</li>
 * <li>User : An alert that hjas been posted by a user of the application. Not yet confirmed</li>
 * <li>Historical : An alertthat is NO LONGUER CURRENT and has been saved in the internal
 * Database of Acclimete. Can come from</li>
 * </ul>
 */
public class AlertController {


    public static class FetchBasicAlert {

        private boolean userAlertsFetched = false;
        private boolean liveAlertsFetched = false;

        private boolean userAlertsSucess = false;
        private boolean liveAlertsSucess = false;


        public boolean isSuccessful() {
            return userAlertsSucess && liveAlertsSucess;
        }

        @Async
        public void begin(IAcclimateAlertFetch fetchingActivity) {

            FileLocalStorage<AlertWrapper> wrapperStorage =
                    new FileLocalStorage<>(ALERT_WRAPPER_FILENAME, new AlertWrapper(), fetchingActivity.getContext());

            // Checks if there exist a JSON file containing already fetched alerts.
            // If it's there and not to old, use that file instead of doing a request.
            try {

                AlertWrapper wrapper = wrapperStorage.read();

//                Log.i(STORAGE,"is old ? = " + (Calendar.getInstance().getTimeInMillis() - wrapper.getLastRequestTime() > REQUEST_TIME_TRESHOLD));

                if (wrapper.isOld()) {
//                    Log.i(STORAGE,"Current time = " + Calendar.getInstance().getTimeInMillis());

//                    Log.i(STORAGE,"last save = " + wrapper.getLastRequestTime());

                    requestBasicAlertsFromServer(fetchingActivity, wrapper);

                } else {
//                    Log.i(STORAGE, "Alerts fetched from internal storage");
                    fetchingActivity.onFetchedAlert(wrapper, true);
                }

            } catch (Exception ioe) {

//                Log.e(STORAGE, "Could not read Wrapper JSON File from storage : " + ioe.getMessage());
                requestBasicAlertsFromServer(fetchingActivity, new AlertWrapper());

            }

        }


        @Async
        private void requestBasicAlertsFromServer(IAcclimateAlertFetch fetchingActivity, AlertWrapper wrapper) {

            HttpRequest<UserAlert[]> requestUserAlerts = GetRequest.alert(AlertRequestType.USER);

            new RequestHandler<>(requestUserAlerts,


                    response -> {

//                        Log.i(SERVER_HANDLE, "User alert length = " + response.length);

                        wrapper.setUser(response);
                        userAlertsFetched = true;
                        userAlertsSucess = true;

                        if (liveAlertsFetched) {
                            fetchingActivity.onFetchedAlert(wrapper, isSuccessful());
                        }

                    },


                    exception -> {

//                        Log.e(SERVER_HANDLE, "Could not load user alerts : " + exception.prettyPrinter());

                        wrapper.setUser(new UserAlert[]{});
                        userAlertsFetched = true;

                        if (liveAlertsFetched) {
                            fetchingActivity.onFetchedAlert(wrapper, isSuccessful());
                        }

                    }).handle(Server.AuthorizationHeaders.NONE);


            HttpRequest<BasicAlert[]> getLiveAlert = GetRequest.alert(AlertRequestType.LIVE);
            new RequestHandler<>(getLiveAlert,


                    response -> {
//                        Log.i(SERVER_HANDLE, "Live alert length = " + response.length);

                        wrapper.setLive(response);
                        liveAlertsFetched = true;
                        liveAlertsSucess = true;

                        if (userAlertsFetched) {
                            fetchingActivity.onFetchedAlert(wrapper, isSuccessful());
                        }

                    },


                    exception -> {

//                        Log.e(SERVER_HANDLE, "Could not load live alerts : " + exception.getMessage());

                        wrapper.setLive(new BasicAlert[]{});
                        liveAlertsFetched = true;

                        if (userAlertsFetched) {
                            fetchingActivity.onFetchedAlert(wrapper, isSuccessful());
                        }

                    }).handle(Server.AuthorizationHeaders.NONE);

        }

    }


    public static class Builder<T extends BasicAlert> {

        // building params
        protected String id;                     // Serveur
        protected String nom;                    // UserRequest
        protected String source;                 // UserRequest
        protected String territoire;             // UserRequest
        protected String dateDeMiseAJour;        // App
        protected String description;            // UserRequest
        protected String type;                   // UserRequest
        protected Geometry geometry;               // App
        protected String certitude;              // App : auto
        protected String severite;               //
        protected String urgence;                //
        protected String sousCategorie;          // UserRequest
        protected String count;                  // App : 0
        protected double lat;
        protected double lng;
        private Class<T> alertClass;
        // User alert params
        private List<String> plusOneUsers;       // vide par défaut, par l'app
        private List<String> minusOneUsers;      // vide par défaut, par l'app
        private String photoPath;          // TODO : comment faire ?
        private int score;              // défaut 0, par l'app
        private User user;


        public Builder(Class<T> clazz) {
            if (clazz == BasicAlert.class || clazz == UserAlert.class) {
                alertClass = clazz;
            } else {
                throw new IllegalArgumentException(
                        "AlertController.Builder can only build instances BasicAlert or UserAlert.");
            }
        }


        public AlertController.Builder id(String id) {
            this.id = id;
            return this;
        }

        public AlertController.Builder nom(String nom) {
            this.nom = nom;
            return this;

        }

        public AlertController.Builder source(String source) {
            this.source = source;
            return this;
        }

        public AlertController.Builder territoire(String territoire) {
            this.territoire = territoire;
            return this;
        }

        public AlertController.Builder dateDeMiseAJour(String dateDeMiseAJour) {
            this.dateDeMiseAJour = dateDeMiseAJour;
            return this;
        }

        public AlertController.Builder description(String description) {
            this.description = description;
            return this;
        }

        public AlertController.Builder type(String type) {
            this.type = type;
            return this;
        }

        public AlertController.Builder geometry(Geometry geometry) {
            this.geometry = geometry;
            return this;
        }

        public AlertController.Builder certitude(String certitude) {
            this.certitude = certitude;
            return this;
        }

        public AlertController.Builder severite(String severite) {
            this.severite = severite;
            return this;
        }

        public AlertController.Builder urgence(String urgence) {
            this.urgence = urgence;
            return this;
        }

        public AlertController.Builder sousCategorie(String sousCategorie) {
            this.sousCategorie = sousCategorie;
            return this;
        }

        public AlertController.Builder count(String count) {
            this.count = count;
            return this;
        }

        public AlertController.Builder lng(double lng) {
            this.lng = lng;
            return this;
        }

        public AlertController.Builder lat(double lat) {
            this.lat = lat;
            return this;
        }


        // user alert
        public AlertController.Builder plusOneUsers(List<String> plusOneUsers) {
            if (alertClass == UserAlert.class) this.plusOneUsers = plusOneUsers;
            return this;
        }

        public AlertController.Builder minusOneUsers(List<String> minusOneUsers) {
            if (alertClass == UserAlert.class) this.minusOneUsers = minusOneUsers;
            return this;
        }

        public AlertController.Builder photoPath(String photoPath) {
            if (alertClass == UserAlert.class) this.photoPath = photoPath;
            return this;
        }

        public AlertController.Builder score(int score) {
            if (alertClass == UserAlert.class) this.score = score;
            return this;
        }

        public AlertController.Builder user(User user) {
            if (alertClass == UserAlert.class) this.user = user;
            return this;
        }

        @SuppressWarnings("uncheked")
        public T build() {

            if (alertClass == BasicAlert.class) {

                BasicAlert basicAlert = new BasicAlert();
                basicAlert.setNom(nom);
                basicAlert.setSource(source);
                basicAlert.setTerritoire(territoire);
                basicAlert.setDateDeMiseAJour(dateDeMiseAJour);
                basicAlert.setDescription(description);
                basicAlert.setType(type);
                basicAlert.setGeometry(geometry);
                basicAlert.setCertitude(certitude);
                basicAlert.setSeverite(severite);
                basicAlert.setUrgence(urgence);
                basicAlert.setSousCategorie(sousCategorie);
                basicAlert.setCount(count);
                basicAlert.setLat(lat);
                basicAlert.setLng(lng);
                return (T) basicAlert;

            } else {

                UserAlert userAlert = new UserAlert();
                userAlert.setNom(nom);
                userAlert.setSource(source);
                userAlert.setTerritoire(territoire);
                userAlert.setDateDeMiseAJour(dateDeMiseAJour);
                userAlert.setDescription(description);
                userAlert.setType(type);
                userAlert.setGeometry(geometry);
                userAlert.setCertitude(certitude);
                userAlert.setSeverite(severite);
                userAlert.setUrgence(urgence);
                userAlert.setSousCategorie(sousCategorie);
                userAlert.setCount(count);
                userAlert.setLat(lat);
                userAlert.setLng(lng);
                userAlert.setMinusOneUsers(minusOneUsers);
                userAlert.setPlusOneUsers(plusOneUsers);
                userAlert.setPhotoPath("");
                userAlert.setScore(1);
                userAlert.setUser(user);
                return (T) userAlert;

            }


        }


    }

}
