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

package com.example.android.motion.demo.sharedelement

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.example.android.motion.R
import com.example.android.motion.demo.doOnEnd
import com.example.android.motion.model.Cheese
import com.google.android.material.card.MaterialCardView

private const val STATE_LAST_SELECTED_ID = "last_selected_id"

/**
 * A [RecyclerView.Adapter] for cheeses.
 *
 * This adapter starts the shared element transition. It also handles return transition.
 */
internal class CheeseGridAdapter(
    private val onReadyToTransition: () -> Unit
) : ListAdapter<Cheese, CheeseViewHolder>(Cheese.DIFF_CALLBACK) {

    private var lastSelectedId: Long? = null

    /**
     * `true` if we are expecting a reenter transition from the detail fragment.
     */
    val expectsTransition: Boolean
        get() = lastSelectedId != null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CheeseViewHolder {
        return CheeseViewHolder(parent).apply {
            itemView.setOnClickListener { view ->
                val cheese = getItem(adapterPosition)

                // Record the selected item so that we can make the item ready before starting the
                // reenter transition.
                lastSelectedId = cheese.id

                view.findNavController().navigate(
                    R.id.cheeseDetailFragment,
                    CheeseDetailFragmentArgs(cheese.id).toBundle(),
                    null,
                    FragmentNavigatorExtras(
                        // We expand the card into the background.
                        // The fragment will use this first element as the epicenter of all the
                        // fragment transitions, including Explode for non-shared elements.
                        card to CheeseDetailFragment.TRANSITION_NAME_BACKGROUND,
                        // The image is the focal element in this shared element transition.
                        image to CheeseDetailFragment.TRANSITION_NAME_IMAGE,

                        // These elements are only on the grid item, but they need to be shared
                        // elements so they can be animated with the card. See SharedFade.kt.
                        name to CheeseDetailFragment.TRANSITION_NAME_NAME,
                        favorite to CheeseDetailFragment.TRANSITION_NAME_FAVORITE,
                        bookmark to CheeseDetailFragment.TRANSITION_NAME_BOOKMARK,
                        share to CheeseDetailFragment.TRANSITION_NAME_SHARE,

                        // These elements are only on the detail fragment.
                        toolbar to CheeseDetailFragment.TRANSITION_NAME_TOOLBAR,
                        body to CheeseDetailFragment.TRANSITION_NAME_BODY
                    )
                )
            }
        }
    }

    override fun onBindViewHolder(holder: CheeseViewHolder, position: Int) {
        val cheese = getItem(position)

        // Each of the shared elements has to have a unique transition name, not just in this grid
        // item, but in the entire fragment.
        ViewCompat.setTransitionName(holder.image, "image-${cheese.id}")
        ViewCompat.setTransitionName(holder.name, "name-${cheese.id}")
        ViewCompat.setTransitionName(holder.toolbar, "toolbar-${cheese.id}")
        ViewCompat.setTransitionName(holder.card, "card-${cheese.id}")
        ViewCompat.setTransitionName(holder.favorite, "favorite-${cheese.id}")
        ViewCompat.setTransitionName(holder.bookmark, "bookmark-${cheese.id}")
        ViewCompat.setTransitionName(holder.share, "share-${cheese.id}")
        ViewCompat.setTransitionName(holder.body, "body-${cheese.id}")

        holder.name.text = cheese.name

        // Load the image asynchronously. See CheeseDetailFragment.kt about "dontTransform()"
        var requestBuilder = Glide.with(holder.image).load(cheese.image).dontTransform()
        if (cheese.id == lastSelectedId) {
            requestBuilder = requestBuilder
                .priority(Priority.IMMEDIATE)
                .doOnEnd {
                    // We have loaded the image for the transition destination. It is ready to start
                    // the transition postponed in the fragment.
                    onReadyToTransition()
                    lastSelectedId = null
                }
        }
        requestBuilder.into(holder.image)
    }

    fun saveInstanceState(outState: Bundle) {
        lastSelectedId?.let { id ->
            outState.putLong(STATE_LAST_SELECTED_ID, id)
        }
    }

    fun restoreInstanceState(state: Bundle) {
        if (lastSelectedId == null && state.containsKey(STATE_LAST_SELECTED_ID)) {
            lastSelectedId = state.getLong(STATE_LAST_SELECTED_ID)
        }
    }
}

internal class CheeseViewHolder(
    parent: ViewGroup
) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.cheese_grid_item, parent, false)
) {
    val card: MaterialCardView = itemView.findViewById(R.id.card)
    val image: ImageView = itemView.findViewById(R.id.image)
    val name: TextView = itemView.findViewById(R.id.name)
    val toolbar: MirrorView = itemView.findViewById(R.id.toolbar)
    val favorite: ImageView = itemView.findViewById(R.id.favorite)
    val bookmark: ImageView = itemView.findViewById(R.id.bookmark)
    val share: ImageView = itemView.findViewById(R.id.share)
    val body: MirrorView = itemView.findViewById(R.id.body)
}
