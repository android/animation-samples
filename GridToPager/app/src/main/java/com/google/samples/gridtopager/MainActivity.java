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
package com.google.samples.gridtopager;

import android.os.Bundle;

import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AppCompatActivity;
import com.google.samples.gridtopager.fragment.GridFragment;

/**
 * Grid to pager app's main activity.
 */
public class MainActivity extends AppCompatActivity {

  /**
   * Holds the current image position to be shared between the grid and the pager fragments. This
   * position updated when a grid item is clicked, or when paging the pager.
   *
   * In this demo app, the position always points to an image index at the {@link
   * com.google.samples.gridtopager.adapter.ImageData} class.
   */
  public static int currentPosition;
  private static final String KEY_CURRENT_POSITION = "com.google.samples.gridtopager.key.currentPosition";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    if (savedInstanceState != null) {
      currentPosition = savedInstanceState.getInt(KEY_CURRENT_POSITION, 0);
      // Return here to prevent adding additional GridFragments when changing orientation.
      return;
    }
    FragmentManager fragmentManager = getSupportFragmentManager();
    fragmentManager
        .beginTransaction()
        .add(R.id.fragment_container, new GridFragment(), GridFragment.class.getSimpleName())
        .commit();
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putInt(KEY_CURRENT_POSITION, currentPosition);
  }
}
