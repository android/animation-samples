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
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import androidx.core.view.ViewGroupCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.transition.ChangeBounds
import androidx.transition.ChangeImageTransform
import androidx.transition.ChangeTransform
import androidx.transition.Transition
import com.bumptech.glide.Glide
import com.example.android.motion.R
import com.example.android.motion.demo.FAST_OUT_SLOW_IN
import com.example.android.motion.demo.LARGE_COLLAPSE_DURATION
import com.example.android.motion.demo.LARGE_EXPAND_DURATION
import com.example.android.motion.demo.doOnEnd
import com.example.android.motion.demo.plusAssign
import com.example.android.motion.demo.transitionTogether
import com.google.android.material.appbar.CollapsingToolbarLayout
import java.util.concurrent.TimeUnit

/**
 * Shows detail about a cheese.
 */
class CheeseDetailFragment : Fragment() {

    companion object {
        const val TRANSITION_NAME_IMAGE = "image"
        const val TRANSITION_NAME_NAME = "name"
        const val TRANSITION_NAME_TOOLBAR = "toolbar"
        const val TRANSITION_NAME_BACKGROUND = "background"
        const val TRANSITION_NAME_FAVORITE = "favorite"
        const val TRANSITION_NAME_BOOKMARK = "bookmark"
        const val TRANSITION_NAME_SHARE = "share"
        const val TRANSITION_NAME_BODY = "body"
    }

    private val args: CheeseDetailFragmentArgs by navArgs()

    private val viewModel: CheeseDetailViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // These are the shared element transitions.
        sharedElementEnterTransition = createSharedElementTransition(LARGE_EXPAND_DURATION)
        sharedElementReturnTransition = createSharedElementTransition(LARGE_COLLAPSE_DURATION)

        viewModel.cheeseId = args.cheeseId
    }

    private fun createSharedElementTransition(duration: Long): Transition {
        return transitionTogether {
            this.duration = duration
            interpolator = FAST_OUT_SLOW_IN
            this += SharedFade()
            this += ChangeImageTransform()
            this += ChangeBounds()
            this += ChangeTransform()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.cheese_detail_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // We are expecting an enter transition from the grid fragment.
        postponeEnterTransition(500L, TimeUnit.MILLISECONDS)

        val toolbar: Toolbar = view.findViewById(R.id.toolbar)
        val dummyName: View = view.findViewById(R.id.dummy_name)
        val name: TextView = view.findViewById(R.id.name)
        val image: ImageView = view.findViewById(R.id.image)
        val scroll: NestedScrollView = view.findViewById(R.id.scroll)
        val content: LinearLayout = view.findViewById(R.id.content)
        val coordinator: CoordinatorLayout = view.findViewById(R.id.detail)
        val favorite: View = view.findViewById(R.id.favorite)
        val bookmark: View = view.findViewById(R.id.bookmark)
        val share: View = view.findViewById(R.id.share)

        // Transition names. Note that they don't need to match with the names of the selected grid
        // item. They only have to be unique in this fragment.
        ViewCompat.setTransitionName(image, TRANSITION_NAME_IMAGE)
        ViewCompat.setTransitionName(dummyName, TRANSITION_NAME_NAME)
        ViewCompat.setTransitionName(toolbar, TRANSITION_NAME_TOOLBAR)
        ViewCompat.setTransitionName(coordinator, TRANSITION_NAME_BACKGROUND)
        ViewCompat.setTransitionName(favorite, TRANSITION_NAME_FAVORITE)
        ViewCompat.setTransitionName(bookmark, TRANSITION_NAME_BOOKMARK)
        ViewCompat.setTransitionName(share, TRANSITION_NAME_SHARE)
        ViewCompat.setTransitionName(scroll, TRANSITION_NAME_BODY)
        ViewGroupCompat.setTransitionGroup(scroll, true)

        // Adjust the edge-to-edge display.
        ViewCompat.setOnApplyWindowInsetsListener(view) { _, insets ->
            toolbar.updateLayoutParams<CollapsingToolbarLayout.LayoutParams> {
                topMargin = insets.systemWindowInsetTop
            }
            content.updatePadding(
                left = insets.systemWindowInsetLeft,
                right = insets.systemWindowInsetRight,
                bottom = insets.systemWindowInsetBottom
            )
            insets
        }

        viewModel.cheese.observe(viewLifecycleOwner) { cheese ->
            if (cheese != null) {
                name.text = cheese.name
                Glide
                    .with(image)
                    .load(cheese.image)
                    // It is important to call `dontTransform` here.
                    // Glide, as well as many other image loading libraries, crops the image before
                    // setting it to an ImageView and caching it. As a result, the image will have
                    // a different aspect ratio than the original image. This is problematic for
                    // `ChangeImageTransform` during shared element transitions because it expects
                    // the image to have the same aspect ratio both on the start and the end states.
                    // `dontTransform` suppresses the cropping.
                    .dontTransform()
                    // We can start the transition when the image is loaded.
                    .doOnEnd(this::startPostponedEnterTransition)
                    .into(image)
            }
        }

        toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }
}
