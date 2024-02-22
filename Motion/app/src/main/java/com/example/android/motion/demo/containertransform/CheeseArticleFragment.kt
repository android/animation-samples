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

package com.example.android.motion.demo.containertransform

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.BackEventCompat
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import androidx.core.view.ViewGroupCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.animation.PathInterpolatorCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.android.motion.R
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.transition.MaterialContainerTransform

class CheeseArticleFragment : Fragment() {

    companion object {
        const val TRANSITION_NAME_BACKGROUND = "background"

        private val GestureInterpolator = PathInterpolatorCompat.create(0f, 0f, 0f, 1f)
    }

    private val args: CheeseArticleFragmentArgs by navArgs()

    private val viewModel: CheeseArticleViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // These are the shared element transitions.
        sharedElementEnterTransition = MaterialContainerTransform(requireContext(), true)
        sharedElementReturnTransition = MaterialContainerTransform(requireContext(), false)

        viewModel.cheeseId = args.cheeseId
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.cheese_article_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val toolbar: Toolbar = view.findViewById(R.id.toolbar)
        val name: TextView = view.findViewById(R.id.name)
        val image: ImageView = view.findViewById(R.id.image)
        val scroll: NestedScrollView = view.findViewById(R.id.scroll)
        val content: LinearLayout = view.findViewById(R.id.content)

        val background: FrameLayout = view.findViewById(R.id.background)
        val coordinator: CoordinatorLayout = view.findViewById(R.id.coordinator)

        ViewCompat.setTransitionName(background, TRANSITION_NAME_BACKGROUND)
        ViewGroupCompat.setTransitionGroup(coordinator, true)

        // Adjust the edge-to-edge display.
        ViewCompat.setOnApplyWindowInsetsListener(view) { _, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            toolbar.updateLayoutParams<CollapsingToolbarLayout.LayoutParams> {
                topMargin = systemBars.top
            }
            // The collapsed app bar gets taller by the toolbar's top margin. The CoordinatorLayout
            // has to have a bottom margin of the same amount so that the scrolling content is
            // completely visible.
            scroll.updateLayoutParams<CoordinatorLayout.LayoutParams> {
                bottomMargin = systemBars.top
            }
            content.updatePadding(
                left = systemBars.left,
                right = systemBars.right,
                bottom = systemBars.bottom
            )
            insets
        }

        viewModel.cheese.observe(viewLifecycleOwner) { cheese ->
            if (cheese != null) {
                name.text = cheese.name
                image.setImageResource(cheese.image)
            }
        }

        toolbar.setNavigationOnClickListener { v ->
            v.findNavController().popBackStack()
        }

        val predictiveBackMargin = resources.getDimensionPixelSize(R.dimen.predictive_back_margin)
        var initialTouchY = -1f
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    // This invokes the sharedElementReturnTransition, which is
                    // MaterialContainerTransform.
                    findNavController().popBackStack()
                }

                override fun handleOnBackProgressed(backEvent: BackEventCompat) {
                    val progress = GestureInterpolator.getInterpolation(backEvent.progress)
                    if (initialTouchY < 0f) {
                        initialTouchY = backEvent.touchY
                    }
                    val progressY = GestureInterpolator.getInterpolation(
                        (backEvent.touchY - initialTouchY) / background.height
                    )

                    // See the motion spec about the calculations below.
                    // https://developer.android.com/design/ui/mobile/guides/patterns/predictive-back#motion-specs

                    // Shift horizontally.
                    val maxTranslationX = (background.width / 20) - predictiveBackMargin
                    background.translationX = progress * maxTranslationX *
                        (if (backEvent.swipeEdge == BackEventCompat.EDGE_LEFT) 1 else -1)

                    // Shift vertically.
                    val maxTranslationY = (background.height / 20) - predictiveBackMargin
                    background.translationY = progressY * maxTranslationY

                    // Scale down from 100% to 90%.
                    val scale = 1f - (0.1f * progress)
                    background.scaleX = scale
                    background.scaleY = scale
                }

                override fun handleOnBackCancelled() {
                    initialTouchY = -1f
                    background.run {
                        translationX = 0f
                        translationY = 0f
                        scaleX = 1f
                        scaleY = 1f
                    }
                }
            }
        )
    }
}
