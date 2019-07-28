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

package com.google.samples.apps.ourstreets.fragment;

import com.google.samples.apps.ourstreets.activity.MainActivity;

/**
 * Enables fragments to listen to {@link MainActivity#onBackPressed()}.
 * Fragments implementing BackStackAware must pop themselves of the back stack
 * after they're finished with their work.
 */
public interface BackPressAware {

    /**
     * Called when back is pressed.
     */
    void onBackPressed();
}
