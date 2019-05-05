package com.acclimate.payne.simpletestapp.animator;

import android.app.Activity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.acclimate.payne.simpletestapp.R;

public class MyAnimator {

    private Activity activity;

    public MyAnimator(Activity activity) {
        this.activity = activity;
    }

    /**
     * A fast shaking animation.
     * @param view
     */
    public void fastShakingAnimation(View view) {
        Animation animation = AnimationUtils.loadAnimation(activity, R.anim.shake);
        //Following 2 lines make sure the animation is reset when starting again.
        animation.reset();
        view.clearAnimation();
        view.startAnimation(animation);
    }
}
