package com.acclimate.payne.simpletestapp;

import com.acclimate.payne.simpletestapp.alerts.BasicAlert;
import com.acclimate.payne.simpletestapp.alerts.Geometry;
import com.acclimate.payne.simpletestapp.alerts.UserAlert;
import com.acclimate.payne.simpletestapp.monitoredZones.MonitoredZone;
import com.acclimate.payne.simpletestapp.server.Server;
import com.acclimate.payne.simpletestapp.user.User;
import com.acclimate.payne.simpletestapp.user.karma.Karma;

import java.util.ArrayList;
import java.util.Random;


public class TestObjectsInstances {

    private static TestObjectsInstances instance;

    public User             user;
    public Karma            karma;
    public Geometry    geometry1;
    public Geometry         geometry2;
    public BasicAlert basicAlert;
    public UserAlert userAlert;
    public MonitoredZone    zone;

    public synchronized static TestObjectsInstances getInstance(){
        if (instance == null){
            synchronized (TestObjectsInstances.class){
                if (instance == null){
                    instance = new TestObjectsInstances();
                }
            }

        }
        return instance;
    }

    private TestObjectsInstances() {

        user = new User();

        Karma karma = new Karma(0);
        ArrayList<String> regToken = new ArrayList<>();
        regToken.add("fowijfo433");

        user.setDateCreation(Server.getCurrentTimeFormatted());
        user.setUserName("tempoBob" + (new Random()).nextInt(500));
        user.setuId("greggy4987");
        user.setKarma(karma);
        user.setRegistrationToken(regToken);


        Random random = new Random();

        double randD = random.nextDouble();

        geometry1 = new Geometry();
        geometry1.setCoordinates(new double[]{12.0 + randD, 11.0 + randD});
        geometry1.setType("Point");

        userAlert = new UserAlert();
        userAlert = new UserAlert();
        userAlert.setId("");
        userAlert.setNom("nom de l'alerte");
        userAlert.setSource("User name");
        userAlert.setTerritoire("Montreal");
        userAlert.setCertitude("");
        userAlert.setDateDeMiseAJour("today");
        userAlert.setUrgence("Moyenne");
        userAlert.setDescription("blah blah blah");
        userAlert.setScore(0);
        userAlert.setType("Eau");
        userAlert.setSousCategorie("Innondation");
        userAlert.setPhotoPath("");
        userAlert.setGeometry(geometry1);

        userAlert.setUser(user);
        userAlert.setPlusOneUsers(new ArrayList<>());
        userAlert.setMinusOneUsers(new ArrayList<>());
        userAlert.setScore(0);

        geometry2 = new Geometry();
        geometry2.setType("Polygon");
        geometry2.setPolyCoordinates(new double[][]{{76.0, 46.0}, {76.1, 45.9}});

        zone = new MonitoredZone();
        zone.setZoneId(null);
        zone.setGeometry(geometry2);
        zone.setUser(user);
        zone.setName("Test MZ");
        zone.setDateCreation(Server.getCurrentTimeFormatted());

    }


}
