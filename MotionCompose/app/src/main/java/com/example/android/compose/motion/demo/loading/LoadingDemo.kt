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

package com.example.android.compose.motion.demo.loading

import android.os.SystemClock
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.StartOffset
import androidx.compose.animation.core.StartOffsetType
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemsIndexed
import com.example.android.compose.motion.demo.Cheese
import com.example.android.compose.motion.demo.Demo
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.rememberInsetsPaddingValues
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.fade
import com.google.accompanist.placeholder.placeholder

@Composable
fun LoadingDemo() {
    val viewModel: LoadingViewModel = viewModel()

    // In this demo, we abandon the cached items and restart loading every time this count is
    // incremented. This way, we can show the loading animation again.
    // In a real-life app, you should use `LazyPagingItems#refresh()` which can refresh
    // the content without abandoning the current content.
    var refreshCount by remember { mutableStateOf(0) }

    LoadingDemoContent(
        cheeses = key(refreshCount) { viewModel.cheeses.collectAsLazyPagingItems() },
        onRefresh = { refreshCount++ }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LoadingDemoContent(
    cheeses: LazyPagingItems<Cheese>,
    onRefresh: () -> Unit
) {
    val systemBars = LocalWindowInsets.current.systemBars
    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = {
                    Text(text = Demo.Loading.title)
                },
                modifier = Modifier
                    .padding(
                        rememberInsetsPaddingValues(
                            insets = systemBars,
                            applyBottom = false
                        )
                    ),
                actions = {
                    IconButton(onClick = onRefresh) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh"
                        )
                    }
                }
            )
        }
    ) {
        val startTimeMillis = remember(cheeses) { SystemClock.uptimeMillis() }
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = rememberInsetsPaddingValues(
                insets = systemBars,
                applyTop = false
            )
        ) {
            itemsIndexed(items = cheeses) { index, cheese ->
                // Calculate the offset used to animate the placeholder.
                // Note that we calculate this with `updateMillis` to compensate for the timing gap
                // between items displayed during scrolling.
                // After that, we offset each item by 80 milliseconds.
                val offset = (SystemClock.uptimeMillis() - startTimeMillis).toInt() - index * 80
                CheeseItem(
                    cheese = cheese,
                    animationOffsetMillis = offset,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

/**
 * Shows one item in the list.
 *
 * @param cheese The item to be shown. Or `null` if it is still loading.
 * @param animationOffsetMillis The offset for the placeholder animation in milliseconds.
 * @param modifier The modifier.
 */
@Composable
private fun CheeseItem(
    cheese: Cheese?,
    animationOffsetMillis: Int,
    modifier: Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // We can use the placeholder modifier of Accompanist to easily show placeholder content
        // when the item is null (= still loading).
        val placeholderModifier = Modifier.placeholder(
            // Show the placeholder when the item is null.
            visible = cheese == null,
            // The color for the placeholder.
            color = MaterialTheme.colorScheme.surfaceVariant,
            // Specify the highlight to animate the placeholder.
            highlight = PlaceholderHighlight.fade(
                highlightColor = MaterialTheme.colorScheme.surface,
                // The placeholder modifier provides a reasonable default animation spec, but here
                // we use our custom animation spec so that each of the items has different offset
                // in order to achieve the "stagger" effect.
                animationSpec = infiniteRepeatable(
                    animation = keyframes {
                        durationMillis = 800
                        0f at 0
                        0f at 200
                        1f at 800 with FastOutSlowInEasing
                    },
                    repeatMode = RepeatMode.Reverse,
                    // Offset the animation play time.
                    initialStartOffset = StartOffset(
                        offsetType = StartOffsetType.FastForward,
                        offsetMillis = animationOffsetMillis
                    )
                )
            ),
            // Specify how the placeholder should fade out.
            placeholderFadeTransitionSpec = { tween(durationMillis = 200) },
            // Specify how the content should fade in.
            contentFadeTransitionSpec = { tween(durationMillis = 200) }
        )

        Image(
            painter = if (cheese == null) emptyPainter else painterResource(cheese.image),
            contentDescription = null,
            modifier = Modifier
                .padding(16.dp)
                .size(48.dp)
                .clip(CircleShape)
                // Apply the placeholder modifier after other modifiers.
                .then(placeholderModifier),
            contentScale = ContentScale.Crop
        )
        Text(
            text = cheese?.name ?: "",
            style = MaterialTheme.typography.titleLarge,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 32.dp)
                // Apply the placeholder modifier after other modifiers.
                .then(placeholderModifier)
        )
    }
}

private val emptyPainter = object : Painter() {

    override val intrinsicSize = Size.Unspecified

    override fun DrawScope.onDraw() {
        // Nothing
    }
}
