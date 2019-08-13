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

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View

/**
 * Takes another view as a substance and draws its content.
 *
 * This is useful for copying an appearance of another view without spending the cost of full
 * instantiation.
 *
 * @see SharedFade
 */
class MirrorView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    init {
        setWillNotDraw(true)
    }

    private var _substance: View? = null
    var substance: View?
        get() = _substance
        set(value) {
            _substance = value
            setWillNotDraw(value == null)
        }

    override fun onDraw(canvas: Canvas?) {
        _substance?.draw(canvas)
    }
}
