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

package com.example.android.compose.motion.ui.home

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.android.compose.motion.R
import com.example.android.compose.motion.demo.Demo
import com.example.android.compose.motion.ui.MotionComposeTheme
import com.google.accompanist.flowlayout.FlowRow
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.rememberInsetsPaddingValues

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home(
    onDemoSelected: (demo: Demo) -> Unit
) {
    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = {
                    Text(text = stringResource(R.string.app_name))
                },
                modifier = Modifier
                    .padding(
                        rememberInsetsPaddingValues(
                            insets = LocalWindowInsets.current.systemBars,
                            applyBottom = false
                        )
                    )
            )
        }
    ) {
        DemoList(
            onDemoSelected = onDemoSelected,
            modifier = Modifier
                .fillMaxSize()
        )
    }
}

@Composable
private fun DemoList(
    onDemoSelected: (demo: Demo) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = rememberInsetsPaddingValues(
            insets = LocalWindowInsets.current.systemBars,
            applyTop = false
        )
    ) {
        items(items = Demo.values()) { demo ->
            DemoCard(
                demo = demo,
                onClick = { onDemoSelected(demo) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            )
        }
    }
}

@Composable
private fun DemoCard(
    demo: Demo,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        tonalElevation = 2.dp,
        shape = RoundedCornerShape(16.dp),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = demo.title,
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                text = demo.description,
                style = MaterialTheme.typography.bodyMedium
            )
            FlowRow(mainAxisSpacing = 8.dp) {
                for (api in demo.apis) {
                    ApiChip(api = api)
                }
            }
        }
    }
}

@Composable
private fun ApiChip(api: String) {
    Text(
        text = api,
        modifier = Modifier
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(
                horizontal = 8.dp,
                vertical = 2.dp
            ),
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.primary
    )
}

@Preview
@Composable
private fun PreviewHome() {
    MotionComposeTheme {
        Home(
            onDemoSelected = {}
        )
    }
}
