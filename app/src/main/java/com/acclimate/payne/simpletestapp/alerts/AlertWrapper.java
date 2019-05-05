package com.acclimate.payne.simpletestapp.alerts;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Arrays;
import java.util.Calendar;

import lombok.Getter;
import lombok.Setter;

import static com.acclimate.payne.simpletestapp.appUtils.AppConfig.REQUEST_TIME_TRESHOLD;

/**
 * Temporary container for alerts. <p></p>
 * This Class is used to :
 * <ol>
 *     <li>Temporary container before updating the ViewModel containing all the alerts.</li>
 *     <li>Store and retreive alerts onto the user device.</li>
 * </ol>
 *
 */
@Getter @Setter
public class AlertWrapper {

    private UserAlert[] user;
    private BasicAlert[] live;
    private BasicAlert[] histo;

    @JsonIgnore private boolean userCountInit, liveCountInit;

    @JsonIgnore private int feu = 0;
    @JsonIgnore private int eau = 0;
    @JsonIgnore private int meteo = 0;
    @JsonIgnore private int terrain = 0;
    @JsonIgnore private int feuU = 0;
    @JsonIgnore private int eauU = 0;
    @JsonIgnore private int meteoU = 0;
    @JsonIgnore private int terrainU = 0;

    private long lastRequestTime;


    public AlertWrapper(){
        lastRequestTime = Calendar.getInstance().getTimeInMillis();
    }

    /**
     * Use to calculate the amount of each alert type and update private fields containing those amounts.
     */
    @JsonIgnore
    public void initAlertAmount() {
        countUser();
        countLive();
    }


    @JsonIgnore
    private void countUser() {

        if (user != null) {

            for (UserAlert a : user) {

                if (a.getEnumType() == null){
                    a.initType();
                }

                switch (a.getEnumType()) {

                    case USER_EAU:
                        eauU++;
                        break;
                    case USER_FEU:
                        feuU++;
                        break;
                    case USER_METEO:
                        meteoU++;
                        break;
                    case USER_TERRAIN:
                        terrainU++;
                        break;

                }
            }

        }

    }


    @JsonIgnore
    private void countLive() {

        if (live != null) {

            for (BasicAlert a : live) {

                if (a.getEnumType() == null){
                    a.initType();
                }

                switch (a.getEnumType()) {

                    case EAU:
                        eau++;
                        break;
                    case FEU:
                        feu++;
                        break;
                    case METEO:
                        meteo++;
                        break;
                    case TERRAIN:
                        terrain++;
                        break;
                    default:

                }
            }

        }


    }

    /**
     *
     * @return
     */
    @JsonIgnore
    public boolean isOld(){
        return Calendar.getInstance().getTimeInMillis() - lastRequestTime > REQUEST_TIME_TRESHOLD;
    }

    @JsonIgnore
    public boolean isComplete(){
        return live != null && user != null;
    }



    @JsonIgnore
    public void setLastSavedInstanceTimestamp(){
        lastRequestTime = Calendar.getInstance().getTimeInMillis();
    }

    @Override
    public String toString() {

        try { return (new ObjectMapper().writeValueAsString(this)); }
        catch (JsonProcessingException jpe) {
            return "AlertWrapper{" +
                    "user=" + Arrays.toString(user) +
                    ", live=" + Arrays.toString(live) +
                    ", histo=" + Arrays.toString(histo) +
                    '}';
        }
    }


/*


    *//* **************** *
     *    PARCELABLE    *
     * **************** *//*


    @Override @JsonIgnore
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedArray(this.user, flags);
        dest.writeTypedArray(this.live, flags);
        dest.writeInt(this.feu);
        dest.writeInt(this.eau);
        dest.writeInt(this.meteo);
        dest.writeInt(this.terrain);
        dest.writeInt(this.feuU);
        dest.writeInt(this.eauU);
        dest.writeInt(this.meteoU);
        dest.writeInt(this.terrainU);
    }

    protected AlertWrapper(Parcel in) {
        this.user = in.createTypedArray(UserAlert.CREATOR);
        this.live = in.createTypedArray(BasicAlert.CREATOR);
        this.feu = in.readInt();
        this.eau = in.readInt();
        this.meteo = in.readInt();
        this.terrain = in.readInt();
        this.feuU = in.readInt();
        this.eauU = in.readInt();
        this.meteoU = in.readInt();
        this.terrainU = in.readInt();
    }

    @JsonIgnore
    public static final Parcelable.Creator<AlertWrapper> CREATOR = new Parcelable.Creator<AlertWrapper>() {
        @Override
        public AlertWrapper createFromParcel(Parcel source) {
            return new AlertWrapper(source);
        }

        @Override
        public AlertWrapper[] newArray(int size) {
            return new AlertWrapper[size];
        }
    };*/
}
