package com.acclimate.payne.simpletestapp.server.requests;

import org.springframework.http.HttpMethod;

import lombok.NonNull;

// todo
public abstract class DeleteRequest {

    private static final String all_zone_url = "all";

    public static final HttpMethod METHOD = HttpMethod.DELETE;


    public static HttpRequest<String> user(@NonNull String userId){
        return new DeleteRequest.ConcreteDelete(userId, RequestCategory.USER);
    }

    public static HttpRequest<String> alert(@NonNull String alertId){
        return new DeleteRequest.ConcreteDelete(alertId, RequestCategory.ALERT);
    }

    public static HttpRequest<String> zone(@NonNull String zoneId){
        return new DeleteRequest.ConcreteDelete(zoneId, RequestCategory.MONITORED_ZONE);
    }


    public static HttpRequest<String> allzones(String userId){
        return new DeleteRequest.ConcreteDelete(
                String.format("%s/" + userId, all_zone_url), RequestCategory.MONITORED_ZONE);
    }



    /**
         * Concrete private class representation of a Patch request used internally.
         */
    private static class ConcreteDelete extends HttpRequest<String>{

        private String id;

        ConcreteDelete(String id, RequestCategory category){
            super(category, METHOD);
            this.id = id;
            // requestEntity = new HttpEntity<>(headers);

        }


        @Override
        protected String setUrl() {
            // todo
            url = String.format(SERVER_API, category.getCategory());
            if (category == RequestCategory.ALERT) {
                // api/alerts/user/{uId}
                url = String.format(url + "%s/%s/", AlertRequestType.USER.getType(), id );
            } else {
              // api/{requestCategory}/uId
                url = String.format(url + "%s/", id);
            }
//            Log.e(SERVER, "delete url = " + url);
            return url;
        }

        @Override
        public String getResponse() {
            return String.format("Deleting %s id %s was completed successfully with server code = %s",
                    category.getCategory(), id, responseEntity.getStatusCode() == null ? "unknown" : responseEntity.getStatusCode());
        }
    }

}
