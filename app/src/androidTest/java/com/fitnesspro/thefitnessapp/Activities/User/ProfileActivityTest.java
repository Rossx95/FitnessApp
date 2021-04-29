package com.fitnesspro.thefitnessapp.Activities.User;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.view.KeyEvent;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.action.ReplaceTextAction;
import androidx.test.rule.ActivityTestRule;

import com.fitnesspro.thefitnessapp.Activities.Main.MainActivity;
import com.fitnesspro.thefitnessapp.R;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.InstrumentationRegistry.getInstrumentation;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.pressKey;
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

public class ProfileActivityTest {
    @Rule
    public ActivityTestRule<ProfileActivity> mActivityTestRule = new ActivityTestRule<ProfileActivity>(ProfileActivity.class);

    private ProfileActivity mActivity = null;
    private String first = "david";
    private String last = "jones";
    private String age = "25";
    private String weight = "180";
    private String height = "183";

    Instrumentation.ActivityMonitor profileMonitor = getInstrumentation().addMonitor(ProfileActivity.class.getName(),null,false);

    @Before
    public void setUp() throws Exception
    {
        mActivity = mActivityTestRule.getActivity();
    }
    @Test
    public void testRegisterLaunch()
    {
        Intent expectedIntent = new Intent(mActivity, ProfileActivity.class);
        assertNotNull(expectedIntent);
    }
    @Test
    public void testProfileUpdate() throws InterruptedException {
        //Updating profile info
        Thread.sleep(2000);
        Espresso.onView(withId(R.id.firstname)).perform(clearText());
        Espresso.onView(withId(R.id.firstname)).perform(typeText(first));
        Espresso.onView(withId(R.id.lastname)).perform(clearText());
        Espresso.onView(withId(R.id.lastname)).perform(typeText(last));
        Espresso.closeSoftKeyboard();
        Espresso.onView(withId(R.id.age)).perform(clearText());
        Espresso.onView(withId(R.id.age)).perform(typeText(age));
        Espresso.closeSoftKeyboard();
        Espresso.onView(withId(R.id.weight)).perform(clearText());
        Espresso.onView(withId(R.id.weight)).perform(typeText(weight));
        Espresso.closeSoftKeyboard();
        Espresso.onView(withId(R.id.height)).perform(clearText());
        Espresso.onView(withId(R.id.height)).perform(typeText(height));
        Espresso.closeSoftKeyboard();
        //set the activity to the login button
        assertNotNull(mActivity.findViewById(R.id.update_button));
        //perform a click on the login button
        onView(withId(R.id.update_button)).perform(click());
        onView(withText(R.string.profile_update_successful)).
                inRoot(withDecorView(not(is(mActivity.getWindow().getDecorView())))).
                check(matches(isDisplayed()));
    }

