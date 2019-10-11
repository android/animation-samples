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

package com.example.android.motion.demo.bottomsheet

import android.content.res.ColorStateList
import android.content.res.Resources
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import androidx.core.view.doOnLayout
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import com.example.android.motion.R
import com.example.android.motion.ui.EdgeToEdge
import com.google.android.material.animation.ArgbEvaluatorCompat
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.floatingactionbutton.FloatingActionButton

private const val STATE_BOTTOM_SHEET = "bottom_sheet"

/**
 * Demonstrates how to "animate" bottom sheet contents. This does not actually use any Animation
 * API, but it just modifies view properties as the bottom sheet moves.
 */
class BottomSheetActivity : AppCompatActivity() {

    private lateinit var behavior: BottomSheetBehavior<View>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.bottom_sheet_activity)

        // View references.
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        val content: ViewGroup = findViewById(R.id.content)
        val bottomSheet: LinearLayout = findViewById(R.id.bottom_sheet)
        behavior = BottomSheetBehavior.from(bottomSheet)
        val header: ViewGroup = findViewById(R.id.bottom_sheet_header)
        val headerTexts: List<TextView> = listOf(
            findViewById(R.id.bottom_sheet_title),
            findViewById(R.id.bottom_sheet_subtitle)
        )
        val body: ViewGroup = findViewById(R.id.bottom_sheet_body)
        val fab: FloatingActionButton = findViewById(R.id.fab)

        // Cache some theme colors for later use.
        val theme = theme
        val typedValue = TypedValue()
        val colorSurface = theme.getColor(R.attr.colorSurface, typedValue)
        val colorPrimary = theme.getColor(R.attr.colorPrimary, typedValue)
        val colorSecondary = theme.getColor(R.attr.colorSecondary, typedValue)
        val colorOnSurface = theme.getColor(R.attr.colorOnSurface, typedValue)
        val colorOnPrimary = theme.getColor(R.attr.colorOnPrimary, typedValue)
        val colorOnSecondary = theme.getColor(R.attr.colorOnSecondary, typedValue)

        // Set up edge-to-edge display.
        EdgeToEdge.setUpRoot(findViewById(R.id.coordinator))
        EdgeToEdge.setUpAppBar(findViewById(R.id.app_bar), toolbar)
        EdgeToEdge.setUpScrollingContent(findViewById(R.id.content))
        header.doOnLayout {
            // The initial height of the bottom sheet should align with the height of its header.
            behavior.peekHeight = header.height
            val fabMargin = resources.getDimensionPixelSize(R.dimen.spacing_medium)
            ViewCompat.setOnApplyWindowInsetsListener(bottomSheet) { _, insets ->
                behavior.peekHeight = insets.systemWindowInsetBottom + header.height
                bottomSheet.updatePadding(bottom = insets.systemWindowInsetBottom)
                body.updatePadding(
                    left = insets.systemWindowInsetLeft,
                    right = insets.systemWindowInsetRight
                )
                header.updatePadding(
                    left = insets.systemWindowInsetLeft,
                    right = insets.systemWindowInsetRight
                )
                fab.updateLayoutParams<CoordinatorLayout.LayoutParams> {
                    leftMargin = fabMargin + insets.systemWindowInsetLeft
                    rightMargin = fabMargin + insets.systemWindowInsetRight
                }
                insets
            }
            ViewCompat.requestApplyInsets(bottomSheet)
        }

        // Restore states.
        if (savedInstanceState != null) {
            // The bottom sheet can restore its own states, but we have to restore the states of
            // its content.
            val state = savedInstanceState.getInt(STATE_BOTTOM_SHEET)
            if (state == BottomSheetBehavior.STATE_EXPANDED) {
                header.setBackgroundColor(colorPrimary)
                headerTexts.forEach { it.setTextColor(colorOnPrimary) }
                fab.supportImageTintList = ColorStateList.valueOf(colorPrimary)
                fab.supportBackgroundTintList = ColorStateList.valueOf(colorSurface)
                body.alpha = 1f
            }
        }

        // Bind events.
        header.setOnClickListener {
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
        content.setOnClickListener {
            behavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        // Monitor movement of the bottom sheet and change the appearance of its content.
        val evaluator = ArgbEvaluatorCompat()
        behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                // This is called every frame while the bottom sheet is moving. We can simply
                // modify the view properties without using any Animation API.

                header.setBackgroundColor(
                    evaluator.evaluate(slideOffset, colorSurface, colorPrimary)
                )

                val textColor = evaluator.evaluate(slideOffset, colorOnSurface, colorOnPrimary)
                headerTexts.forEach { it.setTextColor(textColor) }

                fab.supportImageTintList = ColorStateList.valueOf(
                    evaluator.evaluate(slideOffset, colorOnSecondary, colorPrimary)
                )
                fab.supportBackgroundTintList = ColorStateList.valueOf(
                    evaluator.evaluate(slideOffset, colorSecondary, colorSurface)
                )

                // Fade out the body when the sheet is collapsed. We don't want to show it behind
                // the system navigation bar.
                body.alpha = slideOffset
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_EXPANDED -> fab.rippleColor = colorPrimary
                    BottomSheetBehavior.STATE_COLLAPSED -> fab.rippleColor = colorOnSecondary
                    else -> Unit
                }
            }
        })
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(STATE_BOTTOM_SHEET, behavior.state)
    }

    override fun onBackPressed() {
        if (behavior.state == BottomSheetBehavior.STATE_EXPANDED) {
            behavior.state = BottomSheetBehavior.STATE_COLLAPSED
        } else {
            super.onBackPressed()
        }
    }
}

@ColorInt
private fun Resources.Theme.getColor(
    @AttrRes attrId: Int,
    reused: TypedValue = TypedValue()
): Int {
    resolveAttribute(attrId, reused, true)
    return reused.data
}
