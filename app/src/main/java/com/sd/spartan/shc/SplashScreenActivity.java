package com.sd.spartan.shc;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.sd.spartan.shc.activity.CameraViewActivity;
import com.sd.spartan.shc.constants.Constraints;

import java.util.Random;

@SuppressLint("CustomSplashScreen")
public class SplashScreenActivity extends AppCompatActivity {
    private boolean action = false;
    private final int[] images = {R.drawable.image001, R.drawable.image002, R.drawable.image003,
            R.drawable.image004, R.drawable.image005, R.drawable.image006, R.drawable.image007, R.drawable.image008, R.drawable.image009, };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash_screen);

        ImageView mSnakeImage = findViewById(R.id.image_snake);
        TextView mSnakeText = findViewById(R.id.text_main_snake);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        final int min = 0;
        final int max = 8;
        final int random = new Random().nextInt((max - min) + 1) + min;
        mSnakeImage.setImageResource(images[random]);

        Animation animation =  AnimationUtils.loadAnimation(getApplicationContext(), R.anim.down_move);
        mSnakeImage.getLayoutParams().width = width-100 ;
        mSnakeImage.getLayoutParams().height = ((height/2)-130) ;
        mSnakeImage.requestLayout();
        mSnakeImage.startAnimation(animation);
        Animation animation2 =  AnimationUtils.loadAnimation(getApplicationContext(), R.anim.up_move);
        mSnakeText.startAnimation(animation2);

        new Handler().postDelayed(() -> {
            if(!action){
                goActivity();
            }
        }, Constraints.SPLASH_TIME);

    }

    private void goActivity() {
        Intent mySuperIntent = new Intent(getApplicationContext(), CameraViewActivity.class);
        startActivity(mySuperIntent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Constraints.SPLASH_TIME = 0 ;
        action = true ;
    }


    @Override
    protected void onStop() {
        super.onStop();
        Constraints.SPLASH_TIME = 0 ;
        action = true ;
    }

    @Override
    protected void onPause() {
        super.onPause();
        Constraints.SPLASH_TIME = 0 ;
        action = true;
        finish();
    }
}