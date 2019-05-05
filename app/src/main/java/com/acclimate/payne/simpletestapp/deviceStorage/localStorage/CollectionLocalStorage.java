package com.acclimate.payne.simpletestapp.deviceStorage.localStorage;

import android.util.Log;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
class CollectionLocalStorage<T> {


    private List<T> collection;
    private long timestamp;

    public CollectionLocalStorage(ArrayList<T> collection){
        this.collection = collection;
        this.timestamp = Calendar.getInstance().getTimeInMillis();
    }


    @Override @JsonIgnore
    public String toString() {

        ObjectMapper mapper = new ObjectMapper();

        try {

            return mapper.writeValueAsString(this);

        } catch (JsonProcessingException jpe){
            Log.e("Error to String","Error while printing CollectionLocalStorage infos", jpe);
            StringBuilder sb = new StringBuilder();
            for (T elem : collection){
                sb.append(elem.toString()).append("\n");
            }
            return sb.append(timestamp).toString();
        }

    }
}
