/*
 * Copyright (C) 2021 The Android Open Source Project
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

package com.example.android.compose.motion.demo

import androidx.compose.runtime.Composable
import com.example.android.compose.motion.demo.custom.CustomCanvasBouncyLoader
import com.example.android.compose.motion.demo.fade.FadeDemo
import com.example.android.compose.motion.demo.fadethrough.FadeThroughDemo
import com.example.android.compose.motion.demo.loading.LoadingDemo
import com.example.android.compose.motion.demo.sharedaxis.SharedAxisDemo
import com.example.android.compose.motion.demo.sharedtransform.SharedTransformDemo
import com.example.android.compose.motion.demo.visibility.AnimatedVisibilityExample

enum class Demo(
    val title: String,
    val description: String,
    val apis: List<String>,
    val content: @Composable () -> Unit
) {
    Fade(
        title = "Layout > Fade",
        description = """
            A fade creates a smooth sequence between elements that fully overlap each other, such as
            photos inside of a card or another container. When a new element enters, it fades in
            over the current element.
        """.trimIndent().replace('\n', ' '),
        apis = listOf(
            "AnimatedContent"
        ),
        content = { FadeDemo() }
    ),

    FadeThrough(
        title = "Layout > Fade through",
        description = """
            Fade through involves one element fading out completely before a new one fades in. These
            transitions can be applied to text, icons, and other elements that don't perfectly
            overlap. This technique lets the background show through during a transition, and it can
            provide continuity between screens when paired with a shared transformation.
        """.trimIndent().replace('\n', ' '),
        apis = listOf(
            "AnimatedContent"
        ),
        content = { FadeThroughDemo() }
    ),

    SharedTransform(
        title = "Layout > Shared transform",
        description = """
            Complex layout changes use a shared transformation to create smooth transitions from
            one layout to the next. Elements are grouped together and transform as a single unit,
            rather than animating independently. This avoids multiple transformations overlapping
            and competing for attention.
        """.trimIndent().replace('\n', ' '),
        apis = listOf(
            "updateTransition", "Transition.AnimatedContent"
        ),
        content = { SharedTransformDemo() }
    ),

    SharedAxis(
        title = "Layout > Shared axis (Y-axis)",
        description = """
            The shared axis pattern is used for transitions between UI elements that have a spatial
            or navigational relationship. This demo uses a shared transformation on the Y-axis to
            reinforce the sequential order of elements.
        """.trimIndent().replace('\n', ' '),
        apis = listOf(
            "AnimatedContent"
        ),
        content = { SharedAxisDemo() }
    ),
    AnimatedVisibility(
        title = "Layout > Animating a view's visibility",
        description = """
            Animating the views visibility from visible to invisible
        """.trimIndent(),
        apis = listOf("AnimatedVisibility"),
        content = { AnimatedVisibilityExample() }
    ),
    Loading(
        title = "List > Loading",
        description = """
            Motion provides timely feedback and the status of user actions. An animated placeholder
            UI can indicate that content is loading.
        """.trimIndent().replace('\n', ' '),
        apis = listOf(
            "Modifier.placeholder", "AnimationSpec"
        ),
        content = { LoadingDemo() }
    ),
    CustomCanvasAnimation(
        title = "Custom > Custom Drawing Canvas Animation",
        description = """
           A custom loading animation example using Canvas and draw APIs, combined with 
           Animatable to show the use of the animateTo() function used sequentially.
        """.trimIndent().replace('\n', ' '),
        apis = listOf(
            "Animatable"
        ),
        content = { CustomCanvasBouncyLoader() }
    )
}
