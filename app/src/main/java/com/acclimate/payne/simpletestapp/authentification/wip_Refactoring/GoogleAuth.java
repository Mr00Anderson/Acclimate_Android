package com.acclimate.payne.simpletestapp.authentification.wip_Refactoring;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

public interface GoogleAuth extends AuthInterface {

    /**
     * Called to begin the Google Authentification.
     * Can be used to adapt the UI based on the response.
     *
     * @param acct the signed in account
     */
    void firebaseAuthWithGoogle(GoogleSignInAccount acct);

    /**
     * Disconnect.
     */
    void revokeAccessGoogle();
}
