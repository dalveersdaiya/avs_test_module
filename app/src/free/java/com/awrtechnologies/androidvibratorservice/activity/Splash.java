package com.awrtechnologies.androidvibratorservice.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.DecelerateInterpolator;

import com.awrtechnologies.androidvibratorservice.R;
import com.sdsmdg.harjot.rotatingtext.RotatingTextWrapper;
import com.sdsmdg.harjot.rotatingtext.models.Rotatable;

public class Splash extends AppCompatActivity {
    RotatingTextWrapper rotatingTextWrapper;
    Rotatable rotatable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_splash);
        setTextAnimation ();
        new Handler ().postDelayed (new Runnable () {
            @Override
            public void run() {
                Intent intent = new Intent (Splash.this, Main2Activity.class);
                intent.setFlags (Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity (intent);
            }
        }, 4000);
    }

    public void setTextAnimation() {
        Typeface typeface = Typeface.createFromAsset (getAssets (), "fonts/Raleway-Light.ttf");

        rotatingTextWrapper = (RotatingTextWrapper) findViewById (R.id.custom_switcher);
        rotatingTextWrapper.setTypeface (typeface);
        rotatingTextWrapper.setSize (25);

        rotatable = new Rotatable (Color.parseColor ("#FF4081"), 1000, "Solutions", "Customizations", "Power");
        rotatable.setTypeface (typeface);
        rotatable.setSize (25);
        rotatable.setAnimationDuration (500);
        rotatable.setInterpolator (new DecelerateInterpolator ());

        rotatingTextWrapper.setContent ("", rotatable);
    }

}
