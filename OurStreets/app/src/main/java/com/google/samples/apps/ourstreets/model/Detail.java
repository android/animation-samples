/*
 * Copyright 2015 Google Inc. All Rights Reserved.
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

package com.google.samples.apps.ourstreets.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Stores details of a gallery item.
 */
public class Detail implements Parcelable, ClusterItem {

    private final String mTitle;
    private final String mDescription;
    private final LatLng mLocation;
    private final float mTilt;
    private final float mBearing;

    public Detail(String title, String description, LatLng location, float tilt, float bearing) {
        mTitle = title;
        mDescription = description;
        mLocation = location;
        mTilt = tilt;
        mBearing = bearing;
    }

    @Override
    public LatLng getPosition() {
        return mLocation;
    }

    public String getDescription() {
        return mDescription;
    }

    public String getTitle() {
        return mTitle;
    }

    public float getTilt() {
        return mTilt;
    }

    public float getBearing() {
        return mBearing;
    }

    protected Detail(Parcel in) {
        mTitle = in.readString();
        mDescription = in.readString();
        mLocation = in.readParcelable(Detail.class.getClassLoader());
        mTilt = in.readFloat();
        mBearing = in.readFloat();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(mTitle);
        dest.writeString(mDescription);
        dest.writeParcelable(mLocation, flags);
        dest.writeFloat(mTilt);
        dest.writeFloat(mBearing);
    }

    public static final Creator<Detail> CREATOR = new
            Creator<Detail>() {
                @Override
                public Detail createFromParcel(Parcel in) {
                    return new Detail(in);
                }

                @Override
                public Detail[] newArray(int size) {
                    return new Detail[size];
                }
            };
}
