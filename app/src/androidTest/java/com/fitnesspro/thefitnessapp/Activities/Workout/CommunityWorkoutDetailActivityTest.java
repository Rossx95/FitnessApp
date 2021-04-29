package com.fitnesspro.thefitnessapp.Activities.Workout;

import android.app.Activity;
import android.app.Instrumentation;
import android.view.View;

import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.rule.ActivityTestRule;

import com.fitnesspro.thefitnessapp.Activities.CommunityWorkout.CommunityWorkoutActivity;
import com.fitnesspro.thefitnessapp.Activities.CommunityWorkout.CommunityWorkoutDetailView;
import com.fitnesspro.thefitnessapp.Activities.CommunityWorkout.ReviewView;
import com.fitnesspro.thefitnessapp.R;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.InstrumentationRegistry.getInstrumentation;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.hasErrorText;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertNotNull;

public class CommunityWorkoutDetailActivityTest extends CommunityWorkoutActivityTest {
    @Rule
    public ActivityTestRule<CommunityWorkoutActivity> mActivityTestRule = new ActivityTestRule<CommunityWorkoutActivity>(CommunityWorkoutActivity.class);

    Instrumentation.ActivityMonitor exeDetailMonitor = getInstrumentation().addMonitor(WorkoutExeActivity.class.getName(), null, false);
    Instrumentation.ActivityMonitor reviewMonitor = getInstrumentation().addMonitor(ReviewView.class.getName(), null, false);
    private CommunityWorkoutActivity mActivity = null;
    private WorkoutExeActivity eActivity = null;
    private String exeName = "Chest";

    @Before
    public void setUp() throws Exception {
        mActivity = mActivityTestRule.getActivity();
    }
    @Test
    public void loadWorkoutAndAddReview() throws InterruptedException {
        OnCommunityWorkoutClick();
        onView(withId(R.id.review_btn)).perform(click());
        onView(withText("Please enter description")).check(matches(isDisplayed()));
        onView(withId(R.id.description)).perform(typeText("Test"));
        onView(withId(R.id.rating)).perform(click());
        onView(withId(R.id.review_btn)).perform(click());
    }

    @Test
    public void alreadyReviewedWorkout() throws InterruptedException {
        loadWorkoutAndAddReview();
        onView(withText("You already reviewed this workout before!")).
                inRoot(withDecorView(not(is(mActivity.getWindow().getDecorView())))).
                check(matches(isDisplayed()));
    }
    @Test
    public void loadExerciseToLog() throws InterruptedException {
        OnCommunityWorkoutClick();
        onView(withId(R.id.detail_list)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        Activity exeDetail = getInstrumentation().waitForMonitorWithTimeout(exeDetailMonitor,5000);
        //open register screen
        assertNotNull(exeDetail);
    }
    @Test
    public void logExercise() throws InterruptedException {
        loadExerciseToLog();
        onView(withId(R.id.log_btn)).perform(click());
    }
}