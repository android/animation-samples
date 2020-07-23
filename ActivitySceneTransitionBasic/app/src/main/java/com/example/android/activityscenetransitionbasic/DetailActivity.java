/*
 * Copyright (C) 2014 The Android Open Source Project
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

package com.example.android.activityscenetransitionbasic;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.transition.Transition;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;

import com.squareup.picasso.Picasso;

/**
 * Our secondary Activity which is launched from {@link MainActivity}. Has a simple detail UI
 * which has a large banner image, title and body text.
 */
public class DetailActivity extends AppCompatActivity {

    private static final String TAG = "DetailActivity";
    // Extra name for the ID parameter
    public static final String EXTRA_PARAM_ID = "detail:_id";

    // View name of the header image. Used for activity scene transitions
    public static final String VIEW_NAME_HEADER_IMAGE = "detail:header:image";

    // View name of the header title. Used for activity scene transitions
    public static final String VIEW_NAME_HEADER_TITLE = "detail:header:title";

    private ImageView mHeaderImageView;
    private TextView mHeaderTitle;

    private Item mItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate: ");
        setContentView(R.layout.details);

        // Retrieve the correct Item instance, using the ID provided in the Intent
        mItem = Item.getItem(getIntent().getIntExtra(EXTRA_PARAM_ID, 0));

        mHeaderImageView = findViewById(R.id.imageview_header);
        mHeaderTitle = findViewById(R.id.textview_title);

        // BEGIN_INCLUDE(detail_set_view_name)
        /*
         * Set the name of the view's which will be transition to, using the static values above.
         * This could be done in the layout XML, but exposing it via static variables allows easy
         * querying from other Activities
         */
        ViewCompat.setTransitionName(mHeaderImageView, VIEW_NAME_HEADER_IMAGE);
        ViewCompat.setTransitionName(mHeaderTitle, VIEW_NAME_HEADER_TITLE);
        // END_INCLUDE(detail_set_view_name)

