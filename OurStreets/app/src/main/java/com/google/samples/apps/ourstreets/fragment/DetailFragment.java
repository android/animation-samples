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

package com.google.samples.apps.ourstreets.fragment;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.transition.ChangeBounds;
import android.transition.Fade;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterManager;
import com.google.samples.apps.ourstreets.R;
import com.google.samples.apps.ourstreets.data.DataView;
import com.google.samples.apps.ourstreets.data.DetailPresenter;
import com.google.samples.apps.ourstreets.data.IntentKeys;
import com.google.samples.apps.ourstreets.map.DetailClusterManager;
import com.google.samples.apps.ourstreets.map.OnCameraPositionUpdateListener;
import com.google.samples.apps.ourstreets.model.Detail;
import com.google.samples.apps.ourstreets.model.Gallery;
import com.google.samples.apps.ourstreets.view.ViewUtils;

import java.util.List;

/**
 * A {@link Fragment} that displays {@link Detail}s.
 * and a {@link com.google.android.gms.maps.MapView}.
 */
public class DetailFragment extends Fragment implements DataView<Detail> {

    public static final String TAG = "DetailFragment";
    private MapView mMapView;
    private FloatingActionButton mFloatingActionButton;
    private Gallery mGallery;
    private CameraPosition mCameraPosition;
    private int mMapPadding;
    private OnCameraPositionUpdateListener mCameraListener;
    private ClusterManager.OnClusterItemClickListener<Detail> mOnClusterItemClickListener;
    private GoogleMap.OnMapClickListener mOnMapClickListener;
    private Detail mSelectedDetail;
    private Transition mDescriptionChange;

    /**
     * Create a new instance with details for a given {@link Gallery}.
     *
     * @param context The context this runs in.
     * @param gallery The gallery of which the details should be displayed.
     * @return A newly instantiated fragment.
     */
    public static DetailFragment newInstance(@NonNull Context context, @NonNull Gallery gallery,
                                             @NonNull CameraPosition cameraPosition) {
        DetailFragment fragment = new DetailFragment();
        Bundle args = new Bundle();
        args.putParcelable(IntentKeys.GALLERY, gallery);
        args.putParcelable(IntentKeys.CAMERA_POSITION, cameraPosition);
        fragment.setArguments(args);
        final TransitionInflater inflater = TransitionInflater.from(context);
        fragment.setSharedElementEnterTransition(
                inflater.inflateTransition(R.transition.detail_shared_enter));
        fragment.setEnterTransition(new Fade());
        return fragment;
    }

