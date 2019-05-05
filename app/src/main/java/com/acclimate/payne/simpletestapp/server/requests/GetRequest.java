package com.acclimate.payne.simpletestapp.server.requests;

import com.acclimate.payne.simpletestapp.alerts.BasicAlert;
import com.acclimate.payne.simpletestapp.alerts.UserAlert;
import com.acclimate.payne.simpletestapp.monitoredZones.MonitoredZone;
import com.acclimate.payne.simpletestapp.user.User;

import org.osmdroid.util.BoundingBox;
import org.springframework.http.HttpMethod;

import lombok.NonNull;

/**
 * Representation of a Get request HTTP connection to our Spring Server.<p></p>
 *
 * The response of the Get request can vary depending on the nature of the request.
 * Four different type of return objects can be requested all as single instances or as arrays:
 *
 * <ul>
 *     <li>{@link User}</li>
 *     <li>{@link MonitoredZone}</li>
 *     <li>{@link BasicAlert}</li>
 *     <li>{@link UserAlert}</li>
 * </ul>
 *
 * The return type is important and should match what the server is sending back.
 * Requests for alerts usually send back arrays of Alerts, although a request for a single alert
 * based on its unique id is supported with the id as a parameter in the url.
 * The {@link AlertRequestType} Enum is used to differentiate betweenlive alerts,
 * user alerts and historical alerts, although historical and live are represented
 * by the {@link BasicAlert} class and user alerts by the {@link UserAlert} class.
 * Requests for {@link User} and {@link MonitoredZone} also support single instance
 * or array responses.
 *
 * @author Applin
 */
@SuppressWarnings("unchecked")
public abstract class GetRequest {

    /**
     * The readable GET METHOD representation of this class.
     */
    @SuppressWarnings("all")
    public static final HttpMethod METHOD = HttpMethod.GET;

    /**
     * Public factory method used to create an instance of a 'sendable' {@link HttpRequest} that returns
     * a single instance of a {@link User}.
     *
     * @param userId the unique id of the {@link User} that is requested from the server.
     * @return A ready to use instance of a {@link HttpRequest} which can ben sent to the server.
     */
    public static HttpRequest<User> user(@NonNull String userId){
        return new GetRequest.UserRequest(userId);
    }



    /**
     * Public factory method used to create an instance of a 'sendable' {@link HttpRequest} that returns
     * all instances of {@link User} registered in the server.
     *
     * @return A ready to use instance of a {@link HttpRequest} which can ben sent to the server.
     */
    public static HttpRequest<User[]> user(){
        return new GetRequest.UserRequestArray();
    }


    /**
     * Public factory method used to create an instance of a 'sendable' {@link HttpRequest} that returns
     * an array of alerts. Can either be {@link BasicAlert} or {@link UserAlert} depending on the
     * {@link AlertRequestType} passed as parameter.
     *
     * @param type The Type of the Alert requested
     * @return A ready to use instance of a {@link HttpRequest} which can ben sent to the server.
     */
    @SuppressWarnings("all")
    public static HttpRequest alert(@NonNull AlertRequestType type){

        switch (type){
            case USER:
                return new GetRequest.Alert<UserAlert[]>(AlertRequestType.USER, UserAlert[].class);
            case LIVE: case HISTORICAL: default:
                return new GetRequest.Alert<BasicAlert[]>(type, BasicAlert[].class);
        }

    }


    /**
     * Public factory method used to create an instance of a 'sendable' {@link HttpRequest} that returns
     * an array of alerts that are contained in the specified boundingBox.
     * Can either be {@link BasicAlert} or {@link UserAlert} depending on the
     * {@link AlertRequestType} passed as parameter.
     *
     * @param type The Type of the Alert requested
     * @return A ready to use instance of a {@link HttpRequest} which can ben sent to the server.
     */
    @SuppressWarnings("all")
    public static HttpRequest alert(@NonNull AlertRequestType type, BoundingBox box){
        switch (type){
            case USER:
                return new GetRequest.Alert<UserAlert[]>(AlertRequestType.USER, UserAlert[].class, box);
            case LIVE: case HISTORICAL: default:
                return new GetRequest.Alert<BasicAlert[]>(type, BasicAlert[].class, box);
        }

    }


    /**
     * Public factory method used to create an instance of a 'sendable' {@link HttpRequest} that returns
     * a single instance of an alert. Can either be {@link BasicAlert} or {@link UserAlert} depending on the
     * {@link AlertRequestType} passed as parameter.
     *
     * @param type The Type of the Alert requested
     * @param alertId The unique id of the alert requiresd.
     * @return A ready to use instance of a {@link HttpRequest} which can ben sent to the server.
     */
    @SuppressWarnings("all")
    public static HttpRequest alert(@NonNull AlertRequestType type, @NonNull String alertId){

        switch (type){
            case LIVE: case HISTORICAL: default:
                return new GetRequest.Alert<>(type, alertId, BasicAlert.class);
            case USER:
                return new GetRequest.Alert<>(AlertRequestType.USER, alertId, UserAlert.class);
        }

    }


    /**
     * Public factory method used to create an instance of a 'sendable' {@link HttpRequest} that can returns,
     * from the server as a result of the request, an array of {@link MonitoredZone}.
     *
     * @param user
     * @return A ready to use instance of a {@link HttpRequest} which can ben sent to the server.
     */
    public static HttpRequest<MonitoredZone[]> zone(@NonNull User user){
        return new GetRequest.Zone<>(user, MonitoredZone[].class);
    }


