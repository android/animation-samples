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

import android.content.Context;
import androidx.annotation.NonNull;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.maps.android.clustering.ClusterManager;

import com.google.samples.apps.ourstreets.model.Detail;

/**
 * Cluster manager set up for a {@link Detail} GoogleMap cluster.
 */
public final class DetailClusterManager extends ClusterManager<Detail> {

    private final OnCameraPositionUpdateListener mOnCameraPositionUpdateListener;

    public DetailClusterManager(
            @NonNull Context context,
            @NonNull GoogleMap map,
            @NonNull OnClusterItemClickListener<Detail> onClusterItemClickListener,
            @NonNull GoogleMap.OnMapClickListener onMapClickListener,
            @NonNull OnCameraPositionUpdateListener onCameraPositionUpdateListener) {
        super(context, map, new NoEmptyInfoWindowMarkerManager(context, map));
        map.setOnInfoWindowClickListener(this);
        map.setOnMarkerClickListener(this);
        setRenderer(new DetailMarkerRenderer(context, map, this));
        mOnCameraPositionUpdateListener = onCameraPositionUpdateListener;
        map.setOnMapClickListener(onMapClickListener);
        setOnClusterItemClickListener(onClusterItemClickListener);
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        if (mOnCameraPositionUpdateListener != null) {
            mOnCameraPositionUpdateListener.onCameraPositionUpdate(cameraPosition);
        }
        super.onCameraChange(cameraPosition);
    }
}
