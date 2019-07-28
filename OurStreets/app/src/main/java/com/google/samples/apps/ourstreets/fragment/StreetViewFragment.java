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

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;

import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.SupportStreetViewPanoramaFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.StreetViewPanoramaCamera;
import com.google.samples.apps.ourstreets.R;
import com.google.samples.apps.ourstreets.data.IntentKeys;
import com.google.samples.apps.ourstreets.model.Detail;
import com.google.samples.apps.ourstreets.view.ViewUtils;

import java.util.concurrent.TimeUnit;

/**
 * A {@link Fragment} that displays a StreetView.
 */
public class StreetViewFragment extends Fragment implements BackPressAware {

    public static final String TAG = "StreetViewFragment";

    private static final String REVEAL_CENTER = "REVEAL_CENTER";
    private static final String REVEAL_WIDTH = "REVEAL_WIDTH";
    private static final Interpolator INTERPOLATOR = new FastOutSlowInInterpolator();

    private SupportStreetViewPanoramaFragment mMapFragment;
    private boolean isRestored;
    private Detail mDetail;
    private Point mRevealCenter;
    private int mRevealWidth;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param detail The StreetView to display.
     * @param revealCenter Center point of circular reveal start.
     * @param revealWidth Initial width as returned from {@link View#getWidth()}.
     * @return A new instance of fragment StreetViewFragment.
     */
    public static StreetViewFragment newInstance(@NonNull Detail detail,
                                                 @NonNull Point revealCenter,
                                                 int revealWidth) {
        StreetViewFragment fragment = new StreetViewFragment();
        Bundle args = new Bundle();
        args.putParcelable(IntentKeys.DETAIL, detail);
        args.putParcelable(REVEAL_CENTER, revealCenter);
        args.putInt(REVEAL_WIDTH, revealWidth);
        fragment.setArguments(args);
        return fragment;
    }

    public StreetViewFragment() {
        // no-op
    }

    /**
     * Change street view details without having to create a new fragment.
     *
     * @param detail The detail to display.
     */
    public void setDetail(Detail detail) {
        mDetail = detail;
        setUpStreetViewPanoramaIfNeeded(detail.getPosition());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mDetail = getArguments().getParcelable(IntentKeys.DETAIL);
        }
        mRevealCenter = getArguments().getParcelable(REVEAL_CENTER);
        mRevealWidth = getArguments().getInt(REVEAL_WIDTH);
        isRestored = savedInstanceState != null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle state) {
        ViewUtils.setStatusBarColor(getActivity(), R.color.status_bar_color);
        return inflater.inflate(R.layout.fragment_street_view, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        initMapFragment();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.add(R.id.container_street_view, mMapFragment)
                .commit();
        if (savedInstanceState == null) {
            // Only animate when this fragment is not being recreated.
            revealPanorama();
        } else {
            view.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        if (isRestored) {
            getFragmentManager().popBackStack();
        } else {
            // Perform a circular conceal, then pop this fragment off the back stack.
            final FrameLayout view = ((FrameLayout) getView());
            //noinspection ConstantConditions
            Animator circularConceal = ViewUtils.createCircularConceal(mRevealCenter, mRevealWidth,
                    view, INTERPOLATOR);
            circularConceal.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    view.setVisibility(View.GONE);
                    getFragmentManager().popBackStack();
                }
            });
            circularConceal.start();
        }
    }

    private void initMapFragment() {
        mMapFragment = SupportStreetViewPanoramaFragment.newInstance();
        mMapFragment.getStreetViewPanoramaAsync(new OnStreetViewPanoramaReadyCallback() {
            @Override
            public void onStreetViewPanoramaReady(StreetViewPanorama streetViewPanorama) {
                setUpStreetViewPanoramaIfNeeded(mDetail.getPosition());
            }
        });
    }

    private void setUpStreetViewPanoramaIfNeeded(final LatLng location) {
        mMapFragment.getStreetViewPanoramaAsync(new OnStreetViewPanoramaReadyCallback() {
            @Override
            public void onStreetViewPanoramaReady(StreetViewPanorama streetViewPanorama) {
                if (streetViewPanorama != null) {
                    streetViewPanorama.setPosition(location);
                    streetViewPanorama.setUserNavigationEnabled(true);
                    streetViewPanorama.setPanningGesturesEnabled(true);
                    streetViewPanorama.setZoomGesturesEnabled(true);

                    StreetViewPanoramaCamera galleryOrientation = StreetViewPanoramaCamera.
                            builder(streetViewPanorama.getPanoramaCamera())
                            .bearing(mDetail.getBearing())
                            .tilt(mDetail.getTilt())
                            .build();
                    streetViewPanorama.animateTo(galleryOrientation, TimeUnit.SECONDS.toMillis(1));
                }
            }
        });
    }

    /**
     * Reveals the contents of this fragment using a circular reveal animation.
     */
    private void revealPanorama() {
        //noinspection ConstantConditions
        getView().setVisibility(View.VISIBLE);
        //noinspection ConstantConditions
        Animator circularReveal = ViewUtils.createCircularReveal(mRevealCenter, mRevealWidth,
                getView(), INTERPOLATOR);
        ObjectAnimator colorChange = ViewUtils.createColorChange(((FrameLayout) getView()),
                R.color.foreground, android.R.color.transparent, INTERPOLATOR);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(circularReveal).with(colorChange);
        animatorSet.start();
    }
}
