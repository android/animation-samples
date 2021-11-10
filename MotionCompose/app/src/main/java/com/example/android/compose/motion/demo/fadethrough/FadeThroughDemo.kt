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

package com.example.android.compose.motion.demo.fadethrough

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.android.compose.motion.R
import com.example.android.compose.motion.demo.Demo
import com.example.android.compose.motion.ui.MotionComposeTheme
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.rememberInsetsPaddingValues

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FadeThroughDemo() {
    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = {
                    Text(text = Demo.FadeThrough.title)
                },
                modifier = Modifier
                    .padding(
                        rememberInsetsPaddingValues(
                            insets = LocalWindowInsets.current.systemBars,
                            applyBottom = false
                        )
                    )
            )
        },
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            var expanded by rememberSaveable { mutableStateOf(false) }
            DemoCard(
                expanded = expanded,
                modifier = Modifier.align(Alignment.Center)
            )

            Button(
                onClick = { expanded = !expanded },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(64.dp)
            ) {
                Text(text = "TOGGLE")
            }
        }
    }
}

/**
 * Shows the card. The card can be either expanded or collapsed, and the transition between the two
 * states is animated with the fade-through effect.
 *
 * @param expanded Whether the card is expanded or collapsed.
 */
@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun DemoCard(
    expanded: Boolean,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        tonalElevation = 2.dp,
        shape = RoundedCornerShape(16.dp)
    ) {
        // Use `AnimatedContent` to switch between different content.
        AnimatedContent(
            // `targetState` specifies the input state.
            targetState = expanded,
            // `transitionSpec` defines the behavior of the transition animation.
            transitionSpec = fadeThrough()
        ) { targetExpanded ->
            if (targetExpanded) {
                ExpandedContent()
            } else {
                CollapsedContent()
            }
        }
    }
}

/**
 * Creates a transitionSpec for configuring [AnimatedContent] to the fade through pattern.
 * See [Fade through](https://material.io/design/motion/the-motion-system.html#fade-through) for
 * the motion spec.
 */
@OptIn(ExperimentalAnimationApi::class)
private fun fadeThrough(
    durationMillis: Int = 300
): AnimatedContentScope<Boolean>.() -> ContentTransform {
    return {
        ContentTransform(
            // The initial content fades out.
            initialContentExit = fadeOut(
                animationSpec = tween(
                    // The duration is 3/8 of the overall duration.
                    durationMillis = durationMillis * 3 / 8,
                    // FastOutLinearInEasing is suitable for elements exiting the screen.
                    easing = FastOutLinearInEasing
                )
            ),
            // The target content fades in after the current content fades out.
            targetContentEnter = fadeIn(
                animationSpec = tween(
                    // The duration is 5/8 of the overall duration.
                    durationMillis = durationMillis * 5 / 8,
                    // Delays the EnterTransition by the duration of the ExitTransition.
                    delayMillis = durationMillis * 3 / 8,
                    // LinearOutSlowInEasing is suitable for incoming elements.
                    easing = LinearOutSlowInEasing
                )
            ),
            // The size changes along with the content transition.
            sizeTransform = SizeTransform(
                sizeAnimationSpec = { _, _ ->
                    tween(durationMillis = durationMillis)
                }
            )
        )
    }
}

@Composable
private fun CollapsedContent() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Image(
            painter = painterResource(R.drawable.cheese_1),
            contentDescription = null,
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
        )
        Text(
            text = "Cheese",
            style = MaterialTheme.typography.headlineSmall
        )
    }
}

@Composable
private fun ExpandedContent() {
    Column(
        modifier = Modifier.width(IntrinsicSize.Min)
    ) {
        Image(
            painter = painterResource(R.drawable.cheese_1),
            contentDescription = null,
            modifier = Modifier.size(320.dp, 128.dp),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Cheese",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Text(
            text = "Hello, world",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.lorem_ipsum),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(horizontal = 16.dp),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            TextButton(onClick = { /* Do nothing */ }) {
                Text(text = "DETAIL")
            }
            TextButton(onClick = { /* Do nothing */ }) {
                Text(text = "ORDER")
            }
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = { /* Do nothing */ }) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "Favorite"
                )
            }
            IconButton(onClick = { /* Do nothing */ }) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = "Share"
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Preview
@Composable
private fun PreviewDemoCardCollapsed() {
    MotionComposeTheme {
        DemoCard(expanded = false)
    }
}

@Preview
@Composable
private fun PreviewDemoCardExpanded() {
    MotionComposeTheme {
        DemoCard(expanded = true)
    }
}
