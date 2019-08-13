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

package com.example.android.motion.demo.stagger

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.observe
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.TransitionManager
import com.example.android.motion.R
import com.example.android.motion.ui.EdgeToEdge

/**
 * Shows a list of items. The items are loaded asynchronously, and they appear with stagger.
 *
 * Stagger refers to applying temporal offsets to a group of elements in sequence, like a list.
 * Stagger creates a cascade effect that focuses attention briefly on each item. It can reveal
 * significant content or highlight affordances within a group.
 *
 * See
 * [Stagger](https://material.io/design/motion/customization.html#sequencing)
 * for the detail.
 */
class StaggerActivity : AppCompatActivity() {

    private val viewModel: CheeseListViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.stagger_activity)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        val list: RecyclerView = findViewById(R.id.list)
        setSupportActionBar(toolbar)
        EdgeToEdge.setUpRoot(findViewById(R.id.root))
        EdgeToEdge.setUpAppBar(findViewById(R.id.app_bar), toolbar)
        EdgeToEdge.setUpScrollingContent(list)

        val adapter = CheeseListAdapter()
        list.adapter = adapter

        // We animate item additions on our side, so disable it in RecyclerView.
        list.itemAnimator = object : DefaultItemAnimator() {
            override fun animateAdd(holder: RecyclerView.ViewHolder?): Boolean {
                dispatchAddFinished(holder)
                dispatchAddStarting(holder)
                return false
            }
        }

        // This is the transition for the stagger effect.
        val stagger = Stagger()

        viewModel.cheeses.observe(this) { cheeses ->
            // Delay the stagger effect until the list is updated.
            TransitionManager.beginDelayedTransition(list, stagger)
            adapter.submitList(cheeses)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.stagger, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_refresh -> {
                // In real-life apps, refresh feature would just overwrite the existing list with
                // the new list. In this demo, we clear the list and repopulate to demonstrate the
                // stagger effect again.
                viewModel.empty()
                viewModel.refresh()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
