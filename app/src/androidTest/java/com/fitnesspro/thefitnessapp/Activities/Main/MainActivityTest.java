package com.fitnesspro.thefitnessapp.Activities.Main;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;

import androidx.test.espresso.action.GeneralLocation;
import androidx.test.espresso.action.GeneralSwipeAction;
import androidx.test.espresso.action.Press;
import androidx.test.espresso.action.ScrollToAction;
import androidx.test.espresso.action.Swipe;
import androidx.test.espresso.action.ViewActions;
import androidx.test.rule.ActivityTestRule;

import com.fitnesspro.thefitnessapp.Activities.CommunityWorkout.CommunityWorkoutActivity;
import com.fitnesspro.thefitnessapp.Activities.Exe.ExerciseListActivity;
import com.fitnesspro.thefitnessapp.Activities.History.HistoryActivity;
import com.fitnesspro.thefitnessapp.Activities.User.ProfileActivity;
import com.fitnesspro.thefitnessapp.Activities.Workout.WorkoutActivity;
import com.fitnesspro.thefitnessapp.R;

import androidx.test.espresso.ViewAction;


import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.InstrumentationRegistry.getInstrumentation;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.*;


public class MainActivityTest {
    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<MainActivity>(MainActivity.class);

    private MainActivity mActivity = null;

    Instrumentation.ActivityMonitor profileMonitor = getInstrumentation().addMonitor(ProfileActivity.class.getName(),null,false);
    Instrumentation.ActivityMonitor exerciseMonitor = getInstrumentation().addMonitor(ExerciseListActivity.class.getName(),null,false);
    Instrumentation.ActivityMonitor workoutMonitor = getInstrumentation().addMonitor(WorkoutActivity.class.getName(),null,false);
    Instrumentation.ActivityMonitor communityWorkoutMonitor = getInstrumentation().addMonitor(CommunityWorkoutActivity.class.getName(),null,false);
    Instrumentation.ActivityMonitor historyMonitor = getInstrumentation().addMonitor(HistoryActivity.class.getName(),null,false);
    Instrumentation.ActivityMonitor navDrawerMonitor = getInstrumentation().addMonitor(MainActivity.class.getName(),null,false);

    @Before
    public void setUp() throws Exception
    {
        mActivity = mActivityTestRule.getActivity();
    }
    @Test
    public void testMainActivityLaunch()
    {
        Intent expectedIntent = new Intent(mActivity, MainActivity.class);
        assertNotNull(expectedIntent);
    }

    @Test
    public void testProfileLaunch(){
        //set the activity to the register button
        assertNotNull(mActivity.findViewById(R.id.profilecard));
        //perform a click on the register button
        onView(withId(R.id.profilecard)).perform(click());
        Activity profileActivity = getInstrumentation().waitForMonitorWithTimeout(profileMonitor,5000);
        //open register screen
        assertNotNull(profileActivity);
        profileActivity.finish();
    }

    @Test
    public void testExerciseLaunch(){
        //set the activity to the register button
        assertNotNull(mActivity.findViewById(R.id.exercisecard));
        //perform a click on the register button
        onView(withId(R.id.exercisecard)).perform(click());
        Activity exerciseActivity = getInstrumentation().waitForMonitorWithTimeout(exerciseMonitor,5000);
        //open register screen
        assertNotNull(exerciseActivity);
        exerciseActivity.finish();
    }

    @Test
    public void testWorkoutLaunch(){
        //set the activity to the register button
        assertNotNull(mActivity.findViewById(R.id.workoutcard));
        //perform a click on the register button
        onView(withId(R.id.workoutcard)).perform(click());
        Activity workoutActivity = getInstrumentation().waitForMonitorWithTimeout(workoutMonitor,5000);
        //open register screen
        assertNotNull(workoutActivity);
        workoutActivity.finish();
    }

    @Test
    public void testCommunityWorkoutLaunch(){
        //set the activity to the register button
        assertNotNull(mActivity.findViewById(R.id.communityworkoutcard));
        //perform a click on the register button
        onView(withId(R.id.communityworkoutcard)).perform(click());
        Activity communityWorkoutActivity = getInstrumentation().waitForMonitorWithTimeout(communityWorkoutMonitor,5000);
        //open register screen
        assertNotNull(communityWorkoutActivity);
        communityWorkoutActivity.finish();
    }

    @Test
    public void testHistoryLaunch(){
        //set the activity to the register button
        assertNotNull(mActivity.findViewById(R.id.historycard));
        //perform a click on the register button
        onView(withId(R.id.communityworkoutcard)).perform(swipeUp());
        onView(withId(R.id.presetcard)).perform(swipeUp());
        onView(withId(R.id.historycard)).perform(click());
        Activity historyActivity = getInstrumentation().waitForMonitorWithTimeout(historyMonitor,5000);
        //open register screen
        assertNotNull(historyActivity);
        historyActivity.finish();
    }

    public static ViewAction swipeUp()
    { return new GeneralSwipeAction(Swipe.FAST,
            GeneralLocation.BOTTOM_CENTER, GeneralLocation.TOP_CENTER, Press.FINGER); }

    public static ViewAction swipeDown() { return new GeneralSwipeAction(Swipe.FAST,
            GeneralLocation.TOP_CENTER, GeneralLocation.BOTTOM_CENTER, Press.FINGER); }
}
