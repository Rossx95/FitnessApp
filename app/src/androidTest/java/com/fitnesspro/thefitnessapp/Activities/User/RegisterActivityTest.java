package com.fitnesspro.thefitnessapp.Activities.User;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.RootMatchers;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.rule.ActivityTestRule;

import com.fitnesspro.thefitnessapp.Activities.Main.MainActivity;
import com.fitnesspro.thefitnessapp.R;

import org.hamcrest.Matchers;
import org.junit.After;
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
import static org.junit.Assert.*;

public class RegisterActivityTest {
    @Rule
    public ActivityTestRule<RegisterActivity> mActivityTestRule = new ActivityTestRule<RegisterActivity>(RegisterActivity.class);

    private RegisterActivity mActivity = null;
    private String first = "david";
    private String last = "jones";
    private String email = "david@hotmail.com";
    private String pass = "123456";
    private String shortpass = "12345";

    Instrumentation.ActivityMonitor registerMonitor = getInstrumentation().addMonitor(MainActivity.class.getName(),null,false);

    @Before
    public void setUp() throws Exception
    {
        mActivity = mActivityTestRule.getActivity();
    }
    @Test
    public void testRegisterLaunch()
    {
        Intent expectedIntent = new Intent(mActivity, RegisterActivity.class);
        assertNotNull(expectedIntent);
    }
    @Test
    public void testRegistration(){
        //enter credentials into login and password fields
        Espresso.onView(withId(R.id.firstname)).perform(typeText(first));
        Espresso.onView(withId(R.id.lastname)).perform(typeText(last));
        Espresso.onView(withId(R.id.email)).perform(typeText(email));
        //close soft keyboard
        Espresso.closeSoftKeyboard();
        Espresso.onView(withId(R.id.password)).perform(typeText(pass));
        //close soft keyboard
        Espresso.closeSoftKeyboard();
        //set the activity to the login button
        assertNotNull(mActivity.findViewById(R.id.sign_up_button));
        //perform a click on the login button
        onView(withId(R.id.sign_up_button)).perform(click());
        Activity registerActivity = getInstrumentation().waitForMonitorWithTimeout(registerMonitor,5000);
        //open main activity screen if login is successful
        assertNotNull(registerActivity);
        //end activity
        registerActivity.finish();
    }

