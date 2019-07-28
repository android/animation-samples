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

package com.google.samples.apps.ourstreets.map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.google.maps.android.MarkerManager;

import com.google.samples.apps.ourstreets.R;

/**
 * A {@link MarkerManager} that does not show any info when there's an empty info window.
 */
public final class NoEmptyInfoWindowMarkerManager extends MarkerManager {

    private final LayoutInflater mLayoutInflater;

    public NoEmptyInfoWindowMarkerManager(Context context, GoogleMap map) {
        super(map);
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public View getInfoWindow(Marker marker) {
        if (TextUtils.isEmpty(marker.getTitle())) {
            // Highly sophisticated check to see if we're dealing with a cluster.
            // Clusters should not not have an InfoWindow of its own as they don't lead
            // to a specific street view.
            return null;
        }
        @SuppressLint("InflateParams")
        TextView title = (TextView) mLayoutInflater.inflate(R.layout.map_info_window, null);
        title.setText(marker.getTitle());
        return title;
    }
}
