package com.acclimate.payne.simpletestapp.server.requests.exceptions;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class SmallBodyError {

      int    timestamp = 0;
      int    status = -1;
      String error = "";
      String message = "";
      String path = "";

      @JsonIgnore
      public SmallBodyError timestamp(int timestamp) {
            this.timestamp = timestamp;
            return this;
      }

      @JsonIgnore
      public SmallBodyError status(int status) {
            this.status = status;
            return this;

      }

      @JsonIgnore
      public SmallBodyError error(String error) {
            this.error = error;
            return this;

      }

      @JsonIgnore
      public SmallBodyError message(String message) {
            this.message = message;
            return this;

      }

      @JsonIgnore
      public SmallBodyError path(String path) {
            this.path = path;
            return this;
      }


    @Override
    public String toString() {

          try  {
              return (new ObjectMapper()).writeValueAsString(this);
          } catch (JsonProcessingException jpe) {
              return "SmallBodyError{" +
                      "timestamp=" + timestamp +
                      ", status=" + status +
                      ", error='" + error + '\'' +
                      ", message='" + message + '\'' +
                      ", path='" + path + '\'' +
                      '}';
          }

    }
}
