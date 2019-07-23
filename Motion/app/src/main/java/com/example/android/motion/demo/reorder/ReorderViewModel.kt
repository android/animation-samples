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

package com.example.android.motion.demo.reorder

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.example.android.motion.model.Cheese

class ReorderViewModel : ViewModel() {

    private val _cheeses = MutableLiveData(Cheese.ALL.toMutableList())
    val cheeses = _cheeses.map { it.toList() }

    fun move(from: Int, to: Int) {
        _cheeses.value?.let { list ->
            val cheese = list.removeAt(from)
            list.add(to, cheese)
            _cheeses.value = list
        }
    }
}
