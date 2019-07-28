/*
* Copyright 2014 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.example.android.interpolatorplayground.tests;

import com.example.android.interpolator.InterpolatorFragment;
import com.example.android.interpolator.MainActivity;
import com.example.android.interpolator.R;

import android.animation.ObjectAnimator;
import android.test.ActivityInstrumentationTestCase2;
import android.test.UiThreadTest;
import android.view.animation.Interpolator;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * Tests for interpolatorplayground sample.
 */
public class SampleTests extends ActivityInstrumentationTestCase2<MainActivity> {

    private MainActivity mTestActivity;
    private InterpolatorFragment mTestFragment;

    public SampleTests() {
        super(MainActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        setActivityInitialTouchMode(true);

        // Starts the activity under test using the default Intent with:
        // action = {@link Intent#ACTION_MAIN}
        // flags = {@link Intent#FLAG_ACTIVITY_NEW_TASK}
        // All other fields are null or empty.
        mTestActivity = getActivity();
        mTestFragment = (InterpolatorFragment)
                mTestActivity.getSupportFragmentManager().getFragments().get(1);
    }

    /**
     * Test if the test fixture has been set up correctly.
     */
    public void testPreconditions() {
        //Try to add a message to add context to your assertions. These messages will be shown if
        //a tests fails and make it easy to understand why a test failed
        assertNotNull("mTestActivity is null", mTestActivity);
        assertNotNull("mTestFragment is null", mTestFragment);
    }

    /**
     * Test if all UI elements have been set up correctly.
     */
    public void testInitialisation() {
        final int initialDuration = 750;
        final String initialInterpolator = "Linear";

        SeekBar durationBar = (SeekBar) getActivity().findViewById(R.id.durationSeek);
        TextView durationLabel = (TextView) getActivity().findViewById(R.id.durationLabel);
        Spinner interpolateSpinner = (Spinner) getActivity().findViewById(R.id.interpolatorSpinner);
        Interpolator[] interpolators = mTestFragment.getInterpolators();

        // Duration in progress bar
        assertEquals(durationBar.getProgress(), initialDuration);
        // Duration label
        assertEquals(durationLabel.getText().toString(), getActivity().getResources().getString(R.string.animation_duration, initialDuration));
        // Initial Interpolator
        assertEquals((String) interpolateSpinner.getSelectedItem(), initialInterpolator);

        // The number of loaded interpolators has to match the number of entries in the spinner
        assertEquals(interpolators.length, interpolateSpinner.getCount());
        // Test that all interpolators have been loaded
        for (Interpolator i : interpolators) {
            assertNotNull(i);
        }

    }

    /**
     * Test if all Interpolators can be used to start an animation.
     */
    @UiThreadTest
    public void testStartInterpolators() {

        // Start an animation for each interpolator
        final Interpolator[] interpolators = mTestFragment.getInterpolators();

        for (final Interpolator i : interpolators) {
            // Start the animation
            ObjectAnimator animator = mTestFragment.startAnimation(i, 1000L, mTestFragment.getPathIn());
            // Check that the correct interpolator is used for the animation
            assertEquals(i, animator.getInterpolator());
            // Verify the animation has started
            assertTrue(animator.isStarted());
            // Cancel before starting the next animation
            animator.cancel();
        }
    }
}
