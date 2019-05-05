package com.acclimate.payne.simpletestapp.server.requests.exceptions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
public class ErrorMessage {

    private String logref;
    private String message;
    private Link[] links;

    @Getter @Setter @NoArgsConstructor
    private static class Link {
        private String rel;
        private String href;
        private String hreflang;
        private String media;
        private String title;
        private String type;
        private String deprecation;

    }


    @Override
    public String toString() {
        try {
            return (new ObjectMapper().writeValueAsString(this));
        } catch (JsonProcessingException jpe){
            return String.format("{message : %s}", message);
        }
    }
}
