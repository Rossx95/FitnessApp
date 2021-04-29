package com.fitnesspro.thefitnessapp.Activities.User;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.util.Log;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.ViewInteraction;
import androidx.test.rule.ActivityTestRule;

import com.fitnesspro.thefitnessapp.Activities.Main.MainActivity;
import com.fitnesspro.thefitnessapp.Activities.Main.SplashActivity;
import com.fitnesspro.thefitnessapp.R;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.InstrumentationRegistry.getInstrumentation;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.action.ViewActions.typeTextIntoFocusedView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.hasErrorText;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.*;

public class LoginActivityTest {
    @Rule
    public ActivityTestRule<LoginActivity> mActivityTestRule = new ActivityTestRule<LoginActivity>(LoginActivity.class);

    private LoginActivity mActivity = null;
    private String user = "rossx95@hotmail.com";
    private String fakeUser = "Jimmy@ulster.ac.uk";
    private String pass = "123456";
    private String fakePassword = "654321";
    private String shortPassword = "123";


    Instrumentation.ActivityMonitor registerMonitor = getInstrumentation().addMonitor(RegisterActivity.class.getName(),null,false);
    Instrumentation.ActivityMonitor loginMonitor = getInstrumentation().addMonitor(MainActivity.class.getName(),null,false);
    @Before
    public void setUp() throws Exception
    {
        mActivity = mActivityTestRule.getActivity();
    }
    @Test
    public void testLaunch()
    {
        Intent expectedIntent = new Intent(mActivity, LoginActivity.class);
        assertNotNull(expectedIntent);
    }
    @Test
    public void testRegisterLaunch(){
        //set the activity to the register button
        assertNotNull(mActivity.findViewById(R.id.btn_signup));
        //perform a click on the register button
        onView(withId(R.id.btn_signup)).perform(click());
        Activity registerActivity = getInstrumentation().waitForMonitorWithTimeout(registerMonitor,5000);
        //open register screen
        assertNotNull(registerActivity);
        registerActivity.finish();
    }
    @Test
    public void testLoginSuccessful(){
        //enter credentials into login and password fields, login successful
        Espresso.onView(withId(R.id.email)).perform(typeText(user));
        Espresso.onView(withId(R.id.password)).perform(typeText(pass));
        //close soft keyboard
        Espresso.closeSoftKeyboard();
        //set the activity to the login button
        assertNotNull(mActivity.findViewById(R.id.btn_login));
        //perform a click on the login button
        onView(withId(R.id.btn_login)).perform(click());
        Activity loginActivity = getInstrumentation().waitForMonitorWithTimeout(loginMonitor,5000);
        //open main activity screen if login is successful
        assertNotNull(loginActivity);
        //end activity
        loginActivity.finish();
    }

    @Test
    public void testLoginNotAUser(){
        //enter credentials into login and password fields, user does not exist
        Espresso.onView(withId(R.id.email)).perform(typeText(fakeUser));
        Espresso.onView(withId(R.id.password)).perform(typeText(pass));
        //close soft keyboard
        Espresso.closeSoftKeyboard();
        //set the activity to the login button
        assertNotNull(mActivity.findViewById(R.id.btn_login));
        //perform a click on the login button
        onView(withId(R.id.btn_login)).perform(click());
        onView(withText(R.string.auth_failed)).
                inRoot(withDecorView(not(is(mActivity.getWindow().getDecorView())))).
                check(matches(isDisplayed()));
    }
    @Test
    public void testLoginIncorrectPassword(){
        //enter credentials into login and password fields, incorrect password
        Espresso.onView(withId(R.id.email)).perform(typeText(user));
        Espresso.onView(withId(R.id.password)).perform(typeText(fakePassword));
        //close soft keyboard
        Espresso.closeSoftKeyboard();
        //set the activity to the login button
        assertNotNull(mActivity.findViewById(R.id.btn_login));
        //perform a click on the login button
        onView(withId(R.id.btn_login)).perform(click());
        onView(withText(R.string.auth_failed)).
                inRoot(withDecorView(not(is(mActivity.getWindow().getDecorView())))).
                check(matches(isDisplayed()));
    }
    @Test
    public void testLoginPasswordTooShort(){
        //enter credentials into login and password fields, password too short
        Espresso.onView(withId(R.id.email)).perform(typeText(user));
        Espresso.onView(withId(R.id.password)).perform(typeText(shortPassword));
        //close soft keyboard
        Espresso.closeSoftKeyboard();
        //set the activity to the login button
        assertNotNull(mActivity.findViewById(R.id.btn_login));
        //perform a click on the login button
        onView(withId(R.id.btn_login)).perform(click());
        onView(withText(R.string.minimum_password)).
                inRoot(withDecorView(not(is(mActivity.getWindow().getDecorView())))).
                check(matches(isDisplayed()));
    }

    @After
    public void tearDown() throws Exception
    {
        mActivity = null;
    }
}