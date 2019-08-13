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
import androidx.transition.Transition
import androidx.transition.TransitionSet

/**
 * Runs multiple [Transition]s sequentially.
 *
 * Setting a duration to this set will distribute the duration to each child transition based on its
 * weight. Interpolator is also segmented and applied to the transitions.
 *
 * (Background) A normal [TransitionSet] simply sets its properties to child transitions as they
 * are. This can be problematic for sequential transition sets. For example, setting a duration of
 * 300ms means that the entire duration will be the multiple of 300ms and the child count.
 */
class SequentialTransitionSet : TransitionSet() {

    init {
        ordering = ORDERING_SEQUENTIAL
    }

    private var _duration: Long = -1
    private var _interpolator: TimeInterpolator? = null

    private val weights = mutableListOf<Float>()

    override fun setOrdering(ordering: Int): TransitionSet {
        if (ordering != ORDERING_SEQUENTIAL) {
            throw IllegalArgumentException(
                "SequentialTransitionSet only supports ORDERING_SEQUENTIAL"
            )
        }
        return super.setOrdering(ordering)
    }

    fun addTransition(transition: Transition, weight: Float): TransitionSet {
        super.addTransition(transition)
        weights += weight
        distributeDuration()
        distributeInterpolator()
        return this
    }

    override fun addTransition(transition: Transition): TransitionSet {
        return addTransition(transition, 1f)
    }

    override fun setDuration(duration: Long): TransitionSet {
        // Don't call super.
        _duration = duration
        distributeDuration()
        return this
    }

    override fun getDuration(): Long {
        return _duration
    }

    private fun distributeDuration() {
        if (_duration < 0) {
            forEach { transition ->
                transition.duration = -1
            }
            return
        }
        val totalWeight = weights.sum()
        forEachIndexed { i, transition ->
            transition.duration = (_duration * weights[i] / totalWeight).toLong()
        }
    }

    override fun setInterpolator(interpolator: TimeInterpolator?): TransitionSet {
        // Don't call super.
        _interpolator = interpolator
        distributeInterpolator()
        return this
    }

    override fun getInterpolator(): TimeInterpolator? {
        return _interpolator
    }

    private fun distributeInterpolator() {
        val interpolator = _interpolator
        if (interpolator == null) {
            forEach { transition ->
                transition.interpolator = null
            }
            return
        }
        val totalWeight = weights.sum()
        var start = 0f
        forEachIndexed { i, transition ->
            val range = weights[i] / totalWeight
            transition.interpolator = SegmentInterpolator(
                interpolator,
                start,
                start + range
            )
            start += range
        }
    }
}
