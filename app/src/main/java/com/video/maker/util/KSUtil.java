package com.video.maker.util;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.View;

import java.util.ArrayList;


public class KSUtil {


    public static ArrayList<Integer> Filterposs = new ArrayList<>();
    public static ArrayList<Integer> Transitionposs = new ArrayList<>();
    public static ArrayList<Integer> Musicposs = new ArrayList<>();



    public static AnimatorSet InDown(View view){
        AnimatorSet animatorSet = new AnimatorSet();
        long height = - view.getHeight();

        ObjectAnimator object1 =   ObjectAnimator.ofFloat(view,  "alpha", 0, 1, 1, 1);
        ObjectAnimator object2 =   ObjectAnimator.ofFloat(view,  "translationY", height, 30, -10, 0);

        animatorSet.playTogether(object1, object2);
        return animatorSet;
    }

    public static AnimatorSet ZoomIn(View view) {
        AnimatorSet animatorSet = new AnimatorSet();

        ObjectAnimator object1 = ObjectAnimator.ofFloat(view,  "scaleX", 0.45f, 1);
        ObjectAnimator object2 = ObjectAnimator.ofFloat(view,  "scaleY", 0.45f, 1);
        ObjectAnimator object3 = ObjectAnimator.ofFloat(view,   "alpha", 0, 1);

        animatorSet.playTogether(object1, object2, object3);
        return animatorSet;
    }



}
