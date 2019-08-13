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

package com.example.android.motion.model

import android.content.ComponentName
import android.content.Intent

data class Demo(
    val packageName: String,
    val name: String,
    val label: String,
    val description: String?,
    val apis: List<String>
) {

    companion object {
        const val CATEGORY = "com.example.android.motion.intent.category.DEMO"
        const val META_DATA_DESCRIPTION = "com.example.android.motion.demo.DESCRIPTION"
        const val META_DATA_APIS = "com.example.android.motion.demo.APIS"
    }

    fun toIntent() = Intent(Intent.ACTION_MAIN)
        .addCategory(CATEGORY)
        .setComponent(ComponentName(packageName, name))
}
