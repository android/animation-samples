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

import android.os.SystemClock
import androidx.paging.DataSource
import androidx.paging.PositionalDataSource
import com.example.android.motion.model.Cheese

class CheeseDataSource : PositionalDataSource<Cheese>() {

    companion object Factory : DataSource.Factory<Int, Cheese>() {
        override fun create(): DataSource<Int, Cheese> = CheeseDataSource()
    }

    override fun loadInitial(params: LoadInitialParams, callback: LoadInitialCallback<Cheese>) {
        // Simulate a slow network.
        SystemClock.sleep(3000L)
        callback.onResult(
            Cheese.ALL.subList(
                params.requestedStartPosition,
                params.requestedStartPosition + params.requestedLoadSize
            ),
            params.requestedStartPosition,
            Cheese.ALL.size
        )
    }

    override fun loadRange(params: LoadRangeParams, callback: LoadRangeCallback<Cheese>) {
        // Simulate a slow network.
        SystemClock.sleep(3000L)
        callback.onResult(
            Cheese.ALL.subList(
                params.startPosition, params.startPosition + params.loadSize
            )
        )
    }
}
