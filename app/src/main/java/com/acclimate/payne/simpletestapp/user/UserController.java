package com.acclimate.payne.simpletestapp.user;

import com.acclimate.payne.simpletestapp.appUtils.Async;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;

public class UserController {

    @Async
    public static void getUserJWT(OnCompleteListener<GetTokenResult> callback){

        final String TAG = "UserRequest";

        // Getting FirebaseUser instance as it should already be authenticated
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        try {
            user.getIdToken(false).addOnCompleteListener(callback);
        } catch (Exception e) {
//            Log.w(TAG + " Auth Token", "getIdToken might have returned null? : " + e.getMessage());
            e.printStackTrace();
        }
    }


}



/*
    public void onComplete(@NonNull Task<GetTokenResult> task) {
        if (task.isSuccessful()) {
            idToken[0] = task.getResult().getToken();
        } else {
            // Handle error -> task.getException();
            Log.w(TAG + " Auth Token", "FAILURE := idToken (OAuth 2) : " +
                    task.getException().getMessage());
            task.getException().printStackTrace();
        }
    }

 */