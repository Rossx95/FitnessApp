package com.fitnesspro.thefitnessapp.Activities.User;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.util.Log;

import androidx.test.espresso.Espresso;
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
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.*;

public class LoginActivityTest {
    @Rule
    public ActivityTestRule<LoginActivity> mActivityTestRule = new ActivityTestRule<LoginActivity>(LoginActivity.class);

    private LoginActivity mActivity = null;
    private String user = "rossx95@hotmail.com";
    private String pass = "123456";


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
    public void testLoginLaunch(){
        //enter credentials into login and password fields
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
    @After
    public void tearDown() throws Exception
    {
        mActivity = null;
    }
}