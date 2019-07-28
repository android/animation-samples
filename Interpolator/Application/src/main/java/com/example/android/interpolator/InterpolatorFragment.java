/*
* Copyright 2014 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.example.android.interpolator;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.graphics.Path;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.android.common.logger.Log;

/**
 * This sample demonstrates the use of animation interpolators and path animations for
 * Material Design.
 * It shows how an {@link android.animation.ObjectAnimator} is used to animate two properties of a
 * view (scale X and Y) along a path.
 */
public class InterpolatorFragment extends Fragment {

    /**
     * View that is animated.
     */
    private View mView;
    /**
     * Spinner for selection of interpolator.
     */
    private Spinner mInterpolatorSpinner;
    /**
     * SeekBar for selection of duration of animation.
     */
    private SeekBar mDurationSeekbar;
    /**
     * TextView that shows animation selected in SeekBar.
     */
    private TextView mDurationLabel;

    /**
     * Interpolators used for animation.
     */
    private Interpolator mInterpolators[];
    /**
     * Path for in (shrinking) animation, from 100% scale to 20%.
     */
    private Path mPathIn;
    /**
     * Path for out (growing) animation, from 20% to 100%.
     */
    private Path mPathOut;

    /**
     * Set to true if View is animated out (is shrunk).
     */
    private boolean mIsOut = false;

    /**
     * Default duration of animation in ms.
     */
    private static final int INITIAL_DURATION_MS = 750;

    /**
     * String used for logging.
     */
    public static final String TAG = "InterpolatorPlaygroundFragment";

    /**
     * Names of the available interpolators.
     */
    private String[] mInterpolatorNames;

    public InterpolatorFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initInterpolators();
        mInterpolatorNames = getResources().getStringArray(R.array.interpolator_names);
        initPaths();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.interpolator_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        initAnimateButton(view);

        // Get the label to display the selected duration
        mDurationLabel = (TextView) view.findViewById(R.id.durationLabel);

        // Set up the Spinner with the names of interpolators.
        mInterpolatorSpinner = (Spinner) view.findViewById(R.id.interpolatorSpinner);
        ArrayAdapter<String> spinnerAdapter =
                new ArrayAdapter<String>(getActivity(),
                        android.R.layout.simple_spinner_dropdown_item, mInterpolatorNames);
        mInterpolatorSpinner.setAdapter(spinnerAdapter);
        initSeekbar(view);


        // Get the view that will be animated
        mView = view.findViewById(R.id.square);

        super.onViewCreated(view, savedInstanceState);
    }

    /**
     * Set up the 'animate' button, when it is clicked the view is animated with the options
     * selected: the Interpolator, duration and animation path
     *
     * @param view The view holding the button.
     */
    private void initAnimateButton(View view) {
        View button = view.findViewById(R.id.animateButton);
        button.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onClick(View view) {
                // Interpolator selected in the spinner
                int selectedItemPosition = mInterpolatorSpinner.getSelectedItemPosition();
                Interpolator interpolator = mInterpolators[selectedItemPosition];
                // Duration selected in SeekBar
                long duration = mDurationSeekbar.getProgress();
                // Animation path is based on whether animating in or out
                Path path = mIsOut ? mPathIn : mPathOut;

                // Log animation details
                Log.i(TAG, String.format("Starting animation: [%d ms, %s, %s]",
                        duration, (String) mInterpolatorSpinner.getSelectedItem(),
                        ((mIsOut) ? "Out (growing)" : "In (shrinking)")));

                // Start the animation with the selected options
                startAnimation(interpolator, duration, path);

                // Toggle direction of animation (path)
                mIsOut = !mIsOut;
            }
        });
    }

    /**
     * Set up SeekBar that defines the duration of the animation
     *
     * @param view The view holding the button.
     */
    private void initSeekbar(View view) {
        mDurationSeekbar = (SeekBar) view.findViewById(R.id.durationSeek);

        // Register listener to update the text label when the SeekBar value is updated
        mDurationSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                mDurationLabel.setText(getResources().getString(R.string.animation_duration, i));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        // Set initial progress to trigger SeekBarChangeListener and update UI
        mDurationSeekbar.setProgress(INITIAL_DURATION_MS);
    }

    /**
     * Start an animation on the sample view.
     * The view is animated using an {@link android.animation.ObjectAnimator} on the
     * {@link View#SCALE_X} and {@link View#SCALE_Y} properties, with its animation based on a
     * path.
     * The only two paths defined here ({@link #mPathIn} and {@link #mPathOut}) scale the view
     * uniformly.
     *
     * @param interpolator The interpolator to use for the animation.
     * @param duration Duration of the animation in ms.
     * @param path Path of the animation
     * @return The ObjectAnimator used for this animation
     * @see android.animation.ObjectAnimator#ofFloat(Object, String, String, android.graphics.Path)
     */
    public ObjectAnimator startAnimation(Interpolator interpolator, long duration, Path path) {
        // This ObjectAnimator uses the path to change the x and y scale of the mView object.
        ObjectAnimator animator = ObjectAnimator.ofFloat(mView, View.SCALE_X, View.SCALE_Y, path);

        // Set the duration and interpolator for this animation
        animator.setDuration(duration);
        animator.setInterpolator(interpolator);

        animator.start();

        return animator;
    }

    /**
     * Return the array of loaded Interpolators available in this Fragment.
     *
     * @return Interpolators
     */
    public Interpolator[] getInterpolators() {
        return mInterpolators;
    }

    /**
     * @return The animation path for the 'in' (shrinking) animation.
     */
    public Path getPathIn() {
        return mPathIn;
    }

    /**
     * @return The animation path for the 'out' (growing) animation.
     */
    public Path getPathOut() {
        return mPathOut;
    }

    /**
     * Initialize interpolators programmatically by loading them from their XML definitions
     * provided by the framework.
     */
    private void initInterpolators() {
        mInterpolators = new Interpolator[]{
                AnimationUtils.loadInterpolator(getActivity(),
                        android.R.interpolator.linear),
                AnimationUtils.loadInterpolator(getActivity(),
                        android.R.interpolator.fast_out_linear_in),
                AnimationUtils.loadInterpolator(getActivity(),
                        android.R.interpolator.fast_out_slow_in),
                AnimationUtils.loadInterpolator(getActivity(),
                        android.R.interpolator.linear_out_slow_in)
        };
    }

    /**
     * Initializes the paths that are used by the ObjectAnimator to scale the view.
     */
    private void initPaths() {
        // Path for 'in' animation: growing from 20% to 100%
        mPathIn = new Path();
        mPathIn.moveTo(0.2f, 0.2f);
        mPathIn.lineTo(1f, 1f);

        // Path for 'out' animation: shrinking from 100% to 20%
        mPathOut = new Path();
        mPathOut.moveTo(1f, 1f);
        mPathOut.lineTo(0.2f, 0.2f);
    }
}