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

/**
 * Takes a [base] interpolator and extracts out a segment from it as a new [TimeInterpolator].
 *
 * This is useful for sequential animations where each of the child animations should be
 * interpolated so that they match with another animation when combined.
 */
class SegmentInterpolator(
    val base: TimeInterpolator,
    val start: Float = 0f,
    val end: Float = 1f
) : TimeInterpolator {

    private val offset = base.getInterpolation(start)
    private val xRatio = (end - start) / 1f
    private val yRatio = (base.getInterpolation(end) - offset) / 1f

    override fun getInterpolation(input: Float): Float {
        return (base.getInterpolation(start + (input * xRatio)) - offset) / yRatio
    }
}
