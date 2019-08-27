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

import androidx.annotation.NonNull;
import android.util.Log;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class for data presentation.
 *
 * @param <T> The data type which will be displayed.
 */
abstract class DataPresenter<T> {

    private static final String TAG = "DataPresenter";

    private final Firebase mFirebase;
    private final ValueEventListener mValueEventListener;
    private final List<T> mData;
    private final DataView<T> mDataView;

    /**
     * Creates a data presenter.
     *
     * @param dataView The view which will display the data.
     * @param configUrl The firebase endpoint url.
     */
    DataPresenter(@NonNull DataView<T> dataView, @NonNull String configUrl) {
        mFirebase = new Firebase(configUrl);
        mData = new ArrayList<>();
        mDataView = dataView;

        mValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mData.clear();
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    // Data parsing is being done within the extending classes.
                    mData.add(parseData(data));
                }
                mDataView.showData(mData);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.d(TAG, "onCancelled: " + firebaseError.getMessage());
                // Deliberately swallow the firebase error here.
                mDataView.showError();
            }
        };
    }

    @NonNull
    protected abstract T parseData(DataSnapshot data);

    /**
     * Asynchronously gets data to display.
     * Once the data is available one of {@link DataView}'s methods will be invoked.
     */
    public final void getData() {
        mFirebase.removeEventListener(mValueEventListener);
        mFirebase.addValueEventListener(mValueEventListener);
    }

}