    public DetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMapPadding = getResources().getDimensionPixelSize(R.dimen.padding_map);
        if (savedInstanceState != null) {
            mCameraPosition = savedInstanceState.getParcelable(IntentKeys.CAMERA_POSITION);
            mSelectedDetail = savedInstanceState.getParcelable(IntentKeys.DETAIL);
        }
        Bundle arguments;
        if ((arguments = getArguments()) != null) {
            mGallery = arguments.getParcelable(IntentKeys.GALLERY);
            if (mCameraPosition == null) {
                mCameraPosition = arguments.getParcelable(IntentKeys.CAMERA_POSITION);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewUtils.setStatusBarColor(getActivity(), R.color.status_bar_color);
        View contentView = inflater.inflate(R.layout.fragment_detail, container, false);
        mFloatingActionButton = (FloatingActionButton) contentView.findViewById(R.id.fab);
        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mSelectedDetail == null) {
                    return;
                }
                showStreetViewFragment(mSelectedDetail);
            }
        });
        if (mMapView == null) {
            mMapView = (MapView) contentView.findViewById(R.id.map_view);
            mMapView.onCreate(null);
        }
        return contentView;
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        mDescriptionChange = new ChangeBounds();
        mDescriptionChange.addListener(new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {
            }

            @Override
            public void onTransitionEnd(Transition transition) {
                applyMapPadding();
            }

            @Override
            public void onTransitionCancel(Transition transition) {

            }

            @Override
            public void onTransitionPause(Transition transition) {

            }

            @Override
            public void onTransitionResume(Transition transition) {

            }
        });
        if (ViewUtils.isMainDisplayInLandscape(getContext())) {
            ViewUtils.applyTopWindowInsetsForView(view.findViewById(R.id.description_layout));
        }
        loadDetails();
        if (mSelectedDetail == null) {
            setDescriptionText(mGallery);
        } else {
            setDescriptionText(mSelectedDetail);
        }
        applyMapPadding();
    }

    private void applyMapPadding() {
        if (mMapView == null) {
            return;
        }
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                View descriptionLayout = getView().findViewById(R.id.description_layout);
                googleMap.setPadding(0, 0, 0, descriptionLayout.getMeasuredHeight());
            }
        });
    }

    @Override
    public void onDestroyView() {
        mFloatingActionButton = null;
        super.onDestroyView();
    }

    @Override
    public void onResume() {
        mMapView.onResume();
        super.onResume();
    }

    @Override
    public void onSaveInstanceState(final Bundle outState) {
        mMapView.onSaveInstanceState(outState);
        outState.putParcelable(IntentKeys.CAMERA_POSITION, mCameraPosition);
        outState.putParcelable(IntentKeys.DETAIL, mSelectedDetail);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mMapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        mMapView.onLowMemory();
        super.onLowMemory();
    }

    @Override
    public void showData(List<Detail> data) {
        mGallery.replaceDetails(data);
        initMapAsync();
    }

    @Override
    public void showError() {
        // TODO: implement failed view
        Log.i(TAG, "showError: Loading failed");
    }

    /**
     * Enable updating of detail information.
     *
     * @param gallery The new gallery to display.
     * @param cameraPosition The initial camera position for the details.
     */
    public void setNewDetailInformation(@NonNull Gallery gallery,
                                        @NonNull CameraPosition cameraPosition) {
        mGallery = gallery;
        mCameraPosition = cameraPosition;
        loadDetails();
    }

    private void initMapAsync() {
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                initializeMap(googleMap);
            }
        });
    }

    private void initializeMap(final GoogleMap googleMap) {
        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(mCameraPosition));
        googleMap.setOnMapLoadedCallback(getOnMapLoadedCallback(googleMap));
        setMarkers(googleMap);
    }

    private void setDescriptionText(@NonNull Gallery gallery) {
        setDescriptionText(gallery.getTitle(), gallery.getDescription());
        mFloatingActionButton.hide();
    }

    private void setDescriptionText(@NonNull Detail detail) {
        mSelectedDetail = detail;
        setDescriptionText(detail.getTitle(), detail.getDescription());
        mFloatingActionButton.show();
    }

    private void setDescriptionText(@NonNull String title, @NonNull String detail) {
        View view = getView();
        if (view == null) {
            return;
        }
        TransitionManager.beginDelayedTransition(((ViewGroup) view), mDescriptionChange);
        ViewUtils.setTextOn(view, R.id.text_title, title);
        ViewUtils.setTextOn(view, R.id.text_detail, detail);
    }

    private void setMarkers(GoogleMap googleMap) {
        final List<Detail> locations = mGallery.getDetails();
        ClusterManager<Detail> clusterManager = getClusterManager(googleMap);
        clusterManager.addItems(locations);
        googleMap.setOnCameraChangeListener(clusterManager);
    }

    @NonNull
    private DetailClusterManager getClusterManager(GoogleMap googleMap) {
        return new DetailClusterManager(getContext(), googleMap,
                getOnClusterItemClickListener(),
                getOnMapClickListener(),
                getOnCameraPositionUpdateListener());
    }

    @NonNull
    private GoogleMap.OnMapClickListener getOnMapClickListener() {
        if (mOnMapClickListener == null) {
            mOnMapClickListener = new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    setDescriptionText(mGallery);
                }
            };
        }
        return mOnMapClickListener;
    }

    @NonNull
    private ClusterManager.OnClusterItemClickListener<Detail> getOnClusterItemClickListener() {
        if (mOnClusterItemClickListener == null) {
            mOnClusterItemClickListener = new ClusterManager.OnClusterItemClickListener<Detail>() {
                @Override
                public boolean onClusterItemClick(Detail detail) {
                    setDescriptionText(detail);
                    return false;
                }
            };
        }
        return mOnClusterItemClickListener;
    }

    private OnCameraPositionUpdateListener getOnCameraPositionUpdateListener() {
        if (mCameraListener == null) {
            mCameraListener = new OnCameraPositionUpdateListener() {
                @Override
                public void onCameraPositionUpdate(CameraPosition cameraPosition) {
                    mCameraPosition = cameraPosition;
                }
            };
        }
        return mCameraListener;
    }

    private void showStreetViewFragment(Detail detail) {
        StreetViewFragment streetViewFragment = getStreetViewFragment(detail);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        /*
            The StreetViewFragment has to be added (not replaced)
            in order to get the transition animation right.
         */
        transaction.add(android.R.id.content, streetViewFragment, StreetViewFragment.TAG)
                .addToBackStack(StreetViewFragment.TAG);
        transaction.commit();
    }

    @NonNull
    private GoogleMap.OnMapLoadedCallback getOnMapLoadedCallback(final GoogleMap googleMap) {
        return new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                googleMap.getUiSettings().setMapToolbarEnabled(false);
                if (mGallery.hasDetails()) {
                    googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    googleMap.animateCamera(
                            CameraUpdateFactory.newLatLngBounds(mGallery.getBounds(), mMapPadding));
                }
            }
        };
    }

    @NonNull
    private StreetViewFragment getStreetViewFragment(Detail detail) {
        FragmentManager supportFragmentManager = getActivity().getSupportFragmentManager();
        StreetViewFragment streetViewFragment = (StreetViewFragment) supportFragmentManager
                .findFragmentByTag(StreetViewFragment.TAG);
        if (streetViewFragment == null) {
            streetViewFragment = StreetViewFragment.newInstance(detail,
                    ViewUtils.getCenterForView(mFloatingActionButton),
                    mFloatingActionButton.getWidth());
        } else {
            streetViewFragment.setDetail(detail);
        }
        return streetViewFragment;
    }

    private void loadDetails() {
        if (mGallery == null) {
            Log.w(TAG, "loadDetails: can't load data for null gallery");
            showError();
            return;
        } else if (mGallery.hasDetails()) {
            initMapAsync();
            return;
        }
        new DetailPresenter(this, mGallery.getGalleryId()).getData();
    }
}
