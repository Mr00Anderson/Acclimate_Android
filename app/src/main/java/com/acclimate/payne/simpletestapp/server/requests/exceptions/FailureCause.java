package com.acclimate.payne.simpletestapp.server.requests.exceptions;

public enum FailureCause {


    JSON_CONVERSION         ("Json conversion"),
    CLIENT_ERROR            ("malformed request"),
    SERVER_ERROR            ("Server error"),
    SERVER_EXCEPTION        ("Server thrown exception"),
    NO_CONNECTION           ("Device is not connected to internet"),
    AUTH                    ("Problème lors de l'authentification"),
    UNKNOWN                 ("Unkonw source of error"),
    NONE                    ("No error detected");

    private String description;

    FailureCause(String type){ this.description = type; }
    public String getDescription(){ return this.description; }

}
