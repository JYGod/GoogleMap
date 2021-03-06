package com.edu.hrbeu.googlemap.utils;


import android.animation.ObjectAnimator;
import android.graphics.Point;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

public class AnimUtil {

    public static void animateJump(View target){

        int delay = 380;
        ObjectAnimator jump1_up = ObjectAnimator.ofFloat( target, "translationY", 0f, -50f );
        jump1_up.setDuration(300);
        jump1_up.start();

        ObjectAnimator jump1_down = ObjectAnimator.ofFloat( target, "translationY", -50f, 0f );
        jump1_down.setDuration(400);
        jump1_down.setStartDelay(delay);// 滞空時間を考慮してちょっとあとに落ちてくる
        jump1_down.start();


        /**
         *   ObjectAnimator jump2_up = ObjectAnimator.ofFloat( target, "translationY", 0f, -40f );
         jump2_up.setDuration(500);
         //jump2_up.setStartDelay(delay);
         jump2_up.start();

         ObjectAnimator jump2_down = ObjectAnimator.ofFloat( target, "translationY", -40f, 0f );
         jump2_down.setDuration(500);
         delay += 70;
         jump2_down.setStartDelay(delay);// 滞空時間を考慮してちょっとあとに落ちてくる
         jump2_down.start();
         */


    }

}
