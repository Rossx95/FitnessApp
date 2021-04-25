package com.fitnesspro.thefitnessapp.Activities.Main;

import android.os.Bundle;
import android.os.CountDownTimer;

import com.fitnesspro.thefitnessapp.Base.BaseActivity;
import com.fitnesspro.thefitnessapp.R;
import com.fitnesspro.thefitnessapp.Activities.User.LoginActivity;
import com.fitnesspro.thefitnessapp.models.Params;

public class SplashActivity extends BaseActivity {
    //initialising variable to time length of 2 seconds
    private final int SPLASH_DISPLAY_LENGTH = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //set the base view for the activity
        setContentView(R.layout.activity_splash);
        //calling start method
        start();
    }
    private void start(){
        //initialising a timer for the splash screen
        new CountDownTimer(SPLASH_DISPLAY_LENGTH, 1000){
            public void onTick(long millisUntilFinished){
            }
            public  void onFinish(){
                //once the splash has finished displaying, check the users login status
                startView();
            }
            //begin the timer
        }.start();
    }
    //Method to assess if user is logged in, bring them directly to main page,
    //if not bring them to the login page. If the user is an admin, enable admin rights
    private void startView(){
    //if a user is logged in, redirect them to the main activity
        if (auth.getCurrentUser() != null) {
            if(auth.getCurrentUser().getEmail().equals("admin@gmail.com")){
                Params.ADMIN = true;
            }
            startIntentAsFinishMode(MainActivity.class);
        }
        //if a user is not logged in, redirect them to the login activity
        else{
            startIntentAsFinishMode(LoginActivity.class);
        }
    }
}