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

package com.example.android.drawableanimations.demo.animated

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.vectordrawable.graphics.drawable.Animatable2Compat
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import com.example.android.drawableanimations.R
import com.example.android.drawableanimations.databinding.AnimatedFragmentBinding
import com.example.android.drawableanimations.viewBindings

class AnimatedFragment : Fragment(R.layout.animated_fragment) {

    private val binding by viewBindings(AnimatedFragmentBinding::bind)
    override fun onAttach(context: Context) {
        super.onAttach(context)
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(
            true // default to enabled
        ) {
            override fun handleOnBackPressed() {
               binding.stop.performClick()
                activity?.supportFragmentManager?.popBackStack()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(
            this,  // LifecycleOwner
            callback
        )
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val icon = AnimatedVectorDrawableCompat.create(
            requireContext(),
            R.drawable.ic_hourglass_animated
        )!!
        icon.registerAnimationCallback(object: Animatable2Compat.AnimationCallback() {
            override fun onAnimationStart(drawable: Drawable?) {
                binding.start.isEnabled = false
                binding.stop.isEnabled = true
            }

            override fun onAnimationEnd(drawable: Drawable?) {
                binding.start.isEnabled = true
                binding.stop.isEnabled = false
            }
        })
        binding.icon.setImageDrawable(icon)
        binding.start.setOnClickListener { icon.start() }
        binding.stop.setOnClickListener { icon.stop() }
    }
}
