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

package com.example.android.motion.ui.demolist

import android.app.Application
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.android.motion.model.Demo

class DemoListViewModel(application: Application) : AndroidViewModel(application) {

    private val _demos = MutableLiveData<List<Demo>>()
    val demos: LiveData<List<Demo>> = _demos

    init {
        val packageManager = getApplication<Application>().packageManager
        val resolveInfoList = packageManager.queryIntentActivities(
            Intent(Intent.ACTION_MAIN).addCategory(Demo.CATEGORY),
            PackageManager.GET_META_DATA
        )
        val resources = application.resources
        _demos.value = resolveInfoList.map { resolveInfo ->
            val activityInfo = resolveInfo.activityInfo
            val metaData = activityInfo.metaData
            val apisId = metaData?.getInt(Demo.META_DATA_APIS, 0) ?: 0
            Demo(
                activityInfo.applicationInfo.packageName,
                activityInfo.name,
                activityInfo.loadLabel(packageManager).toString(),
                metaData?.getString(Demo.META_DATA_DESCRIPTION),
                if (apisId == 0) emptyList() else resources.getStringArray(apisId).toList()
            )
        }
    }
}
