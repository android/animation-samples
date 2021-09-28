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

package com.example.android.motion.demo.loading

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.android.motion.model.Cheese
import kotlinx.coroutines.delay

class CheeseDataSource : PagingSource<Int, Cheese>() {

    override fun getRefreshKey(state: PagingState<Int, Cheese>): Int? {
        return state.anchorPosition
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Cheese> {
        return params.key.let { position ->
            if (position == null) {
                LoadResult.Page(
                    emptyList(),
                    prevKey = null,
                    nextKey = 0,
                    itemsBefore = 0,
                    itemsAfter = Cheese.ALL.size
                )
            } else {
                // Simulate slow network.
                delay(3000)
                LoadResult.Page(
                    Cheese.ALL.subList(
                        position,
                        (position + params.loadSize).coerceAtMost(Cheese.ALL.size)
                    ),
                    prevKey = (position - params.loadSize).orNullIf { it < 0 },
                    nextKey = (position + params.loadSize).orNullIf { it >= Cheese.ALL.size },
                    itemsBefore = position,
                    itemsAfter = Cheese.ALL.size - position
                )
            }
        }
    }
}

private fun Int.orNullIf(condition: (Int) -> Boolean): Int? {
    return if (condition(this)) null else this
}
