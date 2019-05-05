package com.acclimate.payne.simpletestapp.server.google;

import com.acclimate.payne.simpletestapp.server.google.response.GoogleSearchResponse;

import org.osmdroid.util.BoundingBox;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class GoogleSearchRequest {

    private static String googleAPI = "https://maps.googleapis.com/maps/api/geocode/json?address=";

    /**
     * S'occupe de traite la string reçu
     * @param query
     * @return
     */
    private static GoogleSearchResponse getResponse(String query) {

        // Pour bien formatter la requête (" " -> "+")
        if(query.contains(" ")) {

            StringBuilder sB = new StringBuilder();
            String[] strArray = query.split(" ");

            for(String str : strArray) {
                sB.append(str).append("+");
            }

            // Pour enlever le dernier "+" rajouté.
            query = sB.toString().substring(0, sB.length()-1);
        }

        // Set up pour la requête
        final GoogleSearchResponse[] hacks = new GoogleSearchResponse[1];
        final RestTemplate restTemplate = new RestTemplate();
        final HttpHeaders requestHeaders = new HttpHeaders();
        final HttpEntity<?> requestEntity = new HttpEntity<Object>(requestHeaders);
        final String completeRequest = googleAPI + query;

//        Log.w("Search query", completeRequest);

        // Effectuer la requête via une connection internet
        Thread connection = new Thread(() -> {
            try{
                // Make the HTTP GET request, marshaling the response from JSON to an array of Events
                ResponseEntity<GoogleSearchResponse> responseEntity =
                        restTemplate.exchange(completeRequest, HttpMethod.GET, requestEntity, GoogleSearchResponse.class);

                hacks[0] = responseEntity.getBody();
            } catch (Exception e){
//                Log.w("Google test", "failure in thread");
                e.printStackTrace();
            }
        });

        connection.start();

        try {
            connection.join();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return hacks[0];
    }

    /**
     * Pour obtenir la Bounding Box associée au 'query'
     * @param query exemple "Montreal Quebec"
     * @return retourne la BB d'une recherche avec "montreal+quebec" (transformé)
     */
    public static BoundingBox getBoundingBox(String query) throws Exception {
        GoogleSearchResponse response = GoogleSearchRequest.getResponse(query);
        return response.getResult().geometry.viewport.getBoundingBox();
    }
}
