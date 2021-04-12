package com.fitnesspro.thefitnessapp.Activities.Main;

import androidx.cardview.widget.CardView;
import androidx.drawerlayout.widget.DrawerLayout;

import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.fitnesspro.thefitnessapp.Activities.CommunityWorkout.CommunityWorkoutActivity;
import com.fitnesspro.thefitnessapp.Activities.Exe.ExerciseListActivity;
import com.fitnesspro.thefitnessapp.Activities.History.HistoryActivity;
import com.fitnesspro.thefitnessapp.Activities.Workout.WorkoutActivity;
import com.fitnesspro.thefitnessapp.Base.BaseActivity;
import com.fitnesspro.thefitnessapp.R;
import com.fitnesspro.thefitnessapp.Activities.User.LoginActivity;
import com.fitnesspro.thefitnessapp.Activities.User.ProfileActivity;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends BaseActivity {
    DrawerLayout dLayout;
    ImageView menu_btn;
    //ActionBarDrawerToggle toggle;
    CardView profileCard, workoutCard, communityWorkoutCard, presetCard, exerciseCard, historyCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setNavigationDrawer(); // call method
        initView();
    }
    private void initView(){
        menu_btn = findViewById(R.id.menu);
        profileCard = findViewById(R.id.profilecard);
        workoutCard = findViewById(R.id.workoutcard);
        presetCard = findViewById(R.id.presetcard);
        historyCard = findViewById(R.id.historycard);
        communityWorkoutCard = findViewById(R.id.communityworkoutcard);
        exerciseCard = findViewById(R.id.exercisecard);
        setNavigationDrawer();

        menu_btn.setOnClickListener(this);
        profileCard.setOnClickListener(this);
        presetCard.setOnClickListener(this);
        exerciseCard.setOnClickListener(this);
        workoutCard.setOnClickListener(this);
        communityWorkoutCard.setOnClickListener(this);
        historyCard.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        if(view == profileCard){
            startIntentLiveMode(ProfileActivity.class);
        }else if(view == presetCard){
            //startIntentLiveMode(PresetWorkoutActivity.class);
        }else if(view == exerciseCard){
            startIntentLiveMode(ExerciseListActivity.class);
        }else if(view == workoutCard){
            startIntentLiveMode(WorkoutActivity.class);
        }else if(view == communityWorkoutCard){
            startIntentLiveMode(CommunityWorkoutActivity.class);
        }else if(view == historyCard){
            startIntentLiveMode(HistoryActivity.class);
        }else if(view == menu_btn){
            openDrawerMenu();
        }
    }
    private void openDrawerMenu() {
        if (!dLayout.isDrawerOpen(Gravity.LEFT)) {
            dLayout.openDrawer(Gravity.LEFT);
        }
    }
    private void closeDrawerMenu(){
        if (dLayout.isDrawerOpen(Gravity.LEFT)) {
            dLayout.closeDrawer(Gravity.LEFT);
        }
    }


    private void setNavigationDrawer() {
        dLayout = findViewById(R.id.drawer_layout); // initiate a DrawerLayout
        NavigationView navView = (NavigationView) findViewById(R.id.navigation); // initiate a Navigation View
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                int itemId = menuItem.getItemId(); // get selected menu item's id
                closeDrawerMenu();
                if (itemId == R.id.profile) {
                    startIntentLiveMode(ProfileActivity.class);
                } else if (itemId == R.id.workout) {
                    startIntentLiveMode(WorkoutActivity.class);
                } else if (itemId == R.id.CommunityWorkouts) {
                    startIntentLiveMode(CommunityWorkoutActivity.class);
                } else if (itemId == R.id.logout) {
                    auth.signOut();
                    startIntentLiveMode(LoginActivity.class);
                }
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }
}
