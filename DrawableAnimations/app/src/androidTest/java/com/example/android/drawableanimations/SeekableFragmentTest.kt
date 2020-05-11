/*
 * Copyright 2020 Google LLC
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

package com.example.android.drawableanimations

import android.widget.Button
import androidx.core.animation.AnimatorTestRule
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.android.drawableanimations.demo.seekable.SeekableFragment
import com.google.common.truth.Truth.assertThat
import org.junit.ClassRule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SeekableFragmentTest {

    companion object {
        @ClassRule
        @JvmField
        val animatorTestRule = AnimatorTestRule()
    }

    @Test
    fun start() {
        launchFragmentInContainer { SeekableFragment() }.onFragment { fragment ->
            val view = fragment.view!!
            val start: Button = view.findViewById(R.id.start)

            assertThat(start.text.toString()).isEqualTo("Start")
            start.performClick()
            animatorTestRule.advanceTimeBy(800L)
            assertThat(start.text.toString()).isEqualTo("Pause")
            start.performClick()
            assertThat(start.text.toString()).isEqualTo("Resume")
        }
    }
}
