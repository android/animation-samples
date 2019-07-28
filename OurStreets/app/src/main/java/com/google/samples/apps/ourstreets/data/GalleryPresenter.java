/*
 * Copyright 2016 Google Inc. All Rights Reserved.
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

package com.google.samples.apps.ourstreets.data;

import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;

import com.google.samples.apps.ourstreets.BuildConfig;
import com.google.samples.apps.ourstreets.model.Gallery;
import com.firebase.client.DataSnapshot;

/**
 * Presents galleries to other components of this app.
 */
public final class GalleryPresenter extends DataPresenter<Gallery> {

    public GalleryPresenter(@NonNull DataView<Gallery> dataView) {
        super(dataView, BuildConfig.GALLERIES_URL);
    }

    @NonNull
    @Override
    protected Gallery parseData(DataSnapshot data) {
        String title = data.child(JsonKeys.TITLE).getValue(String.class);
        String description = data.child(JsonKeys.DESCRIPTION).getValue(String.class);
        String galleryId = data.child(JsonKeys.GALLERY_ID).getValue(String.class);
        LatLng latLng = DataUtils.readLatLng(data);
        return new Gallery(title, description, galleryId, latLng);
    }
}
