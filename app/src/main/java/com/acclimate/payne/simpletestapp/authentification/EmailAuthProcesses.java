package com.acclimate.payne.simpletestapp.authentification;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import com.acclimate.payne.simpletestapp.R;
import com.acclimate.payne.simpletestapp.animator.MyAnimator;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Pour séparer un peu de code du MainActivity
 */
public class EmailAuthProcesses extends BaseActivity {

    private AutoCompleteTextView autoCompleteTextView;
    private EditText editText;
    private Button verifyEmailBtn;
    private Activity activity;
    private MyAnimator myAnimator;


    public EmailAuthProcesses(AutoCompleteTextView autoCompleteTextView,
                              EditText editText,
                              Button verifyEmailBtn,
                              Activity activity) {
        this.autoCompleteTextView = autoCompleteTextView;
        this.editText = editText;
        this.verifyEmailBtn = verifyEmailBtn;
        this.activity = activity;
        this.myAnimator = new MyAnimator(activity);
    }


    /**
     * Pour valider l'entrée de User/Passw.
     *
     * @return booléen désignant si la validation est correcte
     */
    public boolean validateForm() {
        boolean valid = true;

        String email = autoCompleteTextView.getText().toString();
        if (TextUtils.isEmpty(email)) {
            autoCompleteTextView.setError("Requis");
            valid = false;
            myAnimator.fastShakingAnimation(autoCompleteTextView);
        } else {
            autoCompleteTextView.setError(null);
        }

        String password = editText.getText().toString();
        if (TextUtils.isEmpty(password)) {
            editText.setError("Requis");
            valid = false;
            myAnimator.fastShakingAnimation(autoCompleteTextView);
        } else {
            editText.setError(null);
        }

        return valid;
    }

    /**
     * Pour envoyer le email de vérification
     */
    public void sendEmailVerification(FirebaseAuth mAuth) {

        // Disable button
        verifyEmailBtn.setEnabled(false);

        // Send verification email
        final FirebaseUser user = mAuth.getCurrentUser();
        user.sendEmailVerification()
                .addOnCompleteListener(activity, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // Re-enable button
                        verifyEmailBtn.setEnabled(true);

                        if (task.isSuccessful()) {
                            Snackbar.make(findViewById(R.id.auth_logo_acclimate),
                                    "Email renvoyé", Snackbar.LENGTH_SHORT).show();
                        } else {
                            Snackbar.make(findViewById(R.id.auth_logo_acclimate),
                                    "Erreur durant l'envoie du email", Snackbar.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
