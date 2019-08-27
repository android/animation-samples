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
import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.view.ViewCompat;
import androidx.interpolator.view.animation.FastOutLinearInInterpolator;
import androidx.recyclerview.widget.RecyclerView;
import android.transition.Explode;
import android.transition.Fade;
import android.transition.TransitionSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.FrameLayout;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.samples.apps.ourstreets.R;
import com.google.samples.apps.ourstreets.data.DataView;
import com.google.samples.apps.ourstreets.data.GalleryPresenter;
import com.google.samples.apps.ourstreets.model.Gallery;
import com.google.samples.apps.ourstreets.transition.Elevation;
import com.google.samples.apps.ourstreets.view.GalleryAdapter;
import com.google.samples.apps.ourstreets.view.GalleryDivider;
import com.google.samples.apps.ourstreets.view.GalleryViewHolder;
import com.google.samples.apps.ourstreets.view.RecyclerItemClickListener;
import com.google.samples.apps.ourstreets.view.ViewUtils;

import java.util.List;

/**
 * A {@link Fragment} that displays an overview of all available StreetView galleries.
 */
public class GalleryFragment extends Fragment implements DataView<Gallery> {

    public static final String TAG = "GalleryFragment";

    private static final FastOutLinearInInterpolator INTERPOLATOR =
            new FastOutLinearInInterpolator();

    private List<Gallery> mGalleries;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView mRecyclerView;
    private View mEmptyView;
    private View mGalleryContent;

    private boolean mAnimateViewSwap;

    public GalleryFragment() {

        final Fade fade = new Fade();
        fade.addTarget(R.id.appbar);

        Explode explode = new Explode();
        explode.excludeTarget(R.id.appbar, true);

        Elevation elevation = new Elevation();
        elevation.addTarget(R.id.gallery_card);
        elevation.setStartDelay(250); // arbitrarily chosen delay

        TransitionSet exit = new TransitionSet();
        exit.addTransition(fade);
        exit.addTransition(explode);
        exit.addTransition(elevation);

        setExitTransition(exit);
    }

