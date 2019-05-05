package com.acclimate.payne.simpletestapp.notifications;

public enum NotifType {

    /**
     * Pour pouvoir différencier les différents 'data-payload' pour chaque type de notification.
     * Devrait être identique au même ENUM qui se retrouve sur le serveur (backend) !!
     */
    NEW_LIVE_ALERT      ("Une nouvelle alerte vous concerne!"),
    NEW_USER_ALERT      ("Une nouvelle alerte d'usager vous concerne"),
    UPDATE              ("Une nouvelle version est disponible"),
    GPS                 ("Des alertes proches de vous!"),
    CUSTOM              ("x7rDgUYqbxNbZLpzWl0O4Qk8ZJuf8eow7BzZrgflCemHrQcVvR5");

    String title;

    NotifType(String title){ this.title = title; }
    public String getTitle(){ return this.title; }
}
