package com.acclimate.payne.simpletestapp.server.requests;

import com.acclimate.payne.simpletestapp.alerts.BasicAlert;
import com.acclimate.payne.simpletestapp.alerts.UserAlert;
import com.acclimate.payne.simpletestapp.monitoredZones.MonitoredZone;
import com.acclimate.payne.simpletestapp.user.User;

import org.springframework.http.HttpMethod;

import java.util.List;

import lombok.NonNull;

import static com.acclimate.payne.simpletestapp.server.Server.USER_LIST;

/**
 * Representation of a HTTP Post request connection to our Spring Server.<p></p>
 *
 * Post requets are use to update the Database with a new instance of a 'post-able' class, which are :
 *
 * <ul>
 *     <li>{@link User}</li>
 *     <li>{@link MonitoredZone}</li>
 *     <li>{@link BasicAlert}</li>
 *     <li>{@link UserAlert}</li>
 * </ul>
 *
 * In case of a succesfull post, the server sends back always a <emph>single instance</emph>
 * of the object that has been added.
 *
 * @author Applin
 */
public abstract class PostRequest {

    /**
     * The readable GET METHOD : POST representation of this class.
     */
    @SuppressWarnings("all")
    public static final HttpMethod METHOD = HttpMethod.POST;

    /**
     * Public factory method used to create an instance of a 'sendable' {@link HttpRequest} that updates
     * the server database with a new {@link User}. The server sends back a new instance of that user,
     * with all the fields set to their values. <p></p>
     *
     * The app must manually set all fields of the {@link User} but not
     * {@link User#lastSaveTimestamp} which is not supported on the server (internal use by the app).
     * Note that {@link User#firstName} and {@link User#lastName} are not required, they can be
     * set to null without any fear of a problem.
     *
     * @param user the instance of the {@link User }
     * @return
     */
    public static PostRequest.ConcretePost<User> user(@NonNull User user){
        return new PostRequest.ConcretePost<>(user, RequestCategory.USER,User.class);
    }

    /**
     * Public factory method used to create an instance of a 'sendable' {@link HttpRequest} that returns
     * all instances of {@link User} for the list of uIds provided.
     * <p>
     *         ******************    WARNING   *************************
     *         *****    DOES NOT POST A NEW VALUE TO THE SERVER    *****
     * </p>
     * the post method is used only bevause the server does not look at the request
     * body if it's a GET method.
     *
     * @param userIds the list of uids from which to get users
     * @return A ready to use instance of a {@link HttpRequest} which can ben sent to the server.
     */
    public static HttpRequest<User[]> user(@NonNull List<String> userIds){
        return new PostRequest.ConcretePostForUserList(userIds);
    }



    /**
     *
     * @param alert
     * @return
     */
    public static PostRequest.ConcretePost<UserAlert> alert(@NonNull UserAlert alert){
        return new PostRequest.ConcretePost<>(alert, RequestCategory.ALERT, UserAlert.class);
    }

    /**
     *
     * @param zone
     * @return
     */
    public static PostRequest.ConcretePost<MonitoredZone> zone(@NonNull MonitoredZone zone){
        return new PostRequest.ConcretePost<>(zone, RequestCategory.MONITORED_ZONE, MonitoredZone.class);
    }


    /**
     * Concrete private class representation of a Post request used internally.
     * @param <T> the class of the posted (and receive) object of the request.
     */
    private static class ConcretePost<T> extends HttpRequest<T>{

        private Class<T> clazz;

        ConcretePost(T body, RequestCategory category, Class<T> clazz){
            super(category, METHOD);
            this.clazz = clazz;
            this.body = body;
            // requestEntity = new HttpEntity<>(body, headers);
        }

        @Override
        protected String setUrl() {

            url = String.format(SERVER_API, category.getCategory());

            switch (category) {
                case ALERT:
                    url += AlertRequestType.USER.getType() + "/";
                    break;
                default: break;
            }

            return url;

        }

        @Override
        public T getResponse() {
            return getResponse(clazz);
        }

    }

    private static class ConcretePostForUserList extends HttpRequest<User[]>{

        ConcretePostForUserList(List<String> uids) {
            super(RequestCategory.USER, METHOD);
//            Log.i(SERVER, "uids length = " + uids.size());
            this.uids = uids;
        }

        @Override
        protected String setUrl() {
            url = String.format(SERVER_API, category.getCategory() + "/" + USER_LIST );
            return url;
        }

        @Override
        public User[] getResponse() {
            return getResponse(User[].class);
        }

    }


}