    public static GalleryFragment newInstance() {
        return new GalleryFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewUtils.setStatusBarColor(getActivity(), R.color.background);
        return inflater.inflate(R.layout.fragment_gallery, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        ViewCompat.requestApplyInsets(view);
        mEmptyView = view.findViewById(R.id.progress);
        mGalleryContent = view.findViewById(R.id.gallery_content);
        initRecyclerView(view);
        mAnimateViewSwap = savedInstanceState == null && mGalleries == null;
        if (mGalleries == null) {
            new GalleryPresenter(this).getData();
        } else {
            showData(mGalleries);
        }
    }

    @Override
    public void onDestroyView() {
        mRecyclerView = null;
        mEmptyView = null;
        mGalleryContent = null;
        super.onDestroyView();
    }

    @Override
    public void showData(List<Gallery> galleries) {
        mGalleries = galleries;
        initRecyclerViewAdapter();
        showContent(mAnimateViewSwap);
    }

    @Override
    public void showError() {
        mGalleryContent.setVisibility(View.GONE);
        mEmptyView.setVisibility(View.GONE);
        //noinspection ConstantConditions
        View failedContainer = getView().findViewById(R.id.failedStub);
        if (failedContainer == null) {
            getView().findViewById(R.id.failed).setVisibility(View.VISIBLE);
            // Loading already failed before. Everything else is already inflated and set up.
            return;
        }
        setupAndInflate((ViewStub) failedContainer);
    }

    private void initRecyclerView(View contentView) {
        mRecyclerView = (RecyclerView) contentView.findViewById(R.id.galleries);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity()) {
                    @Override
                    public void onItemClick(RecyclerView.ViewHolder holder, int position) {
                        showDetailFragment((GalleryViewHolder) holder,
                                mGalleries.get(position));
                    }
                });
        final int itemMargin = getResources().getDimensionPixelSize(R.dimen.grid_single);
        mRecyclerView.addItemDecoration(new GalleryDivider(itemMargin));
    }

    private void initRecyclerViewAdapter() {
        if (mRecyclerView == null || mGalleries == null) {
            Log.d(TAG, "initRecyclerViewAdapter: Missing data, not initializing.");
            return;
        }
        if (mAdapter == null) {
            mAdapter = new GalleryAdapter(mGalleries, getContext());
        }
        mRecyclerView.setAdapter(mAdapter);
    }

    private void showContent(boolean animate) {
        if (!isAdded()) {
            Log.d(TAG, "showData: Not showing data");
            return;
        }
        if (animate) {
            safelyAnimateProgressToContent();
        } else {
            swapEmptyWithContentView(false);
        }
    }

    private void showDetailFragment(@NonNull final GalleryViewHolder holder,
                                    @NonNull final Gallery gallery) {
        // Turn of transition grouping for clicked item view to break card structure.
        ((ViewGroup) holder.itemView).setTransitionGroup(false);
        holder.mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                CameraPosition cameraPosition = googleMap.getCameraPosition();
                performDetailTransition(holder, gallery, cameraPosition);
            }
        });
    }

    private void performDetailTransition(@NonNull GalleryViewHolder itemHolder,
                                         @NonNull Gallery gallery,
                                         CameraPosition cameraPosition) {
        DetailFragment fragment = getDetailFragment(gallery, cameraPosition);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        @SuppressLint("CommitTransaction")
        FragmentTransaction transaction = fragmentManager.beginTransaction()
                .replace(android.R.id.content, fragment, DetailFragment.TAG);
        createAndAddTransitionParticipants(itemHolder, transaction);
        transaction.addToBackStack(DetailFragment.TAG).commit();
    }

    @NonNull
    private DetailFragment getDetailFragment(Gallery gallery, CameraPosition cameraPosition) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        DetailFragment detailFragment = (DetailFragment)
                fragmentManager.findFragmentByTag(DetailFragment.TAG);
        if (detailFragment == null) {
            detailFragment = DetailFragment.newInstance(getActivity(), gallery, cameraPosition);
        } else {
            detailFragment.setNewDetailInformation(gallery, cameraPosition);
        }
        return detailFragment;
    }

    private void createAndAddTransitionParticipants(@NonNull GalleryViewHolder itemHolder,
                                                    @NonNull FragmentTransaction transaction) {
        transaction.addSharedElement(itemHolder.descriptionContainer,
                getString(R.string.transition_description));
        transaction.addSharedElement(itemHolder.mapView,
                getString(R.string.transition_map));
    }

    /**
     * Add animation safety by making sure that the fragment currently is attached.
     */
    private void safelyAnimateProgressToContent() {
        if (mGalleryContent.isAttachedToWindow()) {
            animateProgressToContent();
        } else {
            mGalleryContent.addOnAttachStateChangeListener(new View
                    .OnAttachStateChangeListener() {
                @Override
                public void onViewAttachedToWindow(View v) {
                    v.removeOnAttachStateChangeListener(this);
                    animateProgressToContent();
                }

                @Override
                public void onViewDetachedFromWindow(View v) {
                    v.removeOnAttachStateChangeListener(this);
                }
            });
        }
    }

    /**
     * Perform the animation from loading progress to the actual fragment's content.
     */
    private void animateProgressToContent() {
        AnimatorSet animatorSet = new AnimatorSet();
        FrameLayout targetView = ((FrameLayout) getView());
        //noinspection ConstantConditions
        animatorSet.play(createCircularReveal(targetView)).with(createColorChange(targetView));
        animatorSet.start();
    }

    @NonNull
    private Animator createCircularReveal(@NonNull View targetView) {
        Animator circularReveal = ViewUtils.createCircularReveal(targetView,
                R.id.progress, INTERPOLATOR);
        circularReveal.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                swapEmptyWithContentView(true);
            }
        });
        return circularReveal;
    }

    @NonNull
    private ObjectAnimator createColorChange(@NonNull FrameLayout targetView) {
        return ViewUtils.createColorChange(targetView,
                R.color.blue_grey_600, android.R.color.transparent, INTERPOLATOR);
    }

    /**
     * Replace the empty view with the gallery view.
     *
     * @param animate <code>true</code> if the swap should be animated, else <code>false</code>.
     */
    private void swapEmptyWithContentView(boolean animate) {
        if (animate) {
            mEmptyView.animate()
                    .alpha(0f)
                    .setInterpolator(INTERPOLATOR)
                    .withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            mEmptyView.setVisibility(View.GONE);
                        }
                    });
            mGalleryContent.setAlpha(0f);
            mGalleryContent.setVisibility(View.VISIBLE);
            mGalleryContent.animate()
                    .alpha(1f)
                    .setInterpolator(INTERPOLATOR)
                    .start();
        } else {
            mEmptyView.setVisibility(View.GONE);
            mGalleryContent.setVisibility(View.VISIBLE);
        }
    }

    private void setupAndInflate(ViewStub failedStub) {
        failedStub.setOnInflateListener(new ViewStub.OnInflateListener() {
            @Override
            public void onInflate(ViewStub stub, final View inflated) {
                inflated.findViewById(R.id.try_again).setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                new GalleryPresenter(GalleryFragment.this).getData();
                                mEmptyView.setVisibility(View.VISIBLE);
                                inflated.setVisibility(View.GONE);
                            }
                        });
            }
        });
        failedStub.inflate();
    }
}
