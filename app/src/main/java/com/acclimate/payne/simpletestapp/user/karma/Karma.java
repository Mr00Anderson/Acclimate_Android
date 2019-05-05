package com.acclimate.payne.simpletestapp.user.karma;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Wrapper that contains all karma infos associate with a User
 */
@NoArgsConstructor @AllArgsConstructor @Getter @Setter
public class Karma implements Serializable {

    public static final int BRONZE = 10;
    public static final int SILVER = 25;
    public static final int GOLD = 50;
    public static final int SUPERSTAR = 100;



    public static final String KARMA_POINTS = "points";
    /**
     * The current amount of karma points.
     */
    private int points;

}
