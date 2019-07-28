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

package com.google.samples.gridtopager.adapter;

import androidx.annotation.DrawableRes;
import com.google.samples.gridtopager.R;

/**
 * Holds the image resource references used by the grid and the pager fragments.
 */
abstract class ImageData {

  // Image assets (free for commercial use, no attribution required, from pixabay.com)
  @DrawableRes
  static final int[] IMAGE_DRAWABLES = {
      R.drawable.animal_2024172,
      R.drawable.beetle_562035,
      R.drawable.bug_189903,
      R.drawable.butterfly_417971,
      R.drawable.butterfly_dolls_363342,
      R.drawable.dragonfly_122787,
      R.drawable.dragonfly_274059,
      R.drawable.dragonfly_689626,
      R.drawable.grasshopper_279532,
      R.drawable.hover_fly_61682,
      R.drawable.hoverfly_546692,
      R.drawable.insect_278083,
      R.drawable.morpho_43483,
      R.drawable.nature_95365
  };

}
