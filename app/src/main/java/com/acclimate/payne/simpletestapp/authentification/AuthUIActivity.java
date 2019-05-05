package com.acclimate.payne.simpletestapp.authentification;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.acclimate.payne.simpletestapp.R;
import com.acclimate.payne.simpletestapp.animator.MyAnimator;
import com.acclimate.payne.simpletestapp.appUtils.App;
import com.acclimate.payne.simpletestapp.appUtils.InternetCheck;
import com.acclimate.payne.simpletestapp.notifications.MyFirebaseMessagingService;
import com.acclimate.payne.simpletestapp.server.Server;
import com.acclimate.payne.simpletestapp.server.requests.GetRequest;
import com.acclimate.payne.simpletestapp.server.requests.HttpRequest;
import com.acclimate.payne.simpletestapp.server.requests.PatchRequest;
import com.acclimate.payne.simpletestapp.server.requests.PostRequest;
import com.acclimate.payne.simpletestapp.server.requests.RequestHandler;
import com.acclimate.payne.simpletestapp.user.User;
import com.acclimate.payne.simpletestapp.user.UserController;
import com.acclimate.payne.simpletestapp.user.karma.Karma;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
    TODO: On deployment, add app's fingerprint/SHA-1 to Google-Services.JSON
             see @ https://stackoverflow.com/questions/42871883/unregistered-on-api-console-error-when-using-firebase-google-sign-in?rq=1

https://stackoverflow.com/questions/40838154/retrieve-google-access-token-after-authenticated-using-firebase-authentication
 */


/*
The following copyright seemed obligatory.
 */
