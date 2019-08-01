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

package com.example.android.motion.demo.sharedelement

import android.animation.Animator
import android.animation.ObjectAnimator
import android.view.View
import android.view.ViewGroup
import androidx.transition.Transition
import androidx.transition.TransitionValues

private const val PROPNAME_IS_MIRROR = "com.example.android.motion.demo:is_mirror"

private val MIRROR_PROPERTIES = arrayOf(PROPNAME_IS_MIRROR)

/**
 * Transitions between a view and its copy by [MirrorView].
 *
 * This can be typically used in a shared element transition where the shared element is necessary
 * only during the animation. The shared element needs to exist and laid out on both sides of the
 * transition in order to animate between them, but it can be wasteful to create the exact same view
 * on the side where it is not functional. This transition matches the substance and its mirror and
 * animate between them. Depending on which of the start or the end state is the substance of
 * [MirrorView], the animation either fades into it or fades out of it.
 *
 * This can be combined with other [Transition]s. For example, ChangeTransform can translate the
 * position of the substance view or the mirror view along with this transition.
 */
class SharedFade : Transition() {

    override fun getTransitionProperties(): Array<String>? {
        return MIRROR_PROPERTIES
    }

    private fun captureMirrorValues(transitionValues: TransitionValues) {
        val view = transitionValues.view ?: return
        transitionValues.values[PROPNAME_IS_MIRROR] = view is MirrorView
    }

    override fun captureStartValues(transitionValues: TransitionValues) {
        captureMirrorValues(transitionValues)
    }

    override fun captureEndValues(transitionValues: TransitionValues) {
        captureMirrorValues(transitionValues)
    }

    override fun createAnimator(
        sceneRoot: ViewGroup,
        startValues: TransitionValues?,
        endValues: TransitionValues?
    ): Animator? {
        if (startValues == null || endValues == null) {
            return null
        }
        val startView = startValues.view ?: return null
        val endView = endValues.view ?: return null
        if (startView is MirrorView) {
            // The view is appearing. We animate the substance view.
            // The MirrorView was used merely for matching the layout position by other Transitions.
            return ObjectAnimator.ofFloat(endView, View.ALPHA, 0f, 0f, 1f)
        } else if (endView is MirrorView) { // Disappearing
            // The view is disappearing. We mirror the substance view, and animate the MirrorView.
            endView.substance = startView
            return ObjectAnimator.ofFloat(endView, View.ALPHA, 1f, 0f, 0f)
        }
        return null
    }
}
