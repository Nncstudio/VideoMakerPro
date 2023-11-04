package com.video.maker.util;

import android.animation.AnimatorSet;
import android.content.Context;
import android.view.animation.AccelerateDecelerateInterpolator;

public class Render {
    private long duration = 1000;
    private Context cx;
    private AnimatorSet animatorSet;

    public Render(Context cx) {
        this.cx = cx;
    }

    public void setAnimation (AnimatorSet animationSet) {
        this.animatorSet = animationSet;
    }

    public void start() {
        animatorSet.setDuration(duration);
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animatorSet.start();
    }

    public void setDuration(long duration){
        this.duration = duration;
    }


}
