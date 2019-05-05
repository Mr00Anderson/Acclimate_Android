package com.acclimate.payne.simpletestapp.user.karma;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException (String message) {
        super(message);
    }
}
