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

package com.example.android.compose.motion.demo.sharedtransform

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.datasource.LoremIpsum
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.example.android.compose.motion.R
import com.example.android.compose.motion.demo.Demo
import com.example.android.compose.motion.demo.SimpleScaffold
import com.example.android.compose.motion.demo.fadethrough.fadeThrough
import com.example.android.compose.motion.ui.MotionComposeTheme

@Composable
fun SharedTransformDemo() {
    SimpleScaffold(title = Demo.SharedTransform.title) {
        DemoCard(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 32.dp)
                .widthIn(max = 384.dp)
                .fillMaxWidth()
        )
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun DemoCard(
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        tonalElevation = 2.dp,
        shape = RoundedCornerShape(16.dp),
    ) {
        // The content of this card is laid out by this ConstraintLayout.
        ConstraintLayout {
            // The card is either expanded or collapsed.
            var expanded by rememberSaveable { mutableStateOf(false) }

            // The ConstraintLayout has 4 constrained elements. They animate separately during the
            // animation, except for the icon that is shared in both the expanded and the
            // collapsed states.
            val (content, icon, divider, button) = createRefs()

            // This transition object coordinates different kinds of animations.
            val transition = updateTransition(targetState = expanded, label = "card")

            // This is the main content of the card.
            // By using the AnimatedContent composable as an extension function of the transition
            // object, the animation runs in sync with other animations of the transition.
            // The height of this element animates on the state change (SizeTransform), and the
            // ConstraintLayout can lay out its children based on the constraints continuously
            // during the animation.
            transition.AnimatedContent(
                // We use the fade-through effect for elements that change between the states.
                transitionSpec = fadeThrough(),
                modifier = Modifier.constrainAs(content) {
                    top.linkTo(parent.top, margin = 16.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
            ) { targetExpanded ->
                CardContent(expanded = targetExpanded)
            }

            // The icon is shared between the expanded and collapsed states.
            CardIcon(
                modifier = Modifier.constrainAs(icon) {
                    top.linkTo(parent.top, margin = 16.dp)
                    end.linkTo(parent.end, margin = 16.dp)
                }
            )

            // The divider becomes transparent in the collapsed state.
            val dividerColor by transition.animateColor(label = "divider color") { targetExpanded ->
                if (targetExpanded) {
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                } else {
                    Color.Transparent
                }
            }
            Divider(
                modifier = Modifier.constrainAs(divider) {
                    top.linkTo(content.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                },
                color = dividerColor
            )

            // The expand/collapse button is shared between the expanded and collapsed states.
            TextButton(
                onClick = { expanded = !expanded },
                modifier = Modifier.constrainAs(button) {
                    top.linkTo(divider.bottom, margin = 8.dp)
                    start.linkTo(parent.start, margin = 8.dp)
                    // The button is constrained to the bottom of the parent so that it remains
                    // visible during the animations.
                    bottom.linkTo(parent.bottom, margin = 8.dp)
                }
            ) {
                // The AnimatedContent extension function can be used for any descendant elements,
                // not just direct children.
                transition.AnimatedContent(transitionSpec = fadeThrough()) { targetExpanded ->
                    Text(text = if (targetExpanded) "COLLAPSE" else "EXPAND")
                }
            }
        }
    }
}

private val CheeseImages = listOf(
    R.drawable.cheese_1 to "Cheese 1",
    R.drawable.cheese_2 to "Cheese 2",
    R.drawable.cheese_3 to "Cheese 3",
    R.drawable.cheese_4 to "Cheese 4",
    R.drawable.cheese_5 to "Cheese 5"
)

@Composable
private fun CardContent(expanded: Boolean, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        if (expanded) {
            ContentTitle(modifier = Modifier.padding(horizontal = 16.dp))
            ContentMaker(
                modifier = Modifier.padding(top = 8.dp, start = 16.dp, end = 16.dp, bottom = 16.dp)
            )
            ContentImagesRow(images = CheeseImages.subList(0, 2))
            Spacer(modifier = Modifier.height(1.dp))
            ContentImagesRow(images = CheeseImages.subList(2, 5))
            ContentBody(maxLines = 2, modifier = Modifier.padding(16.dp))
        } else {
            ContentMaker(modifier = Modifier.padding(horizontal = 16.dp))
            ContentTitle(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
            ContentBody(maxLines = 1, Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp))
            ContentImagesRow(images = CheeseImages)
        }
    }
}

@Composable
private fun ContentTitle(modifier: Modifier = Modifier) {
    Text(
        text = "Cheeses",
        modifier = modifier,
        style = MaterialTheme.typography.titleLarge
    )
}

@Composable
private fun ContentMaker(modifier: Modifier = Modifier) {
    Text(
        text = "Maker: Android Cheese",
        modifier = modifier,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
    )
}

@Composable
private fun CardIcon(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(R.drawable.cheese_1),
        contentDescription = null,
        modifier = modifier
            .size(48.dp)
            .clip(CircleShape)
    )
}

@Composable
private fun ContentImagesRow(images: List<Pair<Int, String?>>, modifier: Modifier = Modifier) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(1.dp)) {
        for ((resourceId, contentDescription) in images) {
            Image(
                painter = painterResource(resourceId),
                contentDescription = contentDescription,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1f)
            )
        }
    }
}

@Composable
private fun ContentBody(maxLines: Int, modifier: Modifier = Modifier) {
    Text(
        text = LoremIpsum(32).values.joinToString(" ").replace('\n', ' '),
        modifier = modifier,
        style = MaterialTheme.typography.bodyMedium,
        maxLines = maxLines,
        overflow = TextOverflow.Ellipsis,
        textAlign = TextAlign.Justify,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
    )
}

@Preview(showBackground = true)
@Composable
private fun PreviewExpandedContent() {
    MotionComposeTheme {
        SharedTransformDemo()
    }
}
