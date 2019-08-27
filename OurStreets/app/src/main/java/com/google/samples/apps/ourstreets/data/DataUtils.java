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

import androidx.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;

import com.firebase.client.DataSnapshot;

/**
 * Utility class for data manipulation
 */
public final class DataUtils {

    /**
     * Read latitude and longitude data from a snapshot's child called {@link JsonKeys#LOCATION}.
     */
    public static @NonNull LatLng readLatLng(@NonNull DataSnapshot snapshot) {
        final DataSnapshot location = snapshot.child(JsonKeys.LOCATION);
        double lat = location.child(JsonKeys.LATITUDE).getValue(Double.class);
        double lng = location.child(JsonKeys.LONGITUDE).getValue(Double.class);
        return new LatLng(lat, lng);
    }
}
