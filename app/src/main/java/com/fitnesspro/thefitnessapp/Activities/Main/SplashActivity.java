package com.fitnesspro.thefitnessapp.Activities.Main;

import android.os.Bundle;
import android.os.CountDownTimer;

import com.fitnesspro.thefitnessapp.Base.BaseActivity;
import com.fitnesspro.thefitnessapp.R;
import com.fitnesspro.thefitnessapp.Activities.User.LoginActivity;
import com.fitnesspro.thefitnessapp.models.Params;

public class SplashActivity extends BaseActivity {

    private final int SPLASH_DISPLAY_LENGTH = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        start();
    }
    private void start(){
        new CountDownTimer(SPLASH_DISPLAY_LENGTH, 1000){
            public void onTick(long millisUntilFinished){
            }
            public  void onFinish(){
                startView();
            }
        }.start();
    }
    private void startView(){
        if (auth.getCurrentUser() != null) {
            if(auth.getCurrentUser().getEmail().equals("admin@gmail.com")){
                Params.ADMIN = true;
            }
            startIntentAsFinishMode(MainActivity.class);
        }else{
            startIntentAsFinishMode(LoginActivity.class);
        }
    }
}