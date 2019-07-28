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

package com.google.samples.apps.ourstreets.data;

import java.util.List;

/**
 * Informing on data's state.
 */
public interface DataView<T> {

    /**
     * Called when data is ready to be displayed.
     *
     * @param data The data to display.
     */
    void showData(List<T> data);

    /**
     * Called when an error should be displayed.
     */
    void showError();
}
