package com.acclimate.payne.simpletestapp.server.requests;

import com.acclimate.payne.simpletestapp.server.Server;

import org.springframework.http.HttpMethod;

import java.util.Map;
import java.util.TreeMap;

import lombok.NonNull;

import static com.acclimate.payne.simpletestapp.user.karma.Karma.KARMA_POINTS;

public abstract class PatchRequest {

    /**
     * The readable HTTP PATCH METHOD representation of this class.
     */
    public static final HttpMethod METHOD = HttpMethod.PATCH;

    /**
     * User patch.
     *
     * @param body
     * @param userId
     * @return
     */
    public static PatchRequest.ConcretePatch user(@NonNull Map<String, Object> body, @NonNull String userId) {
        return new PatchRequest.ConcretePatch(body, RequestCategory.USER, userId);
    }


    /**
     * User karma patch.
     * @param points th new value of the user karma points
     * @param userId
     * @return
     */
    public static HttpRequest<Map> karma(int points, @NonNull String userId) {
        Map<String, Object> karmaMap = new TreeMap<>();
        karmaMap.put(KARMA_POINTS, points);
        return new PatchRequest.ConcretePatch(karmaMap, RequestCategory.USER,
                String.format("%s/%s/", Server.KARMA, userId));
                // Server.KARMA + "/" + userId);
    }

    // UserAlert
    public static HttpRequest<Map> alert(@NonNull Map<String, Object> body, @NonNull String alertId){
        return new PatchRequest.ConcretePatch(body, RequestCategory.ALERT,
                String.format("%s/%s", AlertRequestType.USER.getType(), alertId));
                //AlertRequestType.USER.getType() + "/" + alertId);
    }

    // UserAlert -> Geometry
    public static HttpRequest<Map> alertGeometry(@NonNull Map<String, Object> body, String alertId){
        return new PatchRequest.ConcretePatch(body, RequestCategory.ALERT,
                String.format("%s/%s/%s/", AlertRequestType.USER.getType(),Server.GEOMETRY, alertId));
    }


    // MZ
    public static HttpRequest<Map> zone(@NonNull Map<String, Object> body, String zoneId){
        return new PatchRequest.ConcretePatch(body, RequestCategory.MONITORED_ZONE, zoneId);
    }

    // MZ -> Geometry
    public static HttpRequest<Map> zoneGeometry(@NonNull Map<String, Object> body, String zoneId){
        return new PatchRequest.ConcretePatch(body, RequestCategory.MONITORED_ZONE,
                String.format("%s/%s/", Server.GEOMETRY, zoneId));
    }




    /**
     * Concrete private class representation of a Patch request used internally.
     */
    private static class ConcretePatch extends HttpRequest<Map>{

        private String urlExtension;

        ConcretePatch(Map<String, Object> body, RequestCategory category, String urlExtension){
            super(category, METHOD);
            this.urlExtension = urlExtension;
            this.body = body;
            // requestEntity = new HttpEntity<>(body, headers);
        }

        @Override
        protected String setUrl() {
            url = String.format(SERVER_API + "%s/", category.getCategory(), urlExtension);
            return url;
        }

        @Override @SuppressWarnings("unchecked")
        public Map<String, Object> getResponse() {
            return getResponse(Map.class);
        }

    }



}
