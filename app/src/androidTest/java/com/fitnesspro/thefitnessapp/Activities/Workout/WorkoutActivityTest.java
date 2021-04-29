package com.fitnesspro.thefitnessapp.Activities.Workout;

import android.app.Activity;
import android.app.Instrumentation;
import android.view.View;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.rule.ActivityTestRule;

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
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertNotNull;

public class WorkoutActivityTest {
    @Rule
    public ActivityTestRule<WorkoutActivity> mActivityTestRule = new ActivityTestRule<WorkoutActivity>(WorkoutActivity.class);

    Instrumentation.ActivityMonitor workoutDetailMonitor = getInstrumentation().addMonitor(WorkoutDetailActivity.class.getName(), null, false);

    private WorkoutActivity mActivity = null;
    private String exeName = "Chest";

    @Before
    public void setUp() throws Exception {
        mActivity = mActivityTestRule.getActivity();
    }

    @Test
    public void OnWorkoutClick() throws InterruptedException {
        Thread.sleep(2000);
        onView(withId(R.id.workout_list)).perform(RecyclerViewActions.actionOnItemAtPosition(
                                1,
                                new ViewAction() {
                                    @Override
                                    public Matcher<android.view.View> getConstraints() {
                                        return null;
                                    }

                                    @Override
                                    public String getDescription() {
                                        return "Click on specific button";
                                    }

                                    @Override
                                    public void perform(UiController uiController, android.view.View view) {
                                        View button = view.findViewById(R.id.go_btn);
                                        // Maybe check for null
                                        button.performClick();
                                    }
                                })
                );
        Activity workoutDetail = getInstrumentation().waitForMonitorWithTimeout(workoutDetailMonitor,5000);
        //open register screen
        assertNotNull(workoutDetail);
        workoutDetail.finish();
    }
    @Test
    public void OnEditClick() throws InterruptedException {
        Thread.sleep(2000);
        onView(withId(R.id.workout_list)).perform(RecyclerViewActions.actionOnItemAtPosition(
                1,
                new ViewAction() {
                    @Override
                    public Matcher<android.view.View> getConstraints() {
                        return null;
                    }

                    @Override
                    public String getDescription() {
                        return "Click on specific button";
                    }

                    @Override
                    public void perform(UiController uiController, android.view.View view) {
                        View button = view.findViewById(R.id.edit_btn);
                        // Maybe check for null
                        button.performClick();
                    }
                })
        );
        Thread.sleep(2000);
        onView(withText("Please update workout name")).check(matches(isDisplayed()));
    }

    @Test
    public void addWorkout() throws InterruptedException {
        Thread.sleep(2000);
        onView(withId(R.id.add_btn)).perform(click());
        Thread.sleep(2000);
        onView(withText("Please enter workout name")).check(matches(isDisplayed()));
        Espresso.onView(withId(R.id.name)).perform(typeText(exeName));
        Espresso.onView(withId(R.id.create_btn)).perform(click());
        onView(withText("Successfully created workout!")).
                inRoot(withDecorView(not(is(mActivity.getWindow().getDecorView())))).
                check(matches(isDisplayed()));
    }
}
