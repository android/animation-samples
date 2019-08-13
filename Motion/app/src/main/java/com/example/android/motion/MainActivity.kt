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

package com.example.android.motion

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.commitNow
import com.example.android.motion.ui.EdgeToEdge
import com.example.android.motion.ui.demolist.DemoListFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Configure edge-to-edge display.
        EdgeToEdge.setUpRoot(findViewById(R.id.main))
        EdgeToEdge.setUpAppBar(findViewById(R.id.app_bar), toolbar)

        // Set up the fragment.
        if (savedInstanceState == null) {
            supportFragmentManager.commitNow {
                replace(R.id.container, DemoListFragment())
            }
        }
    }
}
