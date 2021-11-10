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
import com.example.android.compose.motion.demo.fadethrough.FadeThroughDemo

enum class Demo(
    val title: String,
    val description: String,
    val apis: List<String>,
    val content: @Composable () -> Unit
) {
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
    )
}
