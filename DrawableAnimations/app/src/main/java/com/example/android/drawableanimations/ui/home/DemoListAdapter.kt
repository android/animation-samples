/*
 * Copyright 2020 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.drawableanimations.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.drawableanimations.R
import com.example.android.drawableanimations.databinding.DemoListItemBinding
import com.example.android.drawableanimations.demo.Demo

internal class DemoListAdapter(
    private val demoClicked: (Demo) -> Unit
) : ListAdapter<Demo, DemoViewHolder>(DIFF_CALLBACK) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DemoViewHolder {
        return DemoViewHolder(LayoutInflater.from(parent.context), parent).apply {
            itemView.setOnClickListener {
                demoClicked(getItem(bindingAdapterPosition))
            }
        }
    }

    override fun onBindViewHolder(holder: DemoViewHolder, position: Int) {
        holder.binding.title.text = getItem(position).title
    }
}

internal val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Demo>() {

    override fun areItemsTheSame(oldItem: Demo, newItem: Demo): Boolean {
        return oldItem.title == newItem.title
    }

    override fun areContentsTheSame(oldItem: Demo, newItem: Demo): Boolean {
        return oldItem == newItem
    }
}

internal class DemoViewHolder(
    inflater: LayoutInflater,
    parent: ViewGroup
) : RecyclerView.ViewHolder(
    inflater.inflate(R.layout.demo_list_item, parent, false)
) {
    val binding: DemoListItemBinding = DemoListItemBinding.bind(itemView)
}
