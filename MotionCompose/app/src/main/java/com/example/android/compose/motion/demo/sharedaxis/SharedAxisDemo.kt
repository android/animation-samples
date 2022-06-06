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

package com.example.android.compose.motion.demo.sharedaxis

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.datasource.LoremIpsum
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.android.compose.motion.demo.CheeseImages
import com.example.android.compose.motion.demo.CheeseNames
import com.example.android.compose.motion.demo.Demo
import com.example.android.compose.motion.demo.SimpleScaffold
import com.example.android.compose.motion.ui.MotionComposeTheme

@Composable
fun SharedAxisDemo() {
    SimpleScaffold(title = Demo.SharedAxis.title) {
        val pages = remember { createPages() }
        // Indicator column
        var id by rememberSaveable { mutableStateOf(1) }
        Row(modifier = Modifier.padding(end = 16.dp)) {
            PageIndicatorsColumn(
                pages = pages,
                selectedId = id,
                onIndicatorClick = { id = it }
            )

            // SharedYAxis animates the content change.
            SharedYAxis(targetState = pages.first { it.id == id }) { page ->
                PageContent(page = page)
            }
        }
    }
}

/**
 * Animates content change with the vertical shared axis pattern.
 *
 * See [Shared axis](https://material.io/design/motion/the-motion-system.html#shared-axis) for the
 * detail about this motion pattern.
 */
@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun <T : Comparable<T>> SharedYAxis(
    targetState: T,
    modifier: Modifier = Modifier,
    content: @Composable AnimatedVisibilityScope.(T) -> Unit
) {
    val exitDurationMillis = 80
    val enterDurationMillis = 220

    // This local function creates the AnimationSpec for outgoing elements.
    fun <T> exitSpec(): FiniteAnimationSpec<T> =
        tween(
            durationMillis = exitDurationMillis,
            easing = FastOutLinearInEasing
        )

    // This local function creates the AnimationSpec for incoming elements.
    fun <T> enterSpec(): FiniteAnimationSpec<T> =
        tween(
            // The enter animation runs right after the exit animation.
            delayMillis = exitDurationMillis,
            durationMillis = enterDurationMillis,
            easing = LinearOutSlowInEasing
        )

    val slideDistance = with(LocalDensity.current) { 30.dp.roundToPx() }

    AnimatedContent(
        targetState = targetState,
        transitionSpec = {
            // The state type (<T>) is Comparable.
            // We compare the initial state and the target state to determine whether we are moving
            // down or up.
            if (initialState < targetState) { // Move down
                ContentTransform(
                    // Outgoing elements fade out and slide up to the top.
                    initialContentExit = fadeOut(exitSpec()) +
                            slideOutVertically(exitSpec()) { -slideDistance },
                    // Incoming elements fade in and slide up from the bottom.
                    targetContentEnter = fadeIn(enterSpec()) +
                            slideInVertically(enterSpec()) { slideDistance }
                )
            } else { // Move up
                ContentTransform(
                    // Outgoing elements fade out and slide down to the bottom.
                    initialContentExit = fadeOut(exitSpec()) +
                            slideOutVertically(exitSpec()) { slideDistance },
                    // Outgoing elements fade in and slide down from the top.
                    targetContentEnter = fadeIn(enterSpec()) +
                            slideInVertically(enterSpec()) { -slideDistance }
                )
            }
        },
        modifier = modifier,
        content = content
    )
}

private class Page(
    val id: Int,
    @DrawableRes
    val image: Int,
    val title: String,
    val body: String
) : Comparable<Page> {

    override fun compareTo(other: Page): Int {
        return id.compareTo(other.id)
    }
}

private fun createPages(): List<Page> {
    val body = LoremIpsum().values.joinToString(separator = " ").replace('\n', ' ')
    return (0..4).map { i ->
        Page(
            id = i + 1,
            image = CheeseImages[i % CheeseImages.size],
            title = CheeseNames[i * 128],
            body = body
        )
    }
}

@Composable
private fun PageIndicatorsColumn(
    pages: List<Page>,
    selectedId: Int,
    onIndicatorClick: (index: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(32.dp),
    ) {
        for (page in pages) {
            PageIndicator(
                index = page.id,
                selected = selectedId == page.id,
                onClick = { onIndicatorClick(page.id) }
            )
        }
    }
}

@Composable
private fun PageIndicator(
    index: Int,
    selected: Boolean,
    onClick: () -> Unit
) {
    val transition = updateTransition(targetState = selected, label = "indicator")
    val backgroundColor by transition.animateColor(label = "background color") { targetSelected ->
        if (targetSelected) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.surfaceVariant
        }
    }
    val textColor by transition.animateColor(label = "text color") { targetSelected ->
        if (targetSelected) {
            MaterialTheme.colorScheme.onPrimary
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        }
    }
    IconButton(onClick = onClick) {
        Text(
            text = index.toString(),
            modifier = Modifier
                .size(32.dp)
                .background(backgroundColor, CircleShape)
                .wrapContentSize(),
            color = textColor,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun PageContent(
    page: Page,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .navigationBarsPadding(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Image(
            painter = painterResource(page.image),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
                .clip(RoundedCornerShape(16.dp)),
            contentScale = ContentScale.Crop
        )
        Text(
            text = page.title,
            style = MaterialTheme.typography.titleLarge
        )
        Text(
            text = page.body,
            textAlign = TextAlign.Justify
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewSharedAxisDemo() {
    MotionComposeTheme {
        SharedAxisDemo()
    }
}
