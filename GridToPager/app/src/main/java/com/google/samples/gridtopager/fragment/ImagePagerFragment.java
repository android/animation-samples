/*
 * Copyright 2018 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.samples.gridtopager.fragment;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.core.app.SharedElementCallback;
import androidx.viewpager.widget.ViewPager;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.samples.gridtopager.MainActivity;
import com.google.samples.gridtopager.adapter.ImagePagerAdapter;
import com.google.samples.gridtopager.R;
import java.util.List;
import java.util.Map;

/**
 * A fragment for displaying a pager of images.
 */
public class ImagePagerFragment extends Fragment {

  private ViewPager viewPager;

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    viewPager = (ViewPager) inflater.inflate(R.layout.fragment_pager, container, false);
    viewPager.setAdapter(new ImagePagerAdapter(this));
    // Set the current position and add a listener that will update the selection coordinator when
    // paging the images.
    viewPager.setCurrentItem(MainActivity.currentPosition);
    viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
      @Override
      public void onPageSelected(int position) {
        MainActivity.currentPosition = position;
      }
    });

    prepareSharedElementTransition();

    // Avoid a postponeEnterTransition on orientation change, and postpone only of first creation.
    if (savedInstanceState == null) {
      postponeEnterTransition();
    }

    return viewPager;
  }

  /**
   * Prepares the shared element transition from and back to the grid fragment.
   */
  private void prepareSharedElementTransition() {
    Transition transition =
        TransitionInflater.from(getContext())
            .inflateTransition(R.transition.image_shared_element_transition);
    setSharedElementEnterTransition(transition);

    // A similar mapping is set at the GridFragment with a setExitSharedElementCallback.
    setEnterSharedElementCallback(
        new SharedElementCallback() {
          @Override
          public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
            // Locate the image view at the primary fragment (the ImageFragment that is currently
            // visible). To locate the fragment, call instantiateItem with the selection position.
            // At this stage, the method will simply return the fragment at the position and will
            // not create a new one.
            Fragment currentFragment = (Fragment) viewPager.getAdapter()
                .instantiateItem(viewPager, MainActivity.currentPosition);
            View view = currentFragment.getView();
            if (view == null) {
              return;
            }

            // Map the first shared element name to the child ImageView.
            sharedElements.put(names.get(0), view.findViewById(R.id.image));
          }
        });
  }
}
