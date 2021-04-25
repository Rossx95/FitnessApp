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
    //Initialising Variables
    DrawerLayout dLayout;
    ImageView menu_btn;
    CardView profileCard, workoutCard, communityWorkoutCard, presetCard, exerciseCard, historyCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //set the base view for the activity
        setContentView(R.layout.activity_main);
        //call navigation drawer method
        setNavigationDrawer();
        //call initView method
        initView();
    }
    private void initView(){
        //initialise variables to features on the page
        menu_btn = findViewById(R.id.menu);
        profileCard = findViewById(R.id.profilecard);
        workoutCard = findViewById(R.id.workoutcard);
        presetCard = findViewById(R.id.presetcard);
        historyCard = findViewById(R.id.historycard);
        communityWorkoutCard = findViewById(R.id.communityworkoutcard);
        exerciseCard = findViewById(R.id.exercisecard);
        setNavigationDrawer();
        //Set on click listener for buttons to pick up in the onClick method
        menu_btn.setOnClickListener(this);
        profileCard.setOnClickListener(this);
        presetCard.setOnClickListener(this);
        exerciseCard.setOnClickListener(this);
        workoutCard.setOnClickListener(this);
        communityWorkoutCard.setOnClickListener(this);
        historyCard.setOnClickListener(this);
    }
    //method to set events for various buttons on the home screen
    @Override
    public void onClick(View view) {
        super.onClick(view);
        if(view == profileCard){
            startIntentLiveMode(ProfileActivity.class);
        }else if(view == exerciseCard){
            startIntentLiveMode(ExerciseListActivity.class);
        }else if(view == workoutCard){
            startIntentLiveMode(WorkoutActivity.class);
        }else if(view == communityWorkoutCard){
            startIntentLiveMode(CommunityWorkoutActivity.class);
        }else if(view == historyCard){
            startIntentLiveMode(HistoryActivity.class);
        }else if(view == presetCard){
            //startIntentLiveMode(PresetWorkoutActivity.class);
        }else if(view == menu_btn){
            openDrawerMenu();
        }
    }
    //Method to open the menu navigation
    private void openDrawerMenu() {
        if (!dLayout.isDrawerOpen(Gravity.LEFT)) {
            dLayout.openDrawer(Gravity.LEFT);
        }
    }
    //Method to close the menu navigation
    private void closeDrawerMenu(){
        if (dLayout.isDrawerOpen(Gravity.LEFT)) {
            dLayout.closeDrawer(Gravity.LEFT);
        }
    }

    //Method to set the menu navigation
    private void setNavigationDrawer() {
        //initiate a DrawerLayout
        dLayout = findViewById(R.id.drawer_layout);
        //Instantiating a Navigation View
        NavigationView navView = (NavigationView) findViewById(R.id.navigation);
        //setting a listener for the menu, on click each item will go to their
        //corresponding activities
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

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        return super.onOptionsItemSelected(item);
//    }
}
