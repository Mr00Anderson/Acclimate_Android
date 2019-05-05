//package com.example.payne.simpletestapp.networkTest.old;
//
//import android.util.Log;
//
//import com.example.payne.simpletestapp.server.Server;
//import com.example.payne.simpletestapp.server.requests.AlertRequestType;
//import com.example.payne.simpletestapp.server.requests.RequestCategory;
//
//import org.springframework.http.HttpAuthentication;
//import org.springframework.http.HttpEntity;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpMethod;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.client.RestTemplate;
//
//import static com.example.payne.simpletestapp.networkTest.ServerRequestTest.TAG_SERVEUR;
//import static com.example.payne.simpletestapp.server.requests.HeadersConfiguration.APPLICATION_JSON;
//
//public class StringTestRequestBuilder<T> {
//
//    private HttpMethod                  method;
//    private RequestCategory             requestCategory;
//    private AlertRequestType            alertType;
//    private String                      customUrlParam;
//    private T                           body;
//    private HttpAuthentication          auth;
//
//
//    public class StringRequest {
//
//        private String response;
//
//        private String url;
//        private RestTemplate template;
//        private HttpMethod method;
//        private HttpEntity<?> entity;
//
//        public String getResponse(){
//            return response;
//        }
//
//        StringRequest(String url, RestTemplate template, HttpMethod method, HttpEntity<T> entity){
//
//            this.url = url; this.template = template; this.method = method; this.entity = entity;
//        }
//
//        public ResponseEntity<String> send(){
//            ResponseEntity<String> responseEntity = template.exchange(url, method, entity, String.class);
//            response = responseEntity.getBody();
//            return responseEntity;
//        }
//
//    }
//
//
//    public StringTestRequestBuilder<T> method(HttpMethod method) {
//        this.method = method;
//        return this;
//    }
//
//    public StringTestRequestBuilder<T> category(RequestCategory category) {
//        this.requestCategory = category;
//        return this;
//
//    }
//
//    public StringTestRequestBuilder<T> alertType(AlertRequestType alertType) {
//        this.alertType = alertType;
//        return this;
//
//    }
//
//    public StringTestRequestBuilder<T> customUrlParam(String customUrlParam) {
//        this.customUrlParam = customUrlParam;
//        return this;
//
//    }
//
//    public StringTestRequestBuilder<T> body(T body) {
//        this.body = body;
//        return this;
//
//    }
//
//    public StringTestRequestBuilder<T> auth(HttpAuthentication auth){
//        this.auth = auth;
//        return this;
//    }
//
//
//    public StringRequest build(){
//
//        RestTemplate restTemplate = new RestTemplate();
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(APPLICATION_JSON.get(0));
//        headers.setAccept(APPLICATION_JSON);
//        HttpEntity<T> requestEntity;
//
//        if (body == null) {
//            requestEntity = new HttpEntity<>(headers);
//        } else {
//            requestEntity = new HttpEntity<>(body, headers);
//
//        }
//
//        if (auth != null){
//            headers.setAuthorization(auth);
//            Log.i(TAG_SERVEUR, "Auth set (STRING)");
//        }
//
//        String url = Server.BASE_SERVER_ADDRESS + requestCategory.getCategory() +
//                (alertType == null ? "" : "/" + alertType.getType()) +
//                (customUrlParam == null ? "" : "/" + customUrlParam);
//
//
//        return new StringRequest(url, restTemplate, method, requestEntity);
//
//    }
//
//}
