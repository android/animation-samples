/*
 * Copyright 2016 Google Inc. All Rights Reserved.
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

package com.google.samples.apps.ourstreets.view;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import androidx.annotation.ColorRes;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.hardware.display.DisplayManagerCompat;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.Patterns;
import android.util.Property;
import android.view.Surface;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.WindowInsets;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;

/**
 * Utilities for view manipulation.
 */
public class ViewUtils {

    public static final Property<FrameLayout, Integer> FOREGROUND_COLOR =
            new Property<FrameLayout, Integer>(Integer.class, "foregroundColor") {

                @Override
                public void set(FrameLayout layout, Integer value) {
                    if (layout.getForeground() instanceof ColorDrawable) {
                        ((ColorDrawable) layout.getForeground().mutate()).setColor(value);
                    } else {
                        layout.setForeground(new ColorDrawable(value));
                    }
                }

                @Override
                public Integer get(FrameLayout layout) {
                    if (layout.getForeground() instanceof ColorDrawable) {
                        return ((ColorDrawable) layout.getForeground()).getColor();
                    } else {
                        return Color.TRANSPARENT;
                    }
                }
            };

    /**
     * Set the status bar color of an activity to a specified value.
     *
     * @param activity The activity to set the colorResId for.
     * @param colorResId The value to use.
     */
    public static void setStatusBarColor(@NonNull Activity activity, @ColorRes int colorResId) {
        //noinspection ConstantConditions
        if (activity == null) {
            return;
        }
        final int backgroundColor = ContextCompat.getColor(activity, colorResId);
        activity.getWindow().setStatusBarColor(backgroundColor);
    }

    /**
     * Sets a text on a {@link TextView}, provided via viewResId, within a parent view.
     * If there's a web url in the tag the text will be converted from Html, respecting tags.
     *
     * @param parent The view's parent.
     * @param viewResId The resource to resolve.
     * @param text The text to set.
     */
    public static void setTextOn(@NonNull View parent, @IdRes int viewResId,
                                 @Nullable CharSequence text) {
        if (TextUtils.isEmpty(text)) {
            text = "";
        }
        View view = parent.findViewById(viewResId);
        if (view instanceof TextView) {
            TextView textView = (TextView) view;
            // Only perform Html conversion if there's actually an Url in the text.
            if (Patterns.WEB_URL.matcher(text).find()) {
                textView.setText(Html.fromHtml(text.toString()));
                textView.setMovementMethod(LinkMovementMethod.getInstance());
            } else {
                textView.setText(text);
            }
        }
    }

    /**
     * Create a simple circular reveal for a given view id within a root view.
     * This reveal will start from the start view's boundaries until it fills the root layout.
     *
     * @param rootView The layout's root.
     * @param startViewId The id of the view to use as animation source.
     * @param interpolator The interpolator to use.
     * @return The created circular reveal.
     */
    @NonNull
    public static Animator createCircularReveal(@NonNull View rootView, @IdRes int startViewId,
                                                @NonNull Interpolator interpolator) {
        final View startView = rootView.findViewById(startViewId);
        return createCircularReveal(startView, rootView, interpolator);
    }

    /**
     * Create a simple circular reveal from a given start view to it's target view.
     * This reveal will start from the start view's boundaries until it fills the target view.
     *
     * @param startView The view to start the reveal from.
     * @param targetView The target view which will be displayed once the reveal is done.
     * @param interpolator The interpolator to use.
     * @return The created circular reveal.
     */
    @NonNull
    public static Animator createCircularReveal(@NonNull View startView, @NonNull View targetView,
                                                @NonNull Interpolator interpolator) {
        Point center = getCenterForView(startView);
        return createCircularReveal(center, startView.getWidth(), targetView, interpolator);
    }

    /**
     * Create a simple circular reveal from a given start view to it's target view.
     * This reveal will start from the start view's boundaries until it fills the target view.
     *
     * @param center The center x and y coordinates of the start circle.
     * @param width The initial width of the view's coordinates.
     * @param targetView The target view which will be displayed once the reveal is done.
     * @param interpolator The interpolator to use.
     * @return The created circular reveal.
     */
    @NonNull
    public static Animator createCircularReveal(@NonNull Point center, int width,
                                                @NonNull View targetView,
                                                @NonNull Interpolator interpolator) {
        final Animator circularReveal = ViewAnimationUtils.createCircularReveal(targetView,
                center.x, center.y, width, (float) Math.hypot(center.x, center.y));
        circularReveal.setInterpolator(interpolator);
        return circularReveal;
    }

