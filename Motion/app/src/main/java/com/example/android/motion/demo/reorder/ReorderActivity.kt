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

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.lifecycle.observe
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.android.motion.R
import com.example.android.motion.ui.EdgeToEdge
import com.example.android.motion.widget.SpaceDecoration

class ReorderActivity : AppCompatActivity() {

    private val viewModel: ReorderViewModel by viewModels()

    private var pickUpElevation: Float = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.reorder_activity)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        val list: RecyclerView = findViewById(R.id.list)
        setSupportActionBar(toolbar)
        EdgeToEdge.setUpRoot(findViewById(R.id.root))
        EdgeToEdge.setUpAppBar(findViewById(R.id.app_bar), toolbar)
        EdgeToEdge.setUpScrollingContent(list)
        pickUpElevation = resources.getDimensionPixelSize(R.dimen.pick_up_elevation).toFloat()
        list.addItemDecoration(
            SpaceDecoration(resources.getDimensionPixelSize(R.dimen.spacing_small))
        )

        // The ItemTouchHelper handles view drag inside the RecyclerView.
        val itemTouchHelper = ItemTouchHelper(touchHelperCallback)
        itemTouchHelper.attachToRecyclerView(list)

        val adapter = CheeseGridAdapter(onItemLongClick = { holder ->
            // Start dragging the item when it is long-pressed.
            itemTouchHelper.startDrag(holder)
        })
        list.adapter = adapter

        viewModel.cheeses.observe(this) { cheeses ->
            // Every time the items are reordered on the screen, we receive a new list here.
            // The adapter takes a diff between the old and the new lists, and animates any moving
            // items by ItemAnimator.
            adapter.submitList(cheeses)
        }
    }

    private val touchHelperCallback = object : ItemTouchHelper.Callback() {

        override fun getMovementFlags(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder
        ): Int {
            return makeMovementFlags(
                // We allow items to be dragged in any direction.
                ItemTouchHelper.UP
                        or ItemTouchHelper.DOWN
                        or ItemTouchHelper.LEFT
                        or ItemTouchHelper.RIGHT,
                // But not swiped away.
                0
            )
        }

        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            // Reorder the items in the ViewModel. The ViewModel will then notify the UI through the
            // LiveData.
            viewModel.move(viewHolder.adapterPosition, target.adapterPosition)
            return true
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            // Do nothing
        }

        override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
            super.onSelectedChanged(viewHolder, actionState)
            val view = viewHolder?.itemView ?: return
            when (actionState) {
                ItemTouchHelper.ACTION_STATE_DRAG -> {
                    ViewCompat.animate(view).setDuration(150L).translationZ(pickUpElevation)
                }
            }
        }

        override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
            super.clearView(recyclerView, viewHolder)
            ViewCompat.animate(viewHolder.itemView).setDuration(150L).translationZ(0f)
        }

        override fun isLongPressDragEnabled(): Boolean {
            // We handle the long press on our side for better touch feedback.
            return false
        }

        override fun isItemViewSwipeEnabled(): Boolean {
            return false
        }
    }
}