    @Test
    public void testRegistrationWithoutFirstName(){
        //enter credentials into login and password fields
//        Espresso.onView(withId(R.id.firstname)).perform(typeText(first));
        Espresso.onView(withId(R.id.lastname)).perform(typeText(last));
        Espresso.onView(withId(R.id.email)).perform(typeText(email));
        //close soft keyboard
        Espresso.closeSoftKeyboard();
        Espresso.onView(withId(R.id.password)).perform(typeText(pass));
        //close soft keyboard
        Espresso.closeSoftKeyboard();
        //set the activity to the login button
        assertNotNull(mActivity.findViewById(R.id.sign_up_button));
        //perform a click on the login button
        onView(withId(R.id.sign_up_button)).perform(click());

        ViewInteraction editText = onView(allOf(withId(R.id.firstname), isDisplayed()));
        editText.check(matches(hasErrorText("Enter first name!")));

    }
    @Test
    public void testRegistrationWithoutLastName(){
        //enter credentials into login and password fields
        Espresso.onView(withId(R.id.firstname)).perform(typeText(first));
//        Espresso.onView(withId(R.id.lastname)).perform(typeText(last));
        Espresso.onView(withId(R.id.email)).perform(typeText(email));
        //close soft keyboard
        Espresso.closeSoftKeyboard();
        Espresso.onView(withId(R.id.password)).perform(typeText(pass));
        //close soft keyboard
        Espresso.closeSoftKeyboard();
        //set the activity to the login button
        assertNotNull(mActivity.findViewById(R.id.sign_up_button));
        //perform a click on the login button
        onView(withId(R.id.sign_up_button)).perform(click());

        ViewInteraction editText = onView(allOf(withId(R.id.lastname), isDisplayed()));
        editText.check(matches(hasErrorText("Enter last name!")));

    }
    @Test
    public void testRegistrationWithoutEmail(){
        //enter credentials into login and password fields
        Espresso.onView(withId(R.id.firstname)).perform(typeText(first));
        Espresso.onView(withId(R.id.lastname)).perform(typeText(last));
//        Espresso.onView(withId(R.id.email)).perform(typeText(email));
        //close soft keyboard
        Espresso.closeSoftKeyboard();
        Espresso.onView(withId(R.id.password)).perform(typeText(pass));
        //close soft keyboard
        Espresso.closeSoftKeyboard();
        //set the activity to the login button
        assertNotNull(mActivity.findViewById(R.id.sign_up_button));
        //perform a click on the login button
        onView(withId(R.id.sign_up_button)).perform(click());

        ViewInteraction editText = onView(allOf(withId(R.id.email), isDisplayed()));
        editText.check(matches(hasErrorText("Enter email address!")));

    }
    @Test
    public void testRegistrationWithoutPassword(){
        //enter credentials into login and password fields
        Espresso.onView(withId(R.id.firstname)).perform(typeText(first));
        Espresso.onView(withId(R.id.lastname)).perform(typeText(last));
        Espresso.onView(withId(R.id.email)).perform(typeText(email));
        //close soft keyboard
        Espresso.closeSoftKeyboard();
//        Espresso.onView(withId(R.id.password)).perform(typeText(pass));
        //close soft keyboard
        Espresso.closeSoftKeyboard();
        //set the activity to the login button
        assertNotNull(mActivity.findViewById(R.id.sign_up_button));
        //perform a click on the login button
        onView(withId(R.id.sign_up_button)).perform(click());

        ViewInteraction editText = onView(allOf(withId(R.id.password), isDisplayed()));
        editText.check(matches(hasErrorText("Enter password!")));

    }

    @Test
    public void testRegistrationWithPasswordTooShort(){
        //enter credentials into login and password fields
        Espresso.onView(withId(R.id.firstname)).perform(typeText(first));
        Espresso.onView(withId(R.id.lastname)).perform(typeText(last));
        Espresso.onView(withId(R.id.email)).perform(typeText(email));
        //close soft keyboard
        Espresso.closeSoftKeyboard();
        Espresso.onView(withId(R.id.password)).perform(typeText(shortpass));
        //close soft keyboard
        Espresso.closeSoftKeyboard();
        //set the activity to the login button
        assertNotNull(mActivity.findViewById(R.id.sign_up_button));
        //perform a click on the login button
        onView(withId(R.id.sign_up_button)).perform(click());

        ViewInteraction editText = onView(allOf(withId(R.id.password), isDisplayed()));
        editText.check(matches(hasErrorText("Password too short, enter minimum 6 characters!")));

    }

    @Test
    public void testRegistrationExistingUser(){
        //enter credentials into login and password fields
        Espresso.onView(withId(R.id.firstname)).perform(typeText(first));
        Espresso.onView(withId(R.id.lastname)).perform(typeText(last));
        Espresso.onView(withId(R.id.email)).perform(typeText(email));
        //close soft keyboard
        Espresso.closeSoftKeyboard();
        Espresso.onView(withId(R.id.password)).perform(typeText(pass));
        //close soft keyboard
        Espresso.closeSoftKeyboard();
        //set the activity to the login button
        assertNotNull(mActivity.findViewById(R.id.sign_up_button));
        //perform a click on the login button
        onView(withId(R.id.sign_up_button)).perform(click());

        ViewInteraction editText = onView(allOf(withId(R.id.email), isDisplayed()));
        editText.check(matches(hasErrorText("This email has already been used for another account!")));
    }

    @After
    public void tearDown() throws Exception
    {
        mActivity = null;
    }
}