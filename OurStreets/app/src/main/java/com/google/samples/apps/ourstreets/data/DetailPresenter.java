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
import com.google.samples.apps.ourstreets.model.Detail;
import com.firebase.client.DataSnapshot;

/**
 * Presents gallery details to other components of this app.
 */
public final class DetailPresenter extends DataPresenter<Detail> {

    public DetailPresenter(@NonNull DataView<Detail> view, @NonNull String childId) {
        super(view, BuildConfig.DETAIL_URL + "/" + childId);
    }

    @NonNull
    @Override
    protected Detail parseData(DataSnapshot data) {
            String title = data.child(JsonKeys.TITLE).getValue(String.class);
            String description = data.child(JsonKeys.DESCRIPTION).getValue(String.class);
            LatLng latLng = DataUtils.readLatLng(data);
            DataSnapshot location = data.child(JsonKeys.LOCATION);
            Float tilt = location.child(JsonKeys.TILT).getValue(Float.class);
            Float bearing = location.child(JsonKeys.BEARING).getValue(Float.class);
        return new Detail(title, description, latLng, tilt, bearing);
    }
}
