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

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.android.motion.R
import com.example.android.motion.model.Cheese

class CheeseGridAdapter(
    private val onItemLongClick: (holder: RecyclerView.ViewHolder) -> Unit
) : ListAdapter<Cheese, CheeseViewHolder>(Cheese.DIFF_CALLBACK) {

    init {
        setHasStableIds(true)
    }

    override fun getItemId(position: Int): Long {
        return getItem(position).id
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CheeseViewHolder {
        return CheeseViewHolder(parent).apply {
            itemView.setOnLongClickListener {
                onItemLongClick(this)
                true
            }
            itemView.setOnClickListener { v ->
                val cheese = getItem(adapterPosition)
                val context = v.context
                Toast.makeText(
                    context,
                    context.getString(R.string.drag_hint, cheese.name),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onBindViewHolder(holder: CheeseViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class CheeseViewHolder(
    parent: ViewGroup
) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context)
        .inflate(R.layout.cheese_staggered_grid_item, parent, false)
) {

    private val constraintLayout: ConstraintLayout = itemView.findViewById(R.id.cheese)
    private val image: ImageView = itemView.findViewById(R.id.image)
    private val name: TextView = itemView.findViewById(R.id.name)
    private val constraintSet = ConstraintSet().apply { clone(constraintLayout) }

    fun bind(cheese: Cheese) {
        // The image loaded asynchronously, but the aspect ratio should be set synchronously.
        constraintSet.setDimensionRatio(R.id.image, "H,${cheese.imageWidth}:${cheese.imageHeight}")
        constraintSet.applyTo(constraintLayout)

        // Load the image.
        Glide.with(image).load(cheese.image).into(image)
        name.text = cheese.name
    }
}
