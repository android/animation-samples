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

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

@SmallTest
public class GalleryTest {

    private Gallery mGalleryUnderTest;
    private String mTitle;
    private String mDescription;
    private String mGalleryId;
    private LatLng mPosition;

    @Before
    public void alloc() {
        mTitle = "Title";
        mDescription = "Description";
        mGalleryId = "galleryId";
        mPosition = new LatLng(51.5014, -0.1419);
        mGalleryUnderTest = new Gallery(mTitle, mDescription, mGalleryId, mPosition);
    }

    @After
    public void release() {
        mGalleryUnderTest = null;
        mPosition = null;
    }

    @Test
    public void testGetTitle() {
        assertThat(mGalleryUnderTest.getTitle(), is(mTitle));
    }

    @Test
    public void testGetDescription() {
        assertThat(mGalleryUnderTest.getDescription(), is(mDescription));
    }

    @Test
    public void testGetGalleryId() {
        assertThat(mGalleryUnderTest.getGalleryId(), is(mGalleryId));
    }

    @Test
    public void testGetPosition() {
        assertThat(mGalleryUnderTest.getPosition(), is(mPosition));
    }

    @Test
    public void testGetDetails_isEmpty() {
        mGalleryUnderTest.replaceDetails(null);
        assertThat(mGalleryUnderTest.getDetails(), notNullValue());
        assertThat(mGalleryUnderTest.getDetails().size(), is(0));
    }

    @Test
    public void testReplaceDetails() {
        List<Detail> testList = new ArrayList<>();
        testList.add(new Detail(mTitle, mDescription, mPosition, 0, 0));
        mGalleryUnderTest.replaceDetails(testList);
        assertThat(mGalleryUnderTest.getDetails(), is(testList));
    }

    @Test
    public void testHasDetails_none() {
        mGalleryUnderTest.replaceDetails(null);
        assertThat(mGalleryUnderTest.hasDetails(), is(false));
    }

    @Test
    public void testHasDetails_true() {
        List<Detail> testList = new ArrayList<>();
        testList.add(new Detail(mTitle, mDescription, mPosition, 0, 0));
        mGalleryUnderTest.replaceDetails(testList);
        assertThat(mGalleryUnderTest.hasDetails(), is(true));
    }

    @Test
    public void testGetBounds_none() {
        List<Detail> testList = new ArrayList<>();
        testList.add(new Detail(mTitle, mDescription, mPosition, 0, 0));
        mGalleryUnderTest.replaceDetails(testList);
        assertThat(mGalleryUnderTest.getBounds(), notNullValue());
    }
}
