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
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

import com.google.samples.apps.ourstreets.R;
import com.google.samples.apps.ourstreets.model.Detail;
import com.google.samples.apps.ourstreets.view.ViewUtils;

/**
 * Custom renderer to use the app's styled markers.
 */
public final class DetailMarkerRenderer extends DefaultClusterRenderer<Detail> {

    private static final int MIN_CLUSTER_SIZE = 10;
    private BitmapDescriptor mMapPin;
    private Drawable mMarkerDrawable;

    public DetailMarkerRenderer(Context context, final GoogleMap map,
                                final ClusterManager<Detail> clusterManager) {
        super(context, map, clusterManager);
        mMarkerDrawable = ContextCompat.getDrawable(context, R.drawable.map_pin);
        map.setInfoWindowAdapter(clusterManager.getMarkerManager());
    }

    @Override
    protected void onBeforeClusterItemRendered(Detail item, MarkerOptions options) {
        options.icon(getMapPin());
    }

    @Override
    protected boolean shouldRenderAsCluster(Cluster<Detail> cluster) {
        return cluster.getSize() > MIN_CLUSTER_SIZE;
    }

    @NonNull
    private BitmapDescriptor getMapPin() {
        if (mMapPin == null) {
            mMapPin = ViewUtils.getBitmapDescriptorFromDrawable(mMarkerDrawable);
        }
        return mMapPin;
    }
}