    /**
     * Public factory method used to create an instance of a 'sendable' {@link HttpRequest} that can returns,
     * from the server as a result of the request, an array of {@link MonitoredZone}.
     *
     * @param userId The
     * @return A ready to use instance of a {@link HttpRequest} which can ben sent to the server.
     */
    public static HttpRequest<MonitoredZone[]> zoneByUserId(@NonNull String userId){
        User tmp = new User(); tmp.setuId(userId);
        return new GetRequest.Zone<>(tmp, MonitoredZone[].class);
    }



    /**
     * Public factory method used to create an instance of a 'sendable' {@link HttpRequest} that can returns
     * from the server as a result of the request a single instance of a {@link MonitoredZone}.
     *
     * @param zoneId The unique id of the {@link MonitoredZone} that is requested.
     * @return A ready to use instance of a {@link HttpRequest} which can ben sent to the server.
     */
    public static HttpRequest<MonitoredZone> zone(@NonNull String zoneId){
        return new GetRequest.Zone<>(zoneId, MonitoredZone.class);
    }


    /**
     * Public factory method used to create an instance of a 'sendable' {@link HttpRequest} that can returns,
     * from the server as a result of the request, all registered {@link Zone}.
     *
     * @return A ready to use instance of a {@link HttpRequest} which can ben sent to the server.
     */
    public static HttpRequest<MonitoredZone[]> zone(){
        return new GetRequest.Zone<>(MonitoredZone[].class);
    }


    /**
     * Concrete private class representation of a Get request for a {@link User}.
     */
    private static class UserRequest extends HttpRequest<User> {

        private String userId;

        /**
         * Construct a ready to use GetRequest for a UserRequest.
         * @param userId the Id of the user to fetch from server.
         */
        public UserRequest(String userId) {
            super(RequestCategory.USER, METHOD);
            this.userId = userId;
            // requestEntity = new HttpEntity<>(headers);
        }


        @Override
        protected String setUrl() {
            if (userId != null){
                url = String.format(SERVER_API + "%s/", category.getCategory(), userId);
            } else {
                url += category.getCategory();
            }
            return url;
        }

        /**
         * Returns the response, being converted
         * @return
         */
        @Override
        public User getResponse() {
            return getResponse(User.class);
        }

        /**
         *
         * @return
         */
        public String getUserId(){
            return this.userId;
        }

    }


    private static class UserRequestArray extends HttpRequest<User[]> {


         UserRequestArray() {
            super(RequestCategory.USER, METHOD);
            // requestEntity = new HttpEntity<>(headers);

        }


        @Override
        protected String setUrl() {
             return String.format(SERVER_API, category.getCategory());

        }

        @Override
        public User[] getResponse() {
            return getResponse(User[].class);
        }

    }

        /**
     * Concrete private class representation of a Get request for an
     * {@link BasicAlert} or {@link UserAlert} an Array of one of them.
     */
    private static class Alert<T> extends HttpRequest<T> {

        private AlertRequestType type;
        private Class<T> clazz;
        private String alertId;
        private BoundingBox box;

        Alert(AlertRequestType type, Class<T> clazz) {
            super(RequestCategory.ALERT, METHOD);
            this.type = type;
            this.clazz = clazz;
        }

        Alert(AlertRequestType type, Class<T> clazz, BoundingBox box) {
            this(type, clazz);
            this.box = box;
        }


        Alert(AlertRequestType type, String alertId, Class<T> clazz) {
            this(type, clazz);
            this.alertId = alertId;
        }


        @Override
        protected String setUrl() {
            url = String.format(SERVER_API + "%s/", category.getCategory(), type.getType());
            if (alertId != null){
                url += alertId;
            }
            if (box != null) {
                url = url + "?north=%s&south=%s&east=%s&west=%s";
                url = String.format(url, box.getLatNorth(), box.getLatSouth(), box.getLonEast(), box.getLonWest());
            }
            return url;
        }

        @Override
        public T getResponse() {
            // requestEntity = new HttpEntity<>(headers);
            return getResponse(clazz);
        }
    }

    /**
     * Concrete private class representation of a Get request for
     * a {@link com.acclimate.payne.simpletestapp.monitoredZones.MonitoredZone}.
     */
    private static class Zone<T> extends HttpRequest<T>{

        private String zoneId;
        private User user;
        private Class<T> clazz;

        Zone(String zoneId, Class<T> clazz){
            this(clazz);
            this.zoneId = zoneId;
        }

        Zone(User user, Class<T> clazz){
            this(clazz);
            this.user = user;
        }

        Zone(Class<T> clazz){
            super(RequestCategory.MONITORED_ZONE, METHOD);
            this.clazz = clazz;
        }

        @Override
        protected String setUrl() {

            url = String.format(SERVER_API, category.getCategory());

            if (zoneId != null){
                url += zoneId;
            } else if (user != null){
                url += "uid/" + user.getuId();
            }

            return url;

        }

        @Override
        public T getResponse() {
            // requestEntity = new HttpEntity<>(headers);
            return getResponse(clazz);
        }
    }


}
