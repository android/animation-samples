/*
 * Copyright 2015 Google Inc. All Rights Reserved.
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

package com.google.samples.apps.ourstreets.view;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;

import com.google.samples.apps.ourstreets.R;
import com.google.samples.apps.ourstreets.model.Gallery;

import java.util.List;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryViewHolder> {

    private static final int MAP_ZOOM = 3;
    private final List<Gallery> mGalleryCollection;
    private final String mItemViewBase;
    private final String mDescriptionContainerBase;
    private final String mDetailTransitionBase;
    private final String mDescriptionTitleBase;
    private final String mMapBase;
    private final OnMapReadyCallback mInitialOnMapReadyCallback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap googleMap) {
            googleMap.setMapType(GoogleMap.MAP_TYPE_NONE);
            googleMap.getUiSettings().setMapToolbarEnabled(false);
        }
    };

    public GalleryAdapter(@NonNull List<Gallery> data, @NonNull Context context) {
        mGalleryCollection = data;
        mItemViewBase = context.getString(R.string.transition_item_view);
        mDescriptionContainerBase = context.getString(R.string.transition_description);
        mDetailTransitionBase = context.getString(R.string.transition_description_detail);
        mDescriptionTitleBase = context.getString(R.string.transition_description_title);
        mMapBase = context.getString(R.string.transition_map);
    }

    @Override
    public GalleryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.gallery_item, parent, false);
        GalleryViewHolder holder = new GalleryViewHolder(v);
        initializeMapView(holder.mapView);
        return holder;
    }

    private void initializeMapView(MapView mapView) {
        mapView.onCreate(null);
        mapView.getMapAsync(mInitialOnMapReadyCallback);
        // lite mode enabled within map's layout declaration
        // Requirement to set clickable to false in order to not open the Google Maps application.
        // See: https://developers.google.com/maps/documentation/android-api/events#disabling_click_events_in_lite_mode
        mapView.setClickable(false);
    }

    @Override
    public void onBindViewHolder(GalleryViewHolder holder, int position) {
        final int adapterPosition = holder.getAdapterPosition();
        final Gallery gallery = mGalleryCollection.get(adapterPosition);
        // Set transition names here and not in the layout declaration,
        // because multiple identical transition names at the same time
        // confuse the transition system.
        holder.itemView.setTransitionName(mItemViewBase + adapterPosition);
        holder.descriptionContainer.setTransitionName(mDescriptionContainerBase + adapterPosition);
        holder.descriptionText.setTransitionName(mDescriptionTitleBase + adapterPosition);
        holder.titleText.setTransitionName(mDetailTransitionBase + adapterPosition);
        holder.mapView.setTransitionName(mMapBase + adapterPosition);

        // Bind the user visible information.
        holder.titleText.setText(gallery.getTitle());
        holder.descriptionText.setText(gallery.getDescription());
        final MapView mapView = holder.mapView;

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(gallery.getPosition(),
                        MAP_ZOOM));
                googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                mapView.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onViewRecycled(GalleryViewHolder holder) {
        // Avoid map glitches provoked due to scrolling behavior.
        holder.mapView.setVisibility(View.INVISIBLE);
    }

    @Override
    public long getItemId(int position) {
        return mGalleryCollection.get(position).hashCode();
    }

    @Override
    public int getItemCount() {
        return mGalleryCollection.size();
    }

}
