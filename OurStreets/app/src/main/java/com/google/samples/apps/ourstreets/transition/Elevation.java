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

package com.google.samples.apps.ourstreets.transition;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.transition.Transition;
import android.transition.TransitionValues;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;


/**
 * Transitions target {@link View}s elevation property to/from zero
 * when it's entering/exiting a scene.
 */
public class Elevation extends Transition {

    private static final String PROP_ELEVATION = "ourstreets:elevation";

    private static final String[] PROPERTIES = {PROP_ELEVATION};

    /**
     * Constructs an Elevation object with no target objects.
     */
    public Elevation() {
        super();
    }

    /**
     * Perform inflation from XML and apply a class-specific base style
     * from a theme attribute or style resource.
     *
     * @param context The Context the transition is running in, through which it can
     * access the current theme, resources, etc.
     * @param attrs The attributes of the XML tag that is inflating the transition.
     */
    public Elevation(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void captureStartValues(TransitionValues transitionValues) {
        captureValues(transitionValues);
    }

    @Override
    public void captureEndValues(TransitionValues transitionValues) {
        captureValues(transitionValues);
    }

    private void captureValues(TransitionValues transitionValues) {
        transitionValues.values.put(PROP_ELEVATION, transitionValues.view.getElevation());
    }

    @Override
    public String[] getTransitionProperties() {
        return PROPERTIES;
    }

    @Override
    public Animator createAnimator(ViewGroup sceneRoot, TransitionValues startValues,
                                   TransitionValues endValues) {
        if (startValues == null && endValues == null) {
            return null; // nothing to do here
        }

        boolean isEntering = startValues == null;

        TransitionValues values = isEntering ? endValues : startValues;
        View target = values.view;

        float targetElevation = (float) values.values.get(PROP_ELEVATION);
        float startElevation = isEntering ? 0f : targetElevation;
        float endElevation = isEntering ? targetElevation : 0f;

        //noinspection ResourceType
        target.setElevation(startElevation);

        return ObjectAnimator.ofFloat(target, View.TRANSLATION_Z, endElevation);
    }

}
