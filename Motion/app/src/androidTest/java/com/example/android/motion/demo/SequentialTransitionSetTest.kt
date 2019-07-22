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

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.transition.Fade
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SequentialTransitionSetTest {

    @Test
    fun setDuration_before() {
        val set = transitionSequential {
            duration = 100L
            this += Fade(Fade.OUT)
            this += Fade(Fade.IN)
        }

        assertThat(set[0].duration).isEqualTo(50L)
        assertThat(set[1].duration).isEqualTo(50L)
    }

    @Test
    fun setDuration_after() {
        val set = transitionSequential {
            this += Fade(Fade.OUT)
            this += Fade(Fade.IN)
            duration = 100L
        }

        assertThat(set[0].duration).isEqualTo(50L)
        assertThat(set[1].duration).isEqualTo(50L)
    }

    @Test
    fun setInterpolator_before() {
        val set = transitionSequential {
            interpolator = FAST_OUT_SLOW_IN
            this += Fade(Fade.OUT)
            this += Fade(Fade.IN)
        }

        set[0].interpolator!!.let { interpolator ->
            val si = interpolator as SegmentInterpolator
            assertThat(si.base).isSameAs(set.interpolator)
            assertThat(si.start).isEqualTo(0f)
            assertThat(si.end).isEqualTo(0.5f)
        }
        set[1].interpolator!!.let { interpolator ->
            val si = interpolator as SegmentInterpolator
            assertThat(si.base).isSameAs(set.interpolator)
            assertThat(si.start).isEqualTo(0.5f)
            assertThat(si.end).isEqualTo(1f)
        }
    }

    @Test
    fun setInterpolator_after() {
        val set = transitionSequential {
            this += Fade(Fade.OUT)
            this += Fade(Fade.IN)
            interpolator = FAST_OUT_SLOW_IN
        }

        set[0].interpolator!!.let { interpolator ->
            val si = interpolator as SegmentInterpolator
            assertThat(si.base).isSameAs(set.interpolator)
            assertThat(si.start).isEqualTo(0f)
            assertThat(si.end).isEqualTo(0.5f)
        }
        set[1].interpolator!!.let { interpolator ->
            val si = interpolator as SegmentInterpolator
            assertThat(si.base).isSameAs(set.interpolator)
            assertThat(si.start).isEqualTo(0.5f)
            assertThat(si.end).isEqualTo(1f)
        }
    }

    @Test
    fun weight() {
        val set = transitionSequential {
            addTransition(Fade(Fade.OUT), 2f)
            addTransition(Fade(Fade.OUT), 3f)
            interpolator = FAST_OUT_SLOW_IN
        }

        set[0].interpolator!!.let { interpolator ->
            val si = interpolator as SegmentInterpolator
            assertThat(si.base).isSameAs(set.interpolator)
            assertThat(si.start).isEqualTo(0f)
            assertThat(si.end).isEqualTo(0.4f)
            assertThat(si.getInterpolation(0f)).isWithin(0.01f).of(0f)
            assertThat(si.getInterpolation(1f)).isWithin(0.01f).of(1f)
        }
        set[1].interpolator!!.let { interpolator ->
            val si = interpolator as SegmentInterpolator
            assertThat(si.base).isSameAs(set.interpolator)
            assertThat(si.start).isEqualTo(0.4f)
            assertThat(si.end).isEqualTo(1f)
            assertThat(si.getInterpolation(0f)).isWithin(0.01f).of(0f)
            assertThat(si.getInterpolation(1f)).isWithin(0.01f).of(1f)
        }
    }
}
