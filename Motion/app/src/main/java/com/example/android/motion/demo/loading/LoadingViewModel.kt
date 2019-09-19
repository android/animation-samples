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

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import androidx.paging.toLiveData
import com.example.android.motion.model.Cheese

class LoadingViewModel : ViewModel() {

    private var source: LiveData<PagedList<Cheese>>? = null
    private val _cheeses = MediatorLiveData<PagedList<Cheese>>()
    val cheeses: LiveData<PagedList<Cheese>> = _cheeses

    init {
        refresh()
    }

    fun refresh() {
        source?.let { _cheeses.removeSource(it) }
        val s = CheeseDataSource.toLiveData(pageSize = 15)
        source = s
        _cheeses.addSource(s) { _cheeses.postValue(it) }
    }
}