    /**
     * Basically a reverse {@link #createCircularReveal(Point, int, View, Interpolator)}.
     *
     * @param center The center x and y coordinates of the final circle.
     * @param width The final width of the view's coordinates.
     * @param startView The view which will initially displayed.
     * @param interpolator The interpolator to use.
     * @return The created circular conceal.
     */
    public static Animator createCircularConceal(@NonNull Point center, int width,
                                                 @NonNull View startView,
                                                 @NonNull Interpolator interpolator) {
        final Animator circularReveal = ViewAnimationUtils.createCircularReveal(startView,
                center.x, center.y, (float) Math.hypot(center.x, center.y), width);
        circularReveal.setInterpolator(interpolator);
        return circularReveal;
    }

    /**
     * Create a color change animation over a foreground property of a {@link FrameLayout}.
     *
     * @param target The target view.
     * @param startColorRes The color to start from.
     * @param targetColorRes The color this animation will end with.
     * @param interpolator The interpolator to use.
     * @return The color change animation.
     */
    @NonNull
    public static ObjectAnimator createColorChange(@NonNull FrameLayout target,
                                                   @ColorRes int startColorRes,
                                                   @ColorRes int targetColorRes,
                                                   @NonNull Interpolator interpolator) {
        Context context = target.getContext();
        final int startColor = ContextCompat.getColor(context, startColorRes);
        final int targetColor = ContextCompat.getColor(context, targetColorRes);
        ObjectAnimator colorChange = ObjectAnimator.ofInt(target,
                ViewUtils.FOREGROUND_COLOR, startColor, targetColor);
        colorChange.setEvaluator(new ArgbEvaluator());
        colorChange.setInterpolator(interpolator);
        colorChange.setDuration(context.getResources()
                .getInteger(android.R.integer.config_longAnimTime));
        return colorChange;
    }

    /**
     * Get the center of a given view.
     *
     * @param view The view to get coordinates from.
     * @return The center of the given view.
     */
    public static Point getCenterForView(@NonNull View view) {
        final int centerX = (view.getLeft() + view.getRight()) / 2;
        final int centerY = (view.getTop() + view.getBottom()) / 2;
        return new Point(centerX, centerY);
    }

    /**
     * Applies top window insets for a view.
     *
     * @param view The view to apply insets for.
     */
    public static void applyTopWindowInsetsForView(@NonNull final View view) {
        view.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {
            @Override
            public WindowInsets onApplyWindowInsets(View v, WindowInsets insets) {
                v.setPadding(v.getPaddingLeft(), insets.getSystemWindowInsetTop()
                        + v.getPaddingTop(), v.getPaddingRight(), v.getPaddingBottom());
                return insets;
            }
        });
        view.requestApplyInsets();
    }

    /**
     * Checks whether the main display is in landscape.
     *
     * @param context The context to check with.
     * @return <code>true</code> if the main display is in landscape, else <code>false</code>.
     */
    public static boolean isMainDisplayInLandscape(Context context) {
        int rotation = DisplayManagerCompat.getInstance(context).getDisplay(0).getRotation();
        return rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270;
    }

    /**
     * Creates a {@link BitmapDescriptor} from  a drawable.
     * This is particularly useful for {@link GoogleMap} {@link Marker}s.
     *
     * @param drawable The drawable that should be a {@link BitmapDescriptor}.
     * @return The created {@link BitmapDescriptor}.
     */
    @NonNull
    public static BitmapDescriptor getBitmapDescriptorFromDrawable(@NonNull Drawable drawable) {
        BitmapDescriptor bitmapDescriptor;
        // Usually the pin could be loaded via BitmapDescriptorFactory directly.
        // The target map_pin is a VectorDrawable which is currently not supported
        // within BitmapDescriptors.
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        drawable.setBounds(0, 0, width, height);
        Bitmap markerBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(markerBitmap);
        drawable.draw(canvas);
        bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(markerBitmap);
        return bitmapDescriptor;
    }
}
