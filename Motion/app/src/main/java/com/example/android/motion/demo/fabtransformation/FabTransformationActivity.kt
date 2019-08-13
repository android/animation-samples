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

package com.example.android.motion.demo.fabtransformation

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.observe
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.example.android.motion.R
import com.example.android.motion.ui.EdgeToEdge
import com.google.android.material.circularreveal.cardview.CircularRevealCardView
import com.google.android.material.floatingactionbutton.FloatingActionButton

/**
 * FAB transformation is provided by Material Components. This activity demonstrates how to use
 * [FloatingActionButton.setExpanded] to transform the FAB into a sheet.
 */
class FabTransformationActivity : AppCompatActivity() {

    private val viewModel: FabTransformationViewModel by viewModels()

    private lateinit var fab: FloatingActionButton
    private lateinit var message: TextView

    private class CheeseItemHolder(val parent: LinearLayout, listener: View.OnClickListener) {

        val image: ImageView = parent.findViewById(R.id.image)
        val name: TextView = parent.findViewById(R.id.name)

        init {
            parent.setOnClickListener(listener)
        }
    }

    private val cheeseOnClick = View.OnClickListener { v ->
        val name = v.getTag(R.id.tag_name) as String
        message.text = getString(R.string.you_selected, name)
        fab.isExpanded = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // See the layout file for how to set up the CoordinatorLayout behaviors.
        setContentView(R.layout.fab_transformation_activity)
        val root: CoordinatorLayout = findViewById(R.id.root)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        val sheet: CircularRevealCardView = findViewById(R.id.sheet)
        val scrim: View = findViewById(R.id.scrim)
        val cheeseHolders: List<CheeseItemHolder> = listOf(
            CheeseItemHolder(findViewById(R.id.cheese_1), cheeseOnClick),
            CheeseItemHolder(findViewById(R.id.cheese_2), cheeseOnClick),
            CheeseItemHolder(findViewById(R.id.cheese_3), cheeseOnClick),
            CheeseItemHolder(findViewById(R.id.cheese_4), cheeseOnClick)
        )
        message = findViewById(R.id.message)
        fab = findViewById(R.id.fab)
        setSupportActionBar(toolbar)

        // Set up edge-to-edge display.
        EdgeToEdge.setUpRoot(root)
        EdgeToEdge.setUpAppBar(findViewById(R.id.app_bar), toolbar)
        val fabMargin = resources.getDimensionPixelSize(R.dimen.spacing_medium)
        ViewCompat.setOnApplyWindowInsetsListener(root) { _, insets ->
            fab.updateLayoutParams<CoordinatorLayout.LayoutParams> {
                leftMargin = fabMargin + insets.systemWindowInsetLeft
                rightMargin = fabMargin + insets.systemWindowInsetRight
                bottomMargin = fabMargin + insets.systemWindowInsetBottom
            }
            sheet.updateLayoutParams<CoordinatorLayout.LayoutParams> {
                leftMargin = fabMargin + insets.systemWindowInsetLeft
                rightMargin = fabMargin + insets.systemWindowInsetRight
                bottomMargin = fabMargin + insets.systemWindowInsetBottom
            }
            insets
        }

        // Populate the sheet content.
        viewModel.cheeses.observe(this) { cheeses ->
            cheeseHolders.forEachIndexed { i, holder ->
                if (cheeses.size > i) {
                    val cheese = cheeses[i]
                    holder.parent.isVisible = true
                    holder.parent.setTag(R.id.tag_name, cheese.name)
                    holder.name.text = cheese.name
                    Glide.with(holder.image)
                        .load(cheese.image)
                        .transform(CircleCrop())
                        .into(holder.image)
                } else {
                    holder.parent.isVisible = false
                }
            }
        }

        // Bind events.
        fab.setOnClickListener {
            // Expand the FAB. The CoordinatorLayout transforms the FAB into the view marked with
            // FabTransformationSheetBehavior. Also the view marked with
            // FabTransformationScrimBehavior is faded in as a content scrim.
            fab.isExpanded = true
        }
        scrim.setOnClickListener {
            // Shrink the menu sheet back into the FAB.
            fab.isExpanded = false
        }
    }

    override fun onBackPressed() {
        if (fab.isExpanded) {
            fab.isExpanded = false
        } else {
            super.onBackPressed()
        }
    }
}
