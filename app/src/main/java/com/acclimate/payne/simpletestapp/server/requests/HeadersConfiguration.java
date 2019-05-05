package com.acclimate.payne.simpletestapp.server.requests;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.Collections;
import java.util.List;

public class HeadersConfiguration {

    public static final List<MediaType> APPLICATION_JSON =
            Collections.singletonList(new MediaType("application","json"));

    public static final void setDafultConfig(HttpHeaders headers){
        headers.setContentType(APPLICATION_JSON.get(0));
        headers.setAccept(APPLICATION_JSON);
    }


}