/**
 * Copyright 2016 Google Inc. All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public class AuthUIActivity extends BaseActivity implements View.OnClickListener {

    private static final int RC_SIGN_IN = 9001; // arbitrary number
    private MyAnimator myAnimator = new MyAnimator(this);

    private EmailAuthProcesses emailAuthProcesses;
    public static FirebaseAuth mAuth;

    // Pour les requêtes au serveur
    private InternetCheck internetCheck;

    // Variables globales utilisées pour la sélection de méthode d'authentification
    private static boolean hasSelectedAuthMethod = false;
    private static AuthMethod authMethod;

    private enum AuthMethod {GOOGLE, EMAIL}

    // Pour Google mode
    private GoogleSignInClient mGoogleSignInClient;
    // Pour User/Password mode
    private AutoCompleteTextView mEmailField;
    private EditText mPasswordField;
    private Button verify_email_btn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase);

        // Auth Mode Selectors listeners
        findViewById(R.id.email_selector).setOnClickListener(this);
        findViewById(R.id.google_selector).setOnClickListener(this);

        // Email/Password buttons listeners
        findViewById(R.id.email_sign_in_button).setOnClickListener(this);
        findViewById(R.id.email_register_button).setOnClickListener(this);
        findViewById(R.id.email_sign_out_button).setOnClickListener(this);
        verify_email_btn = findViewById(R.id.email_verify_button);
        verify_email_btn.setOnClickListener(this);
        // Input fields
        mEmailField = findViewById(R.id.email);
        mPasswordField = findViewById(R.id.password);

        // Utilisé pour les particularités de l'authentification par email/password
        emailAuthProcesses = new EmailAuthProcesses(mEmailField, mPasswordField, verify_email_btn, this);

        // Set up "Google Sign In" buttons listeners
        SignInButton signInButton = findViewById(R.id.google_sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setOnClickListener(this);
        findViewById(R.id.google_sign_out_button).setOnClickListener(this);
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    /**
     * Utilisé pour s'assurer de bien initialiser la connection avec Firebase.
     * S'occupe de mettre à jour les données internes de l'usager s'il y a connection.
     *
     * @return l'utilisateur (null si pas authentifié!)
     */
    public static FirebaseUser setUpAuthSyncUp(Context ctx) {

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        App myApp = App.getInstance();

        // Attempt at synchronizing Phone with Server
//        Log.w("Auth App Server Flow", "AuthUI.setUpAuthSyncUp() called,  myApp.isSyncedWithServer is: " + myApp.isSyncedWithServer());
        if (!myApp.isSyncedWithServer()) {
            myApp.syncPhone(user, myApp, ctx);
        }

        return user; // retourne "null" si usager pas authentifié
    }


    /**
     * Vérifie si l'utilisateur était déjà connecté ou non.
     */
    @Override
    public void onStart() {
        super.onStart();

        // Changer le titre de la barre
        getSupportActionBar().setTitle("Authentification");

        // Initialisation de l'authentification
        updateUI(setUpAuthSyncUp(this));
    }

    @Override
    public void onStop() {
        super.onStop();
    }







    /*
        UI / Contrôleur
     */

    /**
     * Contrôleur de tous les boutons.
     *
     * @param v View
     */
    @Override
    public void onClick(View v) {
        int i = v.getId();

        // Google
        if (i == R.id.google_sign_in_button) {
            signInGoogle();
        } else if (i == R.id.google_sign_out_button) {
            signOutInternetRequired(v);

            // Email and Password
        } else if (i == R.id.email_verify_button) {
            emailVerifInternetRequired(v);
        } else if (i == R.id.email_register_button) {
            createAccount(mEmailField.getText().toString(), mPasswordField.getText().toString());
        } else if (i == R.id.email_sign_in_button) {
            signInEmail(mEmailField.getText().toString(), mPasswordField.getText().toString());
        } else if (i == R.id.email_sign_out_button) {
            signOutInternetRequired(v);

            // Auth Method Selectors
        } else if (i == R.id.google_selector) {
            authMethod = AuthMethod.GOOGLE;
            hasSelectedAuthMethod = true;
            updateUI(null);
        } else if (i == R.id.email_selector) {
            authMethod = AuthMethod.EMAIL;
            hasSelectedAuthMethod = true;
            updateUI(null);
        }
    }

    /**
     * Vibrates the button (and prevents the action) if there is no internet.
     *
     * @param v The button
     */
    private void signOutInternetRequired(View v) {
        v.setEnabled(false);
        internetCheck = new InternetCheck(hasInternet -> {
            if (hasInternet) {
                hasSelectedAuthMethod = false;
                signOut();
            } else {
                Snackbar.make(findViewById(R.id.auth_logo_acclimate),
                        "Impossible sans internet", Snackbar.LENGTH_SHORT).show();
                myAnimator.fastShakingAnimation(v);
            }
            v.setEnabled(true);
        });
    }

    /**
     * Vibrates the button (and prevents the action) if there is no internet.
     *
     * @param v The button
     */
    private void emailVerifInternetRequired(View v) {
        v.setEnabled(false);
        internetCheck = new InternetCheck(hasInternet -> {
            if (hasInternet) {
                emailAuthProcesses.sendEmailVerification(mAuth);
            } else {
                Snackbar.make(findViewById(R.id.auth_logo_acclimate),
                        "Impossible sans internet", Snackbar.LENGTH_SHORT).show();
                myAnimator.fastShakingAnimation(v);
            }
            v.setEnabled(true);
        });
    }

    /**
     * To replace the old "RETOUR" button which wasn't good UI Design.
     */
    @Override
    public void onBackPressed() {

        if(mAuth.getCurrentUser() != null) { // Sign in
            finish();
        } else { // Not signed in
            if (hasSelectedAuthMethod) {
                authMethod = null;
                hasSelectedAuthMethod = false;
                updateUI(null);
            } else {
                finish();
            }
        }
    }

    /**
     * Pour updater le UI en correspondance avec les réponses d'actions.
     *
     * @param user le user
     */
    private void updateUI(FirebaseUser user) {
        hideProgressDialog();

        // Pour afficher le bon mode
        findViewById(R.id.Selected_Method).setVisibility(View.GONE);
        findViewById(R.id.Mode_Selection).setVisibility(View.GONE);

        if (user != null) { // IS signed in
            hasSelectedAuthMethod = true;
            findViewById(R.id.Selected_Method).setVisibility(View.VISIBLE);
            findViewById(R.id.Email_Method).setVisibility(View.GONE);
            findViewById(R.id.Google_Method).setVisibility(View.GONE);

            // Pour obtenir le ProviderID (type de l'authenticateur fédérale)
            switch (user.getProviders().get(0)) {
                case "google.com":
                    authMethod = AuthMethod.GOOGLE;
                    break;
                case "password":
                    authMethod = AuthMethod.EMAIL;
                    break;
                default:
                    break;
            }

            // Google
            if (authMethod == AuthMethod.GOOGLE) {
                findViewById(R.id.google_sign_in_button).setVisibility(View.GONE);
                findViewById(R.id.google_sign_out_fields).setVisibility(View.VISIBLE);
                findViewById(R.id.Google_Method).setVisibility(View.VISIBLE);

                // Show user info
                ((TextView) findViewById(R.id.google_DisplayName)).setText(user.getDisplayName());
                ((TextView) findViewById(R.id.google_Email)).setText(user.getEmail());
                Picasso.get().load(user.getPhotoUrl())
                        .placeholder(R.drawable.ic_authentication)
                        .resize(250, 250)
                        .centerCrop()
                        .into((ImageView) findViewById(R.id.google_Photo));
            }

            // Email/password
            if (authMethod == AuthMethod.EMAIL) {
                findViewById(R.id.google_sign_out_fields).setVisibility(View.GONE);
                findViewById(R.id.email_not_signed_in_fields).setVisibility(View.GONE);
                findViewById(R.id.email_not_signed_in_buttons).setVisibility(View.GONE);
                findViewById(R.id.email_sign_out_fields).setVisibility(View.VISIBLE);
                findViewById(R.id.Email_Method).setVisibility(View.VISIBLE);

                // Show email
                ((TextView) findViewById(R.id.email_show)).setText(user.getEmail());

                // Email verificator
                if (user.isEmailVerified()) {
                    findViewById(R.id.email_verify_button).setVisibility(View.GONE);
                } else {
                    Snackbar.make(findViewById(R.id.auth_logo_acclimate),
                            "Vous n'avez toujours pas été vérifié!", Snackbar.LENGTH_SHORT).show();
                    findViewById(R.id.email_verify_button).setVisibility(View.VISIBLE);
                }
            }


        } else { // NOT signed in
            if (hasSelectedAuthMethod) { // Mode sélectionné
                findViewById(R.id.Selected_Method).setVisibility(View.VISIBLE);
                findViewById(R.id.Google_Method).setVisibility(View.GONE);
                findViewById(R.id.Email_Method).setVisibility(View.GONE);
                switch (authMethod) {
                    case EMAIL:
                        findViewById(R.id.Email_Method).setVisibility(View.VISIBLE);
                        findViewById(R.id.email_not_signed_in_buttons).setVisibility(View.VISIBLE);
                        findViewById(R.id.email_not_signed_in_fields).setVisibility(View.VISIBLE);
                        findViewById(R.id.email_sign_out_fields).setVisibility(View.GONE);
                        break;

                    case GOOGLE:
                        findViewById(R.id.Google_Method).setVisibility(View.VISIBLE);
                        findViewById(R.id.google_sign_in_button).setVisibility(View.VISIBLE);
                        findViewById(R.id.google_sign_out_fields).setVisibility(View.GONE);
                        break;

                    default:
                        break;
                }


            } else { // Pas encore sélectionné son AuthMode
                findViewById(R.id.Mode_Selection).setVisibility(View.VISIBLE);
            }
        }
    }







    /*
        REQUÊTES SERVEUR
     */

    private void postAndSyncUser(User user) {
        final String TAG = "postAndSyncUser()";

        Log.w(TAG, "user is: " + user.toString());
        updateUI(mAuth.getCurrentUser());

        HttpRequest<User> newUser = PostRequest.user(user);
        new RequestHandler<>(newUser,
                response -> {
                    Log.w(TAG, "success, write user: " + user.toString());
                    App.getInstance().setCurrentUser(user, true, this);
                    Log.w("SERVEUR", "POST user request response = " + response.toString());
                    //updateUI(mAuth.getCurrentUser()); TODO: reintegrate once "Synchro en cours" is done
                },
                error -> {
                    // TODO: Si le serveur réponds par une ERREUR : Sign out + request DELETE to Firebase User DB via server ?
                    Log.w(TAG, "fail with user: " + user.toString());
                    Snackbar.make(findViewById(R.id.auth_logo_acclimate),
                            "Une erreur s'est produite", Snackbar.LENGTH_SHORT).show();
                    signOut();
                    updateUI(null);
                }
        ).handle(Server.AuthorizationHeaders.REQUIRED);
    }

    /**
     * S'occupe aussi d'instancier le User pour l'envoyer dans la requête.
     *
     * @param user
     */
    private void setUpNewUser(FirebaseUser user) {

//        Log.w("Auth Server Request", "setUpNewUser()");

        // Setting up the new User
        User newUser = new User();

        Karma karma = new Karma(0);
        ArrayList<String> regToken = new ArrayList<>();
        regToken.add(MyFirebaseMessagingService.getRegistrationToken());
        String uId = user.getUid();

        newUser.setDateCreation(Server.getCurrentTimeFormatted());
        newUser.setuId(uId);
        newUser.setKarma(karma);
        newUser.setRegistrationToken(regToken);

        // Prompt for the username
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Entrez votre nom d'utilisateur");
        builder.setCancelable(false);

        // Set up the input
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);
        InputFilter[] filterArray = new InputFilter[1];
        filterArray[0] = new InputFilter.LengthFilter(20);
        input.setFilters(filterArray);

        // Set up the buttons
        builder.setNeutralButton("Non merci", (dialog, which) -> {
            newUser.setUserName("UserNameOf_" + uId);
            postAndSyncUser(newUser);
        });
        builder.setPositiveButton("OK", (dialog, which) -> {

            if(input.getText().toString().length() > 0 ) {
                // TODO: intégrer une vérification pour que les 'userName' soient uniques?
                // TODO: Prevent "official usernames" (SOPFEU, Environnement Canada)
                newUser.setUserName(input.getText().toString());
            } else {
                newUser.setUserName("UserNameOf_" + uId);
            }
            postAndSyncUser(newUser);
        });

        // Show dialog
        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(d -> {
            InputMethodManager inM = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inM.showSoftInput(input, InputMethodManager.SHOW_IMPLICIT);
        });
        dialog.show();
    }

    /**
     * S'occupe aussi d'instancier le "User" de l'App.
     * Si le serveur ne renvoit pas de User, alors un POST est effectué (Google account).
     *
     * @param uId
     */
    private void getUserOrPostRequest(String uId, FirebaseUser fbUser) {
        final String TAG = "Auth Server Request";

        HttpRequest<User> getUser = GetRequest.user(uId);
//        Log.w(TAG, "getUser() : User request uid = " + uId);
        new RequestHandler<>(getUser,
                response -> {
//                    Log.w(TAG, "Server response = " + response.toString());

                    User clonedUser = cloneCurrentUser(response);
                    addCurrentRegTok(clonedUser); // saves the PATCHED User in memory

                    App.getInstance().getSetSaveMzRequest(fbUser.getUid(), getApplicationContext());
                },
                error -> {
                    // When the User doesn't exist in our DB but exists in the Firebase DB => Google SignIn (Create account)
                    if(error.getCode() == 400)
                        setUpNewUser(fbUser);
                }
        ).handle(Server.AuthorizationHeaders.REQUIRED);
    }


    /**
     * Only for patching the registrationToken list.
     *
     * @param newUser the user to be saved if the PATCH works
     */
    private void patchRegTokAndSyncUser(List listToMap, User newUser, boolean fromRemove, String idToken) {

        Map<String, Object> body = new HashMap<>();
        body.put("registrationToken", listToMap);

        HttpRequest<Map> patchedUser = PatchRequest.user(body, newUser.getuId());
        RequestHandler<Map> handler = new RequestHandler<>(patchedUser,
                response -> {
                    App.getInstance().setCurrentUser(newUser, true, this);

                    if(fromRemove) {
                        App.getInstance().setCurrentUser(null, false, this);
                    }

                    //updateUI(mAuth.getCurrentUser()); TODO: reintegrate once "Synchro en cours" is done
                },
                error -> {
                    ;
//                    Log.w("patchRegTokAndSyncUser","failure on the PATCH :(");
                    //updateUI(mAuth.getCurrentUser()); TODO: reintegrate once "Synchro en cours" is done
                    // TODO : Error handling
                }
        );

        if(fromRemove) {
            handler.setHardJWT(idToken);
            handler.handle(Server.AuthorizationHeaders.FORCED);
        } else {
            handler.handle(Server.AuthorizationHeaders.REQUIRED);
        }
    }


    /**
     * When signing in.
     *
     * @param clonedUser
     */
    private void addCurrentRegTok(User clonedUser) {

        List<String> listToMap = clonedUser.getRegistrationToken();
        String currRegTok = MyFirebaseMessagingService.getRegistrationToken();
//        Log.w("addCurrentRegTok", currRegTok);


        for (int i = 0; i < listToMap.size(); i++) {
            if (listToMap.get(i).equals(currRegTok)) { // Pour ne pas ajouter deux fois le même regTok (simple précaution)
                patchRegTokAndSyncUser(listToMap, clonedUser, false, null);
                return;
            }
        }

//        Log.w("addCurrentRegTok", "no duplicate found");

        listToMap.add(currRegTok);

        patchRegTokAndSyncUser(listToMap, clonedUser, false, null);
    }

    /**
     * When signing out.
     *
     * @param clonedUser
     */
    private void removeCurrentRegTok(User clonedUser, String idToken) {

        List<String> listToMap = clonedUser.getRegistrationToken();
        String currRegTok = MyFirebaseMessagingService.getRegistrationToken();
//        Log.w("removeCurrentRegTok", currRegTok);

        for (int i = 0; i < listToMap.size(); i++) {
            if (listToMap.get(i).equals(currRegTok))
                listToMap.remove(i);
        }

        patchRegTokAndSyncUser(listToMap, clonedUser, true, idToken);
    }

    /**
     * To prevent modifying User info before the server response
     *
     * @param user
     * @return
     */
    private User cloneCurrentUser(@NonNull User user) {

        User newUser = new User();

        newUser.setDateCreation(user.getDateCreation());
        newUser.setUserName(user.getUserName());
        newUser.setuId(user.getuId());
        newUser.setKarma(user.getKarma());
        newUser.setRegistrationToken(user.getRegistrationToken());

        return newUser;
    }












    /*
    SHARED BETWEEN ALL !!
     */

    /**
     * Used for SignIn flow.
     * If 'task' is successful, then it means the person is now Signed In.
     *
     * @param task trying to sign in
     */
    private void signTaskOnCompleteListener(Task task) {
        task.addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    // GET UserRequest : Constructing the "UserRequest" instance for the App
                    FirebaseUser fbUser = FirebaseAuth.getInstance().getCurrentUser();
                    String uId = fbUser.getUid();
                    getUserOrPostRequest(uId, fbUser);

                    hideKeyboard(findViewById(R.id.email));
                    updateUI(mAuth.getCurrentUser()); //TODO: can be replaced with a "Synchronisation en cours"
                } else {
                    // If sign in fails, drawZones a message to the user.
                    Snackbar.make(findViewById(R.id.auth_logo_acclimate),
                            "Une erreur s'est produite.",
                            Snackbar.LENGTH_SHORT).show();
                    updateUI(null);
                }

                hideProgressDialog();
            }
        });
    }

    private void signOut() {
        UserController.getUserJWT(task -> {
            if (task.isSuccessful()) {
                final String idToken = task.getResult().getToken();

                mAuth.signOut();

                // also used by the Email-method signOut procedure
                mGoogleSignInClient.signOut().addOnCompleteListener(this,
                        (taskOnComplete) -> {
                                App myApp = App.getInstance();

                                User clonedUser = cloneCurrentUser(myApp.getCurrentUser());
                                removeCurrentRegTok(clonedUser, idToken); // saves the PATCHED User in memory

                                myApp.setCurrentUser(null, false, getApplicationContext()); // as a precaution
                                myApp.clearCurrentUserZones();
                                updateUI(mAuth.getCurrentUser()); //TODO: can be replaced with a "Synchronisation en cours"
                            }
                        );
            } else {
                // reverting the UI's logic
                hasSelectedAuthMethod = true;
            }
        });


    }






    /*
    GOOGLE AUTHENTIFICATION !!!
     */

    private void signInGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    /**
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach a listener
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                updateUI(null);
            }
        }
    }

    /**
     * Called to begin the Google Authentification.
     * Can be used to adapt the UI based on the response.
     *
     * @param acct the signed in account
     */
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        showProgressDialog();

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        signTaskOnCompleteListener(mAuth.signInWithCredential(credential));
    }






    /*
    USER/PASSWORD AUTH !!! (No google)
     */

    /**
     * Pour créer un compte via la méthode Email/Password
     *
     * @param email    email
     * @param password password
     */
    private void createAccount(String email, String password) {
        if (!emailAuthProcesses.validateForm())
            return;

        showProgressDialog();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) { // Sign in success

                            FirebaseUser user = mAuth.getCurrentUser();
                            user.sendEmailVerification();
                            hideKeyboard(findViewById(R.id.email));

                            // POST User request
                            setUpNewUser(user);

                        } else {
                            // If sign in fails, drawZones a message to the user.
                            Snackbar.make(findViewById(R.id.auth_logo_acclimate),
                                    "Une erreur s'est produite.",
                                    Snackbar.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        hideProgressDialog();
                    }
                });
    }

    /**
     * Pour Sign In avec la méthode "Email/Password"
     *
     * @param email    email
     * @param password password
     */
    private void signInEmail(String email, String password) {
        if (!emailAuthProcesses.validateForm())
            return;

        showProgressDialog();
        signTaskOnCompleteListener(mAuth.signInWithEmailAndPassword(email, password));
    }
}