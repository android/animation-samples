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

import androidx.transition.ChangeBounds
import androidx.transition.Fade
import androidx.transition.Transition
import androidx.transition.TransitionSet

/**
 * Creates a transition like [androidx.transition.AutoTransition], but customized to be more
 * true to Material Design.
 *
 * Fade through involves one element fading out completely before a new one fades in. These
 * transitions can be applied to text, icons, and other elements that don't perfectly overlap.
 * This technique lets the background show through during a transition, and it can provide
 * continuity between screens when paired with a shared transformation.
 *
 * See
 * [Expressing continuity](https://material.io/design/motion/understanding-motion.html#expressing-continuity)
 * for the detail of fade through.
 */
fun fadeThrough(duration: Long = 300L): Transition {
    return transitionSet {
        ordering = TransitionSet.ORDERING_TOGETHER
        addTransition(
            ChangeBounds()
                .setDuration(duration)
                .setInterpolator(FAST_OUT_SLOW_IN)
        )
        addTransition(transitionSet {
            ordering = TransitionSet.ORDERING_SEQUENTIAL
            this.duration = duration / 2
            addTransition(Fade(Fade.OUT).setInterpolator(FAST_OUT_LINEAR_IN))
            addTransition(Fade(Fade.IN).setInterpolator(LINEAR_OUT_SLOW_IN))
        })
    }
}

inline fun transitionSet(crossinline body: TransitionSet.() -> Unit): TransitionSet {
    return TransitionSet().apply(body)
}
