/*
 * Copyright 2019 The Android Open Source Project
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

package com.example.android.motion.demo

import android.animation.TimeInterpolator
import androidx.core.view.animation.PathInterpolatorCompat

/**
 * Standard easing.
 *
 * Elements that begin and end at rest use standard easing. They speed up quickly and slow down
 * gradually, in order to emphasize the end of the transition.
 */
val FAST_OUT_SLOW_IN: TimeInterpolator by lazy(LazyThreadSafetyMode.NONE) {
    PathInterpolatorCompat.create(0.4f, 0f, 0.2f, 1f)
}

/**
 * Decelerate easing.
 *
 * Incoming elements are animated using deceleration easing, which starts a transition at peak
 * velocity (the fastest point of an element’s movement) and ends at rest.
 */
val LINEAR_OUT_SLOW_IN: TimeInterpolator by lazy(LazyThreadSafetyMode.NONE) {
    PathInterpolatorCompat.create(0f, 0f, 0.2f, 1f)
}

/**
 * Accelerate easing.
 *
 * Elements exiting a screen use acceleration easing, where they start at rest and end at peak
 * velocity.
 */
val FAST_OUT_LINEAR_IN: TimeInterpolator by lazy(LazyThreadSafetyMode.NONE) {
    PathInterpolatorCompat.create(0.4f, 0f, 1f, 1f)
}
