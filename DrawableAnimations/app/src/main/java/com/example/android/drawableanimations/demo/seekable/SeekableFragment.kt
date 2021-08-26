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

package com.example.android.drawableanimations.demo.seekable

import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import androidx.vectordrawable.graphics.drawable.SeekableAnimatedVectorDrawable
import com.example.android.drawableanimations.R
import com.example.android.drawableanimations.databinding.SeekableFragmentBinding
import com.example.android.drawableanimations.viewBindings

class SeekableFragment : Fragment(R.layout.seekable_fragment) {

    private val binding by viewBindings(SeekableFragmentBinding::bind)

    override fun onDestroyView() {
        super.onDestroyView()
        (binding.icon.drawable as? AnimatedVectorDrawableCompat)?.clearAnimationCallbacks()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // You can use the same XML format of <animated-vector> to inflate a
        // SeekableAnimatedVectorDrawable.
        val icon = SeekableAnimatedVectorDrawable.create(
            requireContext(),
            R.drawable.ic_hourglass_animated
        )!!
        // SeekableAnimatedVectorDrawable offers more callback events including pause/resume and
        // update.
        icon.registerAnimationCallback(object : SeekableAnimatedVectorDrawable.AnimationCallback() {
            override fun onAnimationStart(drawable: SeekableAnimatedVectorDrawable) {
                binding.start.setText(R.string.pause)
                binding.stop.isEnabled = true
            }

            override fun onAnimationPause(drawable: SeekableAnimatedVectorDrawable) {
                binding.start.setText(R.string.resume)
            }

            override fun onAnimationResume(drawable: SeekableAnimatedVectorDrawable) {
                binding.start.setText(R.string.pause)
            }

            override fun onAnimationEnd(drawable: SeekableAnimatedVectorDrawable) {
                binding.start.setText(R.string.start)
                binding.stop.isEnabled = false
            }

            override fun onAnimationUpdate(drawable: SeekableAnimatedVectorDrawable) {
                binding.seek.progress = (binding.seek.max * (drawable.currentPlayTime.toFloat() /
                        drawable.totalDuration.toFloat())).toInt()
            }
        })
        binding.icon.setImageDrawable(icon)
        binding.start.setOnClickListener {
            when {
                // You can pause and resume SeekableAnimatedVectorDrawable.
                icon.isPaused -> icon.resume()
                icon.isRunning -> icon.pause()
                else -> icon.start()
            }
        }
        binding.stop.setOnClickListener { icon.stop() }
        binding.seek.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    // With SeekableAnimatedVectorDrawable#setCurrentPlayTime, you can set the
                    // position of animation to the specific time in its duration.
                    icon.currentPlayTime = (icon.totalDuration *
                            (progress.toFloat() / seekBar.max.toFloat())).toLong()
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                // Do nothing.
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                // Do nothing.
            }
        })
    }
}
