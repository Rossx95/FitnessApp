package com.fitnesspro.thefitnessapp.Activities.Main;

import android.content.Intent;
import android.view.View;

import androidx.test.rule.ActivityTestRule;

import com.fitnesspro.thefitnessapp.R;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.*;

public class SplashActivityTest {

    @Rule
    public ActivityTestRule<SplashActivity> mActivityTestRule = new ActivityTestRule<SplashActivity>(SplashActivity.class);

    private SplashActivity mActivity = null;

    @Before
    public void setUp() throws Exception
    {
        mActivity = mActivityTestRule.getActivity();
    }
    @Test
    public void testLaunch()
    {
        Intent expectedIntent = new Intent(mActivity, SplashActivity.class);
        assertNotNull(expectedIntent);
    }

    @After
    public void tearDown() throws Exception
    {
        mActivity = null;
    }
}