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

package com.example.android.motion.demo.dissolve

import android.animation.Animator
import android.animation.ObjectAnimator
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.view.ViewGroup
import android.view.ViewOverlay
import androidx.core.animation.doOnEnd
import androidx.core.view.drawToBitmap
import androidx.transition.Transition
import androidx.transition.TransitionValues

/**
 * Dissolve animation pattern implemented as a [Transition].
 *
 * A dissolve creates a smooth transition between elements that completely overlap one another,
 * such as photos inside a card or other container. A foreground element fades in (appears) or out
 * (disappears) to show or hide an element behind it.
 *
 * See
 * [Expressing continuity](https://material.io/design/motion/understanding-motion.html#expressing-continuity)
 * for the detail.
 */
class Dissolve : Transition() {

    companion object {
        private const val PROPNAME_BITMAP = "com.example.android.motion.demo.dissolve:bitmap"

        /**
         * This transition depends on [ViewOverlay] to show the animation. On older devices that
         * don't support it, this transition doesn't do anything.
         */
        private val SUPPORTS_VIEW_OVERLAY = Build.VERSION.SDK_INT >= 18
    }

    override fun captureStartValues(transitionValues: TransitionValues) {
        captureValues(transitionValues)
    }

    override fun captureEndValues(transitionValues: TransitionValues) {
        captureValues(transitionValues)
    }

    private fun captureValues(transitionValues: TransitionValues) {
        if (SUPPORTS_VIEW_OVERLAY) {
            // Store the current appearance of the view as a Bitmap.
            transitionValues.values[PROPNAME_BITMAP] = transitionValues.view.drawToBitmap()
        }
    }

    override fun createAnimator(
        sceneRoot: ViewGroup,
        startValues: TransitionValues?,
        endValues: TransitionValues?
    ): Animator? {
        if (startValues == null || endValues == null || !SUPPORTS_VIEW_OVERLAY) {
            return null
        }
        val startBitmap = startValues.values[PROPNAME_BITMAP] as Bitmap
        val endBitmap = endValues.values[PROPNAME_BITMAP] as Bitmap

        // No need to animate if the start and the end look identical.
        if (startBitmap.sameAs(endBitmap)) {
            return null
        }

        val view = endValues.view
        val startDrawable = BitmapDrawable(view.resources, startBitmap).apply {
            setBounds(0, 0, startBitmap.width, startBitmap.height)
        }

        // Use ViewOverlay to show the start bitmap on top of the view that is currently showing the
        // end state. This allows us to overlap the start and end states during the animation.
        val overlay = view.overlay
        overlay.add(startDrawable)

        // Fade out the start bitmap.
        return ObjectAnimator
            // Use [BitmapDrawable#setAlpha(int)] to animate the alpha value.
            .ofInt(startDrawable, "alpha", 255, 0).apply {
                doOnEnd {
                    // Remove the start state from the overlay when the animation is over.
                    // The drawable is completely transparent at this point, but we don't want to
                    // leave it there.
                    overlay.remove(startDrawable)
                }
            }
    }
}
