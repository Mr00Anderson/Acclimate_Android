package com.acclimate.payne.simpletestapp.authentification.wip_Refactoring;

import android.app.Activity;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public interface EmailPasswordAuth extends AuthInterface {

    /**
     * Pour créer un compte via la méthode Email/Password
     *
     * @param email email
     * @param password password
     */
    void createAccount(String email, String password);

    /**
     * Pour Sign In avec la méthode "Email/Password"
     *
     * @param email email
     * @param password password
     */
    void signInEmail(String email, String password);

    /**
     * Pour valider l'entrée de User/Passw.
     *
     * @return booléen désignant si la validation est correcte
     */
    boolean validateForm();


    /**
     * Pour envoyer le email de vérification
     */
    void sendEmailVerification(final Button verifyEmailBtn, FirebaseAuth mAuth, Activity activity);
}
