package com.acclimate.payne.simpletestapp.user;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;

import com.acclimate.payne.simpletestapp.R;
import com.acclimate.payne.simpletestapp.user.karma.Karma;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter @Setter
public class User implements Serializable {
    /* TODO: Add a check on "App.getInstance().getSynchronizedWithServer();"
       if false, no SETTER should be allowed
       */

    private String uId;

    private ArrayList<String> registrationToken = new ArrayList<>();
    private String dateCreation;
    private Karma karma;

    //Obligatoires
    private String userName;

    @JsonIgnore
    private long lastSaveTimestamp;

    @JsonIgnore
    public int getPoints() {
        return karma.getPoints();
    }


    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public void setLastSaveTimestamp(){
        lastSaveTimestamp = Calendar.getInstance().getTimeInMillis();
    }


    public Drawable getIcon(@NonNull Resources res){
        int karma = getPoints();
        if (karma < 10) {
            return res.getDrawable(R.drawable.ic_karma_bronze);
        } else if (karma < 25){
            return res.getDrawable(R.drawable.ic_karma_silver);
        } else if (karma < 50){
            return res.getDrawable(R.drawable.ic_karma_gold);
        } else if (karma < 100) {
            return res.getDrawable(R.drawable.ic_karma_superstar);
        } else {
            return res.getDrawable(R.drawable.ic_karma_god);
        }
    }

    @Override
    public String toString() {

        try {
            return (new ObjectMapper()).writeValueAsString(this);
        } catch (Exception jpe) {
            return "User{" +
                    "uId='" + uId + '\'' +
                    ", registrationToken=" + registrationToken +
                    ", dateCreation='" + dateCreation + '\'' +
                    ", karma=" + karma +
                    ", userName='" + userName + '\'' +
                    ", lastSaveTimestamp=" + lastSaveTimestamp +
                    '}';
        }
    }

    @Override
    public boolean equals(Object obj) {
        User other = (User) obj;
        return uId.equals(other.uId);
    }

}
