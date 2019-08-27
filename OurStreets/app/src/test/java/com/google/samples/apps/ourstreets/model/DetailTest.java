/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.samples.apps.ourstreets.model;

import androidx.test.filters.SmallTest;

import com.google.android.gms.maps.model.LatLng;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@SmallTest
public class DetailTest {

    private Detail mDetailUnderTest;
    private LatLng mLocation;
    private String mTitle;
    private String mDescription;
    private float mTilt;
    private float mBearing;

    private static final float MAX_FLOAT_DELTA = 0.001f;

    @Before
    public void alloc() {
        mTitle = "Title";
        mDescription = "Description";
        mLocation = new LatLng(51.5014, -0.1419);
        mTilt = 0f;
        mBearing = 0f;
        mDetailUnderTest = new Detail(mTitle, mDescription, mLocation, mTilt, mBearing);
    }

    @After
    public void release() {
        mDetailUnderTest = null;
        mLocation = null;
    }

    @Test
    public void testGetPosition() {
        assertThat(mDetailUnderTest.getPosition(), is(mLocation));
    }

    @Test
    public void testGetDescription() {
        assertThat(mDetailUnderTest.getDescription(), is(mDescription));
    }

    @Test
    public void testGetTitle() {
        assertThat(mDetailUnderTest.getTitle(), is(mTitle));
    }

    @Test
    public void testGetTilt() {
        assertThat( Math.abs(mDetailUnderTest.getTilt() - mTilt) < MAX_FLOAT_DELTA, is(true));
    }

    @Test
    public void testGetBearing() {
        assertThat( Math.abs(mDetailUnderTest.getBearing() - mBearing) < MAX_FLOAT_DELTA, is(true));
    }
}
