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

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.android.compose.motion.demo.Cheese
import kotlinx.coroutines.delay

class CheeseDataSource : PagingSource<Int, Cheese>() {

    override fun getRefreshKey(state: PagingState<Int, Cheese>): Int? {
        return state.anchorPosition
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Cheese> {
        return params.key.let { position ->
            val all = Cheese.all()
            if (position == null) {
                LoadResult.Page(
                    emptyList(),
                    prevKey = null,
                    nextKey = 0,
                    itemsBefore = 0,
                    itemsAfter = all.size
                )
            } else {
                // Simulate slow network.
                delay(3000)
                LoadResult.Page(
                    all.subList(
                        position,
                        (position + params.loadSize).coerceAtMost(all.size)
                    ),
                    prevKey = (position - params.loadSize).orNullIf { it < 0 },
                    nextKey = (position + params.loadSize).orNullIf { it >= all.size },
                    itemsBefore = position,
                    itemsAfter = (all.size - position - params.loadSize).coerceAtLeast(0)
                )
            }
        }
    }
}

private fun Int.orNullIf(condition: (Int) -> Boolean): Int? {
    return if (condition(this)) null else this
}
