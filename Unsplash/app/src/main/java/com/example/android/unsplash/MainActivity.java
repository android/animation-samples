/*
 * Copyright (C) 2016 The Android Open Source Project
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

package com.example.android.unsplash;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Transition;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ProgressBar;

import com.example.android.unsplash.data.UnsplashService;
import com.example.android.unsplash.data.model.Photo;
import com.example.android.unsplash.databinding.PhotoItemBinding;
import com.example.android.unsplash.ui.DetailSharedElementEnterCallback;
import com.example.android.unsplash.ui.TransitionCallback;
import com.example.android.unsplash.ui.grid.GridMarginDecoration;
import com.example.android.unsplash.ui.grid.OnItemSelectedListener;
import com.example.android.unsplash.ui.grid.PhotoAdapter;
import com.example.android.unsplash.ui.grid.PhotoViewHolder;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MainActivity extends Activity {

    private static final int PHOTO_COUNT = 12;
    private static final String TAG = "MainActivity";

    private final Transition.TransitionListener sharedExitListener =
            new TransitionCallback() {
                @Override
                public void onTransitionEnd(Transition transition) {
                    setExitSharedElementCallback(null);
                }
            };

    private RecyclerView grid;
    private ProgressBar empty;
    private ArrayList<Photo> relevantPhotos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        postponeEnterTransition();
        // Listener to reset shared element exit transition callbacks.
        getWindow().getSharedElementExitTransition().addListener(sharedExitListener);

        grid = (RecyclerView) findViewById(R.id.image_grid);
        empty = (ProgressBar) findViewById(android.R.id.empty);

        setupRecyclerView();

        if (savedInstanceState != null) {
            relevantPhotos = savedInstanceState.getParcelableArrayList(IntentUtil.RELEVANT_PHOTOS);
        }
        displayData();
    }

    private void displayData() {
        if (relevantPhotos != null) {
            populateGrid();
        } else {
            UnsplashService unsplashApi = new RestAdapter.Builder()
                    .setEndpoint(UnsplashService.ENDPOINT)
                    .build()
                    .create(UnsplashService.class);
            unsplashApi.getFeed(new Callback<List<Photo>>() {
                @Override
                public void success(List<Photo> photos, Response response) {
                    // the first items not interesting to us, get the last <n>
                    relevantPhotos = new ArrayList<>(photos.subList(photos.size() - PHOTO_COUNT,
                            photos.size()));
                    populateGrid();
                }

                @Override
                public void failure(RetrofitError error) {
                    Log.e(TAG, "Error retrieving Unsplash feed:", error);
                }
            });
        }
    }

    private void populateGrid() {
        grid.setAdapter(new PhotoAdapter(this, relevantPhotos));
        grid.addOnItemTouchListener(new OnItemSelectedListener(MainActivity.this) {
            public void onItemSelected(RecyclerView.ViewHolder holder, int position) {
                if (!(holder instanceof PhotoViewHolder)) {
                    return;
                }
                PhotoItemBinding binding = ((PhotoViewHolder) holder).getBinding();
                final Intent intent = getDetailActivityStartIntent(MainActivity.this,
                        relevantPhotos, position, binding);
                final ActivityOptions activityOptions = getActivityOptions(binding);

                MainActivity.this.startActivityForResult(intent, IntentUtil.REQUEST_CODE,
                        activityOptions.toBundle());
            }
        });
        empty.setVisibility(View.GONE);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(IntentUtil.RELEVANT_PHOTOS, relevantPhotos);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityReenter(int resultCode, Intent data) {
        postponeEnterTransition();
        // Start the postponed transition when the recycler view is ready to be drawn.
        grid.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                grid.getViewTreeObserver().removeOnPreDrawListener(this);
                startPostponedEnterTransition();
                return true;
            }
        });

        if (data == null) {
            return;
        }

        final int selectedItem = data.getIntExtra(IntentUtil.SELECTED_ITEM_POSITION, 0);
        grid.scrollToPosition(selectedItem);

        PhotoViewHolder holder = (PhotoViewHolder) grid.
                findViewHolderForAdapterPosition(selectedItem);
        if (holder == null) {
            Log.w(TAG, "onActivityReenter: Holder is null, remapping cancelled.");
            return;
        }
        DetailSharedElementEnterCallback callback =
                new DetailSharedElementEnterCallback(getIntent());
        callback.setBinding(holder.getBinding());
        setExitSharedElementCallback(callback);
    }

    private void setupRecyclerView() {
        GridLayoutManager gridLayoutManager = (GridLayoutManager) grid.getLayoutManager();
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                /* emulating https://material-design.storage.googleapis.com/publish/material_v_4/material_ext_publish/0B6Okdz75tqQsck9lUkgxNVZza1U/style_imagery_integration_scale1.png */
                switch (position % 6) {
                    case 5:
                        return 3;
                    case 3:
                        return 2;
                    default:
                        return 1;
                }
            }
        });
        grid.addItemDecoration(new GridMarginDecoration(
                getResources().getDimensionPixelSize(R.dimen.grid_item_spacing)));
        grid.setHasFixedSize(true);

    }

    @NonNull
    private static Intent getDetailActivityStartIntent(Activity host, ArrayList<Photo> photos,
                                                       int position, PhotoItemBinding binding) {
        final Intent intent = new Intent(host, DetailActivity.class);
        intent.setAction(Intent.ACTION_VIEW);
        intent.putParcelableArrayListExtra(IntentUtil.PHOTO, photos);
        intent.putExtra(IntentUtil.SELECTED_ITEM_POSITION, position);
        intent.putExtra(IntentUtil.FONT_SIZE, binding.author.getTextSize());
        intent.putExtra(IntentUtil.PADDING,
                new Rect(binding.author.getPaddingLeft(),
                        binding.author.getPaddingTop(),
                        binding.author.getPaddingRight(),
                        binding.author.getPaddingBottom()));
        intent.putExtra(IntentUtil.TEXT_COLOR, binding.author.getCurrentTextColor());
        return intent;
    }

    private ActivityOptions getActivityOptions(PhotoItemBinding binding) {
        Pair authorPair = Pair.create(binding.author, binding.author.getTransitionName());
        Pair photoPair = Pair.create(binding.photo, binding.photo.getTransitionName());
        View decorView = getWindow().getDecorView();
        View statusBackground = decorView.findViewById(android.R.id.statusBarBackground);
        View navBackground = decorView.findViewById(android.R.id.navigationBarBackground);
        Pair statusPair = Pair.create(statusBackground,
                statusBackground.getTransitionName());

        final ActivityOptions options;
        if (navBackground == null) {
            options = ActivityOptions.makeSceneTransitionAnimation(this,
                    authorPair, photoPair, statusPair);
        } else {
            Pair navPair = Pair.create(navBackground, navBackground.getTransitionName());
            options = ActivityOptions.makeSceneTransitionAnimation(this,
                    authorPair, photoPair, statusPair, navPair);
        }
        return options;
    }
}
