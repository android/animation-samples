/*
* Copyright 2022 The Android Open Source Project
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
package com.example.android.compose.motion.demo.custom

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
@Preview
fun CustomCanvasBouncyLoader() {
    val yOffset = remember {
        Animatable(0f)
    }
    val scale = remember {
        Animatable(1f)
    }
    LaunchedEffect("bouncyLoader") {
        delay(400)
        // We use the Animatable.animateTo() API here to demonstrate the coroutine usage - each
        // item is animating one after the other, as the animateTo function is sequential.
        // Animate y to half the height
        yOffset.animateTo(0.5f, bouncyAnimationSpec)
        scale.animateTo(3f, bouncyAnimationSpec)
        delay(500)
        scale.animateTo(10f, bouncyAnimationSpec)
        delay(500)
        scale.animateTo(50f, bouncyAnimationSpec)
    }
    val size = remember {
        mutableStateOf(IntSize.Zero)
    }
    Box(
        Modifier.fillMaxSize()
            .onSizeChanged {
                // We get the size change of the whole composable, and use this to determine how
                // big the ball should be.
                size.value = it
            }) {
        GradientCircle(
            Modifier.align(Alignment.TopCenter)
                .size(26.dp)
                .graphicsLayer {
                    // We use .graphicsLayer here to perform the animation as we are animating
                    // multiple properties of our Gradient circle at once, and this is more
                    // efficient than using multiple modifiers.
                    // .graphicsLayer also defers these changes to the Draw phase of Compose,
                    // therefore minimizing recompositions required to do this.
                    scaleX = scale.value
                    scaleY = scale.value
                    translationY = yOffset.value * size.value.height
                }
        )
    }
}

@Composable
fun GradientCircle(modifier: Modifier = Modifier) {
    val brush = remember {
        Brush.verticalGradient(listOf(Color(0xFFF56E34), Color(0xFF234EDA)))
    }
    Canvas(modifier = modifier) {
        drawCircle(brush = brush)
    }
}

val bouncyAnimationSpec: SpringSpec<Float> = spring(
    dampingRatio = Spring.DampingRatioMediumBouncy,
    stiffness = Spring.StiffnessLow
)