    @Test
    public void testUpdateWithoutFirstName() throws InterruptedException {
        //Updating profile info without first name to check for error
        Espresso.onView(withId(R.id.firstname)).perform(typeText(first));
        Thread.sleep(2000);
        Espresso.onView(withId(R.id.firstname)).perform(clearText());
        Espresso.onView(withId(R.id.lastname)).perform(clearText());
        Espresso.onView(withId(R.id.lastname)).perform(typeText(last));
        Espresso.closeSoftKeyboard();
        Espresso.onView(withId(R.id.age)).perform(clearText());
        Espresso.onView(withId(R.id.age)).perform(typeText(age));
        Espresso.closeSoftKeyboard();
        Espresso.onView(withId(R.id.weight)).perform(clearText());
        Espresso.onView(withId(R.id.weight)).perform(typeText(weight));
        Espresso.closeSoftKeyboard();
        Espresso.onView(withId(R.id.height)).perform(clearText());
        Espresso.onView(withId(R.id.height)).perform(typeText(height));
        Espresso.closeSoftKeyboard();
        //set the activity to the login button
        assertNotNull(mActivity.findViewById(R.id.update_button));
        //perform a click on the login button
        onView(withId(R.id.update_button)).perform(click());

        ViewInteraction editText = onView(allOf(withId(R.id.firstname), isDisplayed()));
        editText.check(matches(hasErrorText("Please enter your first name!")));
    }
    @Test
    public void testUpdateWithoutLastName() throws InterruptedException {
        //Updating profile info without last name to check for error
        Thread.sleep(2000);
        Espresso.onView(withId(R.id.firstname)).perform(clearText());
        Espresso.onView(withId(R.id.firstname)).perform(typeText(first));
        Thread.sleep(2000);
        Espresso.onView(withId(R.id.lastname)).perform(clearText());
        Espresso.closeSoftKeyboard();
        Espresso.onView(withId(R.id.age)).perform(clearText());
        Espresso.onView(withId(R.id.age)).perform(typeText(age));
        Espresso.closeSoftKeyboard();
        Espresso.onView(withId(R.id.weight)).perform(clearText());
        Espresso.onView(withId(R.id.weight)).perform(typeText(weight));
        Espresso.closeSoftKeyboard();
        Espresso.onView(withId(R.id.height)).perform(clearText());
        Espresso.onView(withId(R.id.height)).perform(typeText(height));
        Espresso.closeSoftKeyboard();
        //set the activity to the login button
        assertNotNull(mActivity.findViewById(R.id.update_button));
        //perform a click on the login button
        onView(withId(R.id.update_button)).perform(click());

        ViewInteraction editText = onView(allOf(withId(R.id.lastname), isDisplayed()));
        editText.check(matches(hasErrorText("Please enter your last name!")));
    }
    @Test
    public void testUpdateInputtingAge() throws InterruptedException {
        //Updating profile info without age to check for error
        Thread.sleep(2000);
        Espresso.onView(withId(R.id.firstname)).perform(clearText());
        Espresso.onView(withId(R.id.firstname)).perform(typeText(first));
        Thread.sleep(2000);
        Espresso.onView(withId(R.id.lastname)).perform(clearText());
        Espresso.onView(withId(R.id.lastname)).perform(typeText(last));
        Espresso.closeSoftKeyboard();
        Espresso.onView(withId(R.id.age)).perform(clearText());
        Espresso.closeSoftKeyboard();
        Espresso.onView(withId(R.id.weight)).perform(clearText());
        Espresso.onView(withId(R.id.weight)).perform(typeText(weight));
        Espresso.closeSoftKeyboard();
        Espresso.onView(withId(R.id.height)).perform(clearText());
        Espresso.onView(withId(R.id.height)).perform(typeText(height));
        Espresso.closeSoftKeyboard();
        //set the activity to the login button
        assertNotNull(mActivity.findViewById(R.id.update_button));
        //perform a click on the login button
        onView(withId(R.id.update_button)).perform(click());

        ViewInteraction editText = onView(allOf(withId(R.id.age), isDisplayed()));
        editText.check(matches(hasErrorText("Please enter your age!")));
    }
    @Test
    public void testUpdateInputtingWeight() throws InterruptedException {
        //Updating profile info without weight to check for error
        Thread.sleep(2000);
        Espresso.onView(withId(R.id.firstname)).perform(clearText());
        Espresso.onView(withId(R.id.firstname)).perform(typeText(first));
        Thread.sleep(2000);
        Espresso.onView(withId(R.id.lastname)).perform(clearText());
        Espresso.onView(withId(R.id.lastname)).perform(typeText(last));
        Espresso.closeSoftKeyboard();
        Espresso.onView(withId(R.id.age)).perform(clearText());
        Espresso.onView(withId(R.id.age)).perform(typeText(age));
        Espresso.closeSoftKeyboard();
        Espresso.onView(withId(R.id.weight)).perform(clearText());
        Espresso.closeSoftKeyboard();
        Espresso.onView(withId(R.id.height)).perform(clearText());
        Espresso.onView(withId(R.id.height)).perform(typeText(height));
        Espresso.closeSoftKeyboard();
        //set the activity to the login button
        assertNotNull(mActivity.findViewById(R.id.update_button));
        //perform a click on the login button
        onView(withId(R.id.update_button)).perform(click());

        ViewInteraction editText = onView(allOf(withId(R.id.weight), isDisplayed()));
        editText.check(matches(hasErrorText("Please enter your weight!")));
    }
    //
    @Test
    public void testUpdateInputtingHeight() throws InterruptedException {
        //Updating profile info without height to check for error
        Thread.sleep(2000);
        Espresso.onView(withId(R.id.firstname)).perform(clearText());
        Espresso.onView(withId(R.id.firstname)).perform(typeText(first));
        Thread.sleep(2000);
        Espresso.onView(withId(R.id.lastname)).perform(clearText());
        Espresso.onView(withId(R.id.lastname)).perform(typeText(last));
        Espresso.closeSoftKeyboard();
        Espresso.onView(withId(R.id.age)).perform(clearText());
        Espresso.onView(withId(R.id.age)).perform(typeText(age));
        Espresso.closeSoftKeyboard();
        Espresso.onView(withId(R.id.weight)).perform(clearText());
        Espresso.onView(withId(R.id.weight)).perform(typeText(weight));
        Espresso.closeSoftKeyboard();
        Espresso.onView(withId(R.id.height)).perform(clearText());
        Espresso.closeSoftKeyboard();
        //set the activity to the login button
        assertNotNull(mActivity.findViewById(R.id.update_button));
        //perform a click on the login button
        onView(withId(R.id.update_button)).perform(click());

        ViewInteraction editText = onView(allOf(withId(R.id.height), isDisplayed()));
        editText.check(matches(hasErrorText("Please enter your height!")));
    }

    @After
    public void tearDown() throws Exception
    {
        mActivity = null;
    }
}