        loadItem();
        Log.i(TAG, "onCreate: ");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart: ");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume: ");
    }

    private void loadItem() {
        // Set the title TextView to the item's name and author
        mHeaderTitle.setText(getString(R.string.image_header, mItem.getName(), mItem.getAuthor()));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && addTransitionListener()) {
            // If we're running on Lollipop and we have added a listener to the shared element
            // transition, load the thumbnail. The listener will load the full-size image when
            // the transition is complete.
            loadThumbnail();
        } else {
            // If all other cases we should just load the full-size image now
            loadFullSizeImage();
        }
    }

    /**
     * Load the item's thumbnail image into our {@link ImageView}.
     */
    private void loadThumbnail() {
        Picasso.with(mHeaderImageView.getContext())
                .load(mItem.getThumbnailUrl())
                .noFade()
                .into(mHeaderImageView);
    }

    /**
     * Load the item's full-size image into our {@link ImageView}.
     */
    private void loadFullSizeImage() {
        Picasso.with(mHeaderImageView.getContext())
                .load(mItem.getPhotoUrl())
                .noFade()
                .noPlaceholder()
                .into(mHeaderImageView);
    }

    /**
     * Try and add a {@link Transition.TransitionListener} to the entering shared element
     * {@link Transition}. We do this so that we can load the full-size image after the transition
     * has completed.
     *
     * @return true if we were successful in adding a listener to the enter transition
     */
    @RequiresApi(21)
    private boolean addTransitionListener() {
        final Transition transition = getWindow().getSharedElementEnterTransition();

        if (transition != null) {
            // There is an entering shared element transition so add a listener to it
            transition.addListener(new Transition.TransitionListener() {
                @Override
                public void onTransitionEnd(Transition transition) {
                    Log.i(TAG, "onTransitionEnd: ");
                    // As the transition has ended, we can now load the full-size image
                    loadFullSizeImage();

                    // Make sure we remove ourselves as a listener
                    transition.removeListener(this);
                    Intent intent = getIntent();
                    int x = intent.getIntExtra("location_x", 0);
                    int y = intent.getIntExtra("location_y", 0);
                    int w = intent.getIntExtra("width", 0);
                    int h = intent.getIntExtra("height", 0);
                    int[] array = new int[2];
                    mHeaderImageView.getLocationOnScreen(array);
                    int width =  mHeaderImageView.getRight() - mHeaderImageView.getLeft();
                    int height = mHeaderImageView.getBottom() - mHeaderImageView.getTop();
                    int ix = array[0];
                    int iy = array[1];
                    Log.i(TAG, "onTransitionEnd: 从 [(" + x + "," + y + ") (w " + w + ",h " + h
                            +")]  移动到 [(" + array[0] + "," + array[1] + ") (w " + width + ",h " + height +")]");
                    View view = getWindow().getDecorView();
                    view.getLocationOnScreen(array);
                    if(view instanceof FrameLayout) {
                        Log.i(TAG, "onTransitionStart: " + (view instanceof FrameLayout) + " x:" + array[0] + ",y" + array[1]);
                        ImageView iv = new ImageView(DetailActivity.this);
                        iv.setBackgroundColor(Color.RED);
                        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(width, height);
                        params.leftMargin = ix - array[0];
                        params.topMargin = iy - array[1];
                        iv.setLayoutParams(params);
//                        ((FrameLayout) view).addView(iv);
                    }
                }

                @Override
                public void onTransitionStart(Transition transition) {
                    // No-op
                    Log.i(TAG, "onTransitionStart: ");

                    Intent intent = getIntent();
                    int x = intent.getIntExtra("location_x", 0);
                    int y = intent.getIntExtra("location_y", 0);
                    int w = intent.getIntExtra("width", 0);
                    int h = intent.getIntExtra("height", 0);
                    int[] array = new int[2];
                    mHeaderImageView.getLocationOnScreen(array);
                    int width =  mHeaderImageView.getRight() - mHeaderImageView.getLeft();
                    int height = mHeaderImageView.getBottom() - mHeaderImageView.getTop();
                    Log.i(TAG, "onTransitionStart: 从 [(" + x + "," + y + ") (w " + w + ",h " + h
                            +")]  移动到 [(" + array[0] + "," + array[1] + ") (w " + width + ",h " + height +")]");
                    //TODO 拿到上个页面传过来的位置坐标，然后 addView() 然后执行动画效果
                    View view = getWindow().getDecorView();
                    view.getLocationOnScreen(array);
                    if(view instanceof FrameLayout) {
                        View sfl = findViewById(R.id.sfl);
                        Log.i(TAG, "onTransitionStart: " + (view instanceof FrameLayout) + " x:" + array[0] + ",y" + array[1]);
                        ImageView iv = new ImageView(DetailActivity.this);
                        iv.setBackgroundColor(Color.RED);
                        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(width, height);
                        params.leftMargin = x - array[0];
                        params.topMargin = y - array[1];
                        iv.setLayoutParams(params);
                        ((FrameLayout) view).addView(iv);
                        sfl.getLocationOnScreen(array);
                        Log.i(TAG, "onTransitionStart: sfl =>> x:" + array[0] + ",y" + array[1]);
                        float scaleX = 1440f / w;
                        float scaleY = 1440f / h;
                        float afterW = w * scaleX;
                        float afterH = h * scaleY;
                        float dX = (afterW - w) / 2;
                        float dY = (afterH - h) / 2;
                        iv.animate()
                                .scaleX(scaleX)
                                .scaleY(scaleY)
                                .translationXBy(array[0] - params.leftMargin + dX)
                                .translationYBy(array[1] - params.topMargin + dY)
                                .setDuration(1000).start();
                    }
                }

                @Override
                public void onTransitionCancel(Transition transition) {
                    // Make sure we remove ourselves as a listener
                    transition.removeListener(this);
                }

                @Override
                public void onTransitionPause(Transition transition) {
                    // No-op
                }

                @Override
                public void onTransitionResume(Transition transition) {
                    // No-op
                }
            });
            return true;
        }

        // If we reach here then we have not added a listener
        return false;
    }

}
