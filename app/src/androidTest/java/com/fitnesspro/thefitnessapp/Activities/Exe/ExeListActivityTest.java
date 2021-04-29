package com.fitnesspro.thefitnessapp.Activities.Exe;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.widget.SearchView;


import androidx.test.espresso.Espresso;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.rule.ActivityTestRule;

import com.fitnesspro.thefitnessapp.Activities.Exe.ExerciseListActivity;
import com.fitnesspro.thefitnessapp.Activities.User.RegisterActivity;
import com.fitnesspro.thefitnessapp.R;
import com.google.firebase.database.core.view.View;

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
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertNotNull;

public class ExeListActivityTest {

    @Rule
    public ActivityTestRule<ExerciseListActivity> mActivityTestRule = new ActivityTestRule<ExerciseListActivity>(ExerciseListActivity.class);

    private ExerciseListActivity mActivity = null;
    private String search = "Chest";

    Instrumentation.ActivityMonitor exerciseMonitor = getInstrumentation().addMonitor(ExerciseListActivity.class.getName(), null, false);
    Instrumentation.ActivityMonitor exeDetailMonitor = getInstrumentation().addMonitor(ExerciseDetailView.class.getName(), null, false);
    @Before
    public void setUp() throws Exception {
        mActivity = mActivityTestRule.getActivity();
    }

    @Test
    public void testRegisterLaunch() {
        Intent expectedIntent = new Intent(mActivity, RegisterActivity.class);
        assertNotNull(expectedIntent);
    }

    @Test
    public void testSearch() throws InterruptedException {
        Thread.sleep(700);
        onView(withId(R.id.searchView)).perform(click());
        onView(withId(R.id.searchView)).perform(typeText(search));
        onView(withText(R.string.searchBar)).
                inRoot(withDecorView(not(is(mActivity.getWindow().getDecorView())))).
                check(matches(isDisplayed()));
    }
    @Test
    public void OnClick() throws InterruptedException {
        Thread.sleep(2000);
        onView(withId(R.id.exe_list)).perform(RecyclerViewActions.actionOnItemAtPosition(1, click()));
        Activity exeDetail = getInstrumentation().waitForMonitorWithTimeout(exeDetailMonitor,5000);
        //open register screen
        assertNotNull(exeDetail);
        exeDetail.finish();
    }
}
