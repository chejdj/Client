package com.aunt.stealen;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;


public class weclome extends Activity {
  private Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        View rootView = LayoutInflater.from(this).inflate(R.layout.weclome, null);
        rootView.requestLayout();
        setContentView(rootView);
        handler= new Handler();
        Animation animation = AnimationUtils.loadAnimation(this,R.anim.weclome);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Intent i = new Intent(weclome.this, Login.class);
                        startActivity(i);
                        weclome.this.finish();
                    }
                });
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
                    });
                    rootView.startAnimation(animation);
                    }
                    }
