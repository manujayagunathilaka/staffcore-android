package lk.businessmanagement.staffcore.utils;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.view.View;

public class AnimationHelper {

    public static void animateBackground(View glowTop, View glowBottom) {
        if (glowTop != null) {
            ObjectAnimator animatorTop = ObjectAnimator.ofFloat(glowTop, "translationX", 0f, 100f);
            animatorTop.setDuration(4000);
            animatorTop.setRepeatCount(ValueAnimator.INFINITE);
            animatorTop.setRepeatMode(ValueAnimator.REVERSE);
            animatorTop.start();
        }

        if (glowBottom != null) {
            ObjectAnimator animatorBottom = ObjectAnimator.ofFloat(glowBottom, "translationX", 0f, -100f);
            animatorBottom.setDuration(5000);
            animatorBottom.setRepeatCount(ValueAnimator.INFINITE);
            animatorBottom.setRepeatMode(ValueAnimator.REVERSE);
            animatorBottom.start();
        }
    }
